package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单退款记录表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_order_refund")
public class OrderRefundEntity extends ShopConditionBaseEntity<OrderRefundEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 退款编号
     */
    private String refundNo;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款状态：SUCCESS—退款成功, CLOSED—退款关闭, PROCESSING—退款处理中, ABNORMAL—退款异常
     */
    private String refundStatus;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款成功时间
     */
    private LocalDateTime successTime;

    /**
     * 退款入账账户
     */
    private String userReceivedAccount;

    /**
     * 微信支付对回调内容的摘要备注
     */
    private String summary;

    /**
     * 回调中实际总金额
     */
    private BigDecimal realTotalAmount;

    /**
     * 回调中实际退款金额
     */
    private BigDecimal realRefundAmount;

    /**
     * 退款请求调度状态：PENDING、PROCESSING、SUBMITTED、RETRY、FAILED、COMPLETED
     */
    private String dispatchStatus;

    /**
     * MQ之外的补偿任务下次可处理时间，同时作为处理中任务的租约截止时间
     */
    private LocalDateTime nextProcessTime;

    /**
     * 调用微信退款申请接口次数
     */
    private Integer requestCount;

    /**
     * 最近一次处理失败的异常类型，不保存异常消息正文
     */
    private String lastError;

    /**
     * 当前处理者令牌，MQ或Job抢占后用于校验租约所有权
     */
    private String processingToken;
}
