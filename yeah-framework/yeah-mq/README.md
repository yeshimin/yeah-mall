# YeahBoot MQ

`yeah-mq` 提供 `MqPublisher`、`MqListener`、`MqMessage` 通用接口，当前实现使用 Redis Stream 消费者组。

`yeah-boot.mq.impl` 当前仅支持 `redis`。Redis 相关组件按该配置条件装配；未配置时默认启用 Redis，配置为尚未支持的实现会在启动阶段直接报错，不会静默回退。

## Pending 恢复

正常消息处理成功后，在 `auto-ack=true` 时执行 ACK；处理异常时不 ACK，由独立扫描线程恢复超过最小空闲时间的 Pending 消息。

- Redis 6.2 及以上优先使用 `XAUTOCLAIM`。
- Redis 5.0 或客户端不支持 `XAUTOCLAIM` 时，自动回退到 `XPENDING + XCLAIM`。
- 每个应用实例和监听器使用唯一消费者名称，claim 由 Redis 原子完成。
- 主扫描游标持续向后推进；每第 10 次扫描强制从 `0-0`/无下界重新检查，不改变主游标位置，避免持续积压时较早消息饥饿。
- 达到最大投递次数后，消息写入 `<topic><dead-letter-suffix>`，写入成功后再 ACK 原消息。
- 死信记录包含 `originalTopic`、`originalMessageId`、`payload`、`failureCount`、`deliveryCount`、`lastError`。`lastError` 只记录异常类型，不记录可能含敏感信息的异常消息正文。
- 死信写入成功后输出 `WARN` 日志，记录 topic、消息 ID、失败次数和安全异常类型，不输出 payload。后续可在此基础上接入站内工单或告警平台。

`failureCount` 由每次业务监听器抛出异常时单独累加；`deliveryCount` 表示 Redis 实际投递次数，可能包含业务成功但 ACK 失败后的再次投递。业务处理失败且 `deliveryCount` 达到配置上限后，消息进入死信阶段，后续只重试死信写入，不再调用业务监听器。

失败次数、安全异常类型和死信阶段标记存放在逐消息临时 Key `<redis-key-prefix>:pending-error:<encoded-topic>:<encoded-group>:<message-id>` 中，默认前缀为中性的 `mq`，下游项目可以自行配置。写入通过 Lua 原子设置 30 天 TTL，死信写入并 ACK 成功后立即删除；即使最终清理失败也会自动过期。

如果 Pending 扫描或 Redis 连续不可用超过 30 天，失败元数据可能先于原消息过期；服务恢复后该消息可能再次进入业务监听器，因此业务处理仍需保持幂等。

## 配置

```yaml
yeah-boot:
  mq:
    impl: redis
    auto-ack: true
    default-group: DEFAULT_GROUP
    batch-size: 10
    block-timeout: 2s
    pending-retry-enabled: false
    pending-scan-interval: 10s
    pending-min-idle: 30s
    max-deliveries: 5
    dead-letter-suffix: ":DLQ"
    redis-key-prefix: mq
```

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `pending-retry-enabled` | `false` | 是否扫描并恢复 Pending 消息；为保持升级兼容，默认关闭，需显式启用 |
| `pending-scan-interval` | `10s` | 两轮扫描之间的间隔 |
| `pending-min-idle` | `30s` | 消息可被重新认领前的最小空闲时间 |
| `max-deliveries` | `5` | Redis 投递次数达到该值且处理仍失败时转入死信 |
| `dead-letter-suffix` | `:DLQ` | 死信 Stream 后缀 |
| `redis-key-prefix` | `mq` | MQ 写入 Redis 的内部 Key 前缀，可按项目定制 |

`auto-ack=false` 保持原有“不自动 ACK”语义，同时禁用 Pending 自动恢复。当前公共接口没有提供手动 ACK；如果此时仍自动扫描，已成功处理但未 ACK 的消息也会被不断重新投递。

同一个应用实例中，一个 topic 只能注册一个 `MqListener`。公共接口没有 group 或 handler 标识，重复注册会导致 Pending 消息被不同业务监听器竞争处理，因此启动时会直接拒绝。

`pending-min-idle` 应大于单条业务消息的最长正常处理时间，否则仍在执行的消息可能被其他实例提前认领。Redis Stream 提供的是至少一次投递语义；业务处理成功但 ACK 失败时仍可能再次投递，因此监听器应保持幂等。死信写入成功后才会 ACK 原消息，但如果死信写入成功后进程在 ACK 前退出，死信 Stream 可能出现可按 `originalMessageId` 识别的重复记录。

ACK 只会移除 PEL 状态，不会删除原 Stream 记录；原消息 Stream 和死信 Stream 的保留、裁剪与归档策略应由业务运维配置。随机消费者名用于多实例安全认领，框架不会自动执行 `XGROUP DELCONSUMER`，因为删除仍有 Pending 的消费者会造成消息无法再被认领；长期运行时可在确认消费者 Pending 数为 0 后进行运维清理。

Redis 5.0 兼容路径通过强制执行 `XPENDING + XCLAIM` 的自动化测试覆盖；当前真实 Redis 集成测试使用本机可用版本，尚未在真实 Redis 5.0 服务上执行。
