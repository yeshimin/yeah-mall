-- 退款 MQ 快速处理与 Job 补偿调度字段：请人工确认后执行，程序不会自动执行。

-- 若此查询返回记录，需先人工处理重复退款单号，再执行后续 ALTER TABLE。
SELECT refund_no, COUNT(*) AS refund_count
FROM s_order_refund
GROUP BY refund_no
HAVING COUNT(*) > 1;

ALTER TABLE s_order_refund
    ADD COLUMN dispatch_status VARCHAR(16) NOT NULL DEFAULT 'COMPLETED'
        COMMENT '退款请求调度状态：PENDING、PROCESSING、SUBMITTED、RETRY、FAILED、COMPLETED'
        AFTER real_refund_amount,
    ADD COLUMN next_process_time DATETIME DEFAULT NULL
        COMMENT '下次可处理时间，同时作为处理租约截止时间'
        AFTER dispatch_status,
    ADD COLUMN request_count INT NOT NULL DEFAULT 0
        COMMENT '调用微信退款申请接口次数'
        AFTER next_process_time,
    ADD COLUMN last_error VARCHAR(64) NOT NULL DEFAULT ''
        COMMENT '最近一次处理失败的异常类型'
        AFTER request_count,
    ADD COLUMN processing_token VARCHAR(64) NOT NULL DEFAULT ''
        COMMENT '当前处理者令牌，用于校验任务租约所有权'
        AFTER last_error,
    ADD UNIQUE KEY uk_refund_no (refund_no),
    ADD KEY idx_order_create (order_id, create_time),
    ADD KEY idx_refund_dispatch_due (deleted, dispatch_status, next_process_time);

-- 历史终态记录不重放；仍处于退款中的订单延迟纳入 Job 补偿。
UPDATE s_order_refund AS refund
INNER JOIN (
    SELECT order_id, MAX(id) AS latest_refund_id
    FROM s_order_refund
    WHERE deleted = 0
      AND refund_status = 'PROCESSING'
    GROUP BY order_id
) AS latest_refund ON latest_refund.latest_refund_id = refund.id
INNER JOIN s_order AS mall_order ON mall_order.id = refund.order_id
SET refund.dispatch_status = 'SUBMITTED',
    refund.next_process_time = DATE_ADD(NOW(), INTERVAL 2 MINUTE)
WHERE refund.deleted = 0
  AND mall_order.deleted = 0
  AND mall_order.status = '6'
  AND mall_order.refund_status = '2';

-- 新建退款任务由程序显式写入 PENDING。
