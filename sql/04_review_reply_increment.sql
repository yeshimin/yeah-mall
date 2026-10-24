-- 评价无限交替回复增量脚本：请人工确认后执行，程序不会自动执行。

-- 1. 主评价表仅保存买家首评，后续双方交替回复统一写入本表。
CREATE TABLE IF NOT EXISTS s_product_sku_review_reply (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    delete_time DATETIME NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：1是，0否',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '创建者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '更新者',
    mch_id BIGINT NOT NULL COMMENT '商户ID',
    shop_id BIGINT NOT NULL COMMENT '店铺ID',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    review_id BIGINT NOT NULL COMMENT '评价ID',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '上一条回复ID，0表示直接回复首评',
    sender_type TINYINT NOT NULL COMMENT '回复方类型：1买家，2商家',
    sender_id BIGINT NOT NULL COMMENT '回复操作人ID',
    content VARCHAR(512) NOT NULL DEFAULT '' COMMENT '回复内容',
    PRIMARY KEY (id),
    KEY idx_review_create (review_id, create_time, id),
    KEY idx_review_parent (review_id, parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价公开回复表';

-- 2. 将旧的单条商家回复迁移为首条公开回复。
--    迁移回复复用评价ID作为主键，仅影响新表中尚无该评价回复的数据。
INSERT INTO s_product_sku_review_reply
    (id, create_time, create_by, update_time, update_by, mch_id, shop_id, member_id,
     review_id, parent_id, sender_type, sender_id, content)
SELECT r.id, COALESCE(r.reply_time, r.update_time), r.reply_by, COALESCE(r.reply_time, r.update_time), r.reply_by,
       r.mch_id, r.shop_id, r.member_id, r.id, 0, 2, r.mch_id, r.reply_content
FROM s_product_sku_review r
WHERE r.deleted = 0
  AND r.reply_content <> ''
  AND NOT EXISTS (
      SELECT 1
      FROM s_product_sku_review_reply rr
      WHERE rr.review_id = r.id
  );

-- 3. 完成迁移后，删除评价主表的旧单条回复字段。
ALTER TABLE s_product_sku_review
    DROP COLUMN reply_content,
    DROP COLUMN reply_time,
    DROP COLUMN reply_by;
