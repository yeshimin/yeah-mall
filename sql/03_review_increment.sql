-- 评价功能增量脚本：请人工确认后执行，程序不会自动执行。

-- 1. 发布评价的数据库兜底：每个订单只能有一条订单评价。
--    若此查询返回记录，先人工确认并清理重复数据，再执行后续唯一索引语句。
SELECT order_id, COUNT(*) AS review_count
FROM s_order_review
WHERE deleted = 0
GROUP BY order_id
HAVING COUNT(*) > 1;

ALTER TABLE s_order_review
    ADD UNIQUE KEY uk_order_id (order_id);

-- 2. 与 App 和后端的 512 字评价限制保持一致。
ALTER TABLE s_product_sku_review
    MODIFY COLUMN content VARCHAR(512) NOT NULL DEFAULT '' COMMENT '评价内容';

-- 3. 平台、商家评价管理菜单。可重复执行，执行后请退出并重新登录刷新动态菜单。
SET @platform_review_res_id := (
    SELECT id
    FROM sys_res
    WHERE deleted = 0 AND component = 'mall/review/index'
    LIMIT 1
);

INSERT INTO sys_res (type, parent_id, group_id, name, permission, path, component, icon, is_link, link_url, status, visible, sort, remark)
SELECT 2, 27, 0, '评价管理', '', 'review', 'mall/review/index', 'form', 0, '', '1', 1, 4, '平台商品评价管理'
WHERE @platform_review_res_id IS NULL;

SET @platform_review_res_id := (
    SELECT id
    FROM sys_res
    WHERE deleted = 0 AND component = 'mall/review/index'
    LIMIT 1
);

INSERT INTO sys_role_res (role_id, res_id, mount_id)
SELECT 1, @platform_review_res_id, 0
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_role_res
    WHERE deleted = 0 AND role_id = 1 AND res_id = @platform_review_res_id AND mount_id = 0
);

SET @mch_review_res_id := (
    SELECT id
    FROM mch_res
    WHERE deleted = 0 AND component = 'mall/mch-review/index'
    LIMIT 1
);

INSERT INTO mch_res (type, parent_id, name, permission, path, component, icon, is_link, link_url, status, visible, sort, remark)
SELECT 2, 1, '评价管理', '', 'review', 'mall/mch-review/index', 'form', 0, '', '1', 1, 12, '商家商品评价管理'
WHERE @mch_review_res_id IS NULL;
