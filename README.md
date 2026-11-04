# YeahMall

> 基于 [YeahBoot](https://github.com/yeshimin/yeah-boot) 构建的多商户商城系统。

YeahMall 面向平台运营、商家经营和消费者购物场景，包含 Java 后端、Vue 3 管理后台和 UniApp 消费者端。项目在 YeahBoot 通用能力的基础上，实现商品、交易、营销、会员、客服和结算等商城业务。

## 项目组成

| 项目 | 说明 |
| --- | --- |
| `yeah-mall` | Java 后端，本仓库 |
| `yeah-mall-admin` | 平台后台与商家后台 |
| `yeah-mall-app` | UniApp 消费者端 |

## 核心功能

- 平台、商家、店铺和会员管理
- 商品、SKU、分类、购物车和订单
- 支付、退款、售后、物流和评价
- 优惠券、秒杀、拼团和积分商城
- 在线客服、首页运营和商家结算
- 平台商城与独立店铺 App

## 后端模块

```text
yeah-admin          聚合启动入口及平台后台接口
yeah-app            消费者端接口
yeah-merchant       商家后台接口
yeah-biz-common     商城数据模型与业务服务
yeah-biz-module     YeahBoot 基础业务模块
yeah-framework      YeahBoot 通用框架能力
docs                原型、设计和项目文档
sql                 数据库脚本
```

日常开发默认启动 `yeah-admin`，由一个进程同时提供平台后台、商家后台和消费者端接口。

## 技术栈

- Java 8、Spring Boot 2.7
- Spring Security、MyBatis-Plus
- MySQL、Redis、Redis Stream
- WebSocket、微信支付 Java SDK
- Maven 多模块工程

## 快速开始

准备 JDK 8、Maven、MySQL 和 Redis，并完成本地数据库及应用配置。

```bash
mvn -pl yeah-admin -am clean package -DskipTests
java -jar yeah-admin/target/yeah-admin-1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

也可以在 IDE 中直接运行 `YeahAdminApplication`。

## 与 YeahBoot 的关系

YeahMall 是 YeahBoot 的下游商城项目。权限、存储、通知、WebSocket、MQ 等通用能力来自 YeahBoot，商城领域能力由 YeahMall 扩展实现。

## License

许可证信息见 [LICENSE](./LICENSE)。
