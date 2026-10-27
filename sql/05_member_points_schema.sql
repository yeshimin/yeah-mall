-- 会员积分基础功能：请人工确认后执行，程序不会自动执行。

CREATE TABLE IF NOT EXISTS s_member_points_account (
    id BIGINT NOT NULL COMMENT '主键ID',
    delete_time DATETIME NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '创建者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '更新者',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    balance BIGINT NOT NULL DEFAULT 0 COMMENT '可用积分',
    total_earned BIGINT NOT NULL DEFAULT 0 COMMENT '累计获得积分',
    total_used BIGINT NOT NULL DEFAULT 0 COMMENT '累计使用积分',
    PRIMARY KEY (id),
    UNIQUE KEY uk_member_id (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员积分账户';

CREATE TABLE IF NOT EXISTS s_member_points_ledger (
    id BIGINT NOT NULL COMMENT '主键ID',
    delete_time DATETIME NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '创建者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '更新者',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    change_points BIGINT NOT NULL COMMENT '积分变动值',
    balance_before BIGINT NOT NULL COMMENT '变动前余额',
    balance_after BIGINT NOT NULL COMMENT '变动后余额',
    type TINYINT NOT NULL COMMENT '流水类型：1获得，2使用，3调整',
    biz_type VARCHAR(32) NOT NULL COMMENT '业务类型',
    biz_id BIGINT NOT NULL COMMENT '业务ID',
    biz_no VARCHAR(64) NOT NULL DEFAULT '' COMMENT '业务单号',
    rule_code VARCHAR(32) NOT NULL DEFAULT '' COMMENT '规则编码',
    description VARCHAR(255) NOT NULL DEFAULT '' COMMENT '流水说明',
    PRIMARY KEY (id),
    UNIQUE KEY uk_biz_type_id (biz_type, biz_id),
    KEY idx_member_create (member_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员积分流水';

CREATE TABLE IF NOT EXISTS s_member_points_rule (
    id BIGINT NOT NULL COMMENT '主键ID',
    delete_time DATETIME NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '创建者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '更新者',
    code VARCHAR(32) NOT NULL COMMENT '规则编码',
    name VARCHAR(32) NOT NULL COMMENT '规则名称',
    calc_type TINYINT NOT NULL COMMENT '计算方式：1固定积分，2按金额比例',
    fixed_points BIGINT NOT NULL DEFAULT 0 COMMENT '固定积分',
    points_per_yuan DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '每一元获得积分数',
    is_enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    sort INT NOT NULL DEFAULT 1 COMMENT '排序',
    description VARCHAR(255) NOT NULL DEFAULT '' COMMENT '规则说明',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员积分获取规则';

-- 积分账户按首次获得或使用积分时懒创建，无需为历史会员批量创建零余额账户。

SET @points_seed_id := CAST(
    (UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000 - 1288834974657) * 4194304 AS UNSIGNED
);

INSERT INTO s_member_points_rule
    (id, code, name, calc_type, fixed_points, points_per_yuan, is_enabled, sort, description)
VALUES
    (@points_seed_id + 1, 'REGISTER', '注册赠送', 1, 100, 0, 1, 1, '新会员注册成功赠送100积分'),
    (@points_seed_id + 2, 'ORDER_COMPLETE', '订单完成', 2, 0, 1, 1, 2, '订单完成后，每1元优惠后应付金额获得1积分'),
    (@points_seed_id + 3, 'REVIEW', '完成评价', 1, 10, 0, 1, 3, '订单完成评价赠送10积分')
ON DUPLICATE KEY UPDATE code = VALUES(code);

CREATE TABLE IF NOT EXISTS s_points_category (
    id BIGINT NOT NULL COMMENT '主键ID',
    delete_time DATETIME NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '创建者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '更新者',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示一级分类',
    name VARCHAR(64) NOT NULL COMMENT '分类名称',
    icon VARCHAR(64) NOT NULL DEFAULT '' COMMENT '分类图标文件Key',
    sort INT NOT NULL DEFAULT 1 COMMENT '排序',
    is_enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    PRIMARY KEY (id),
    KEY idx_parent_enabled_sort (parent_id, is_enabled, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分商品分类';

CREATE TABLE IF NOT EXISTS s_points_spu (
    id BIGINT NOT NULL COMMENT '主键ID',
    delete_time DATETIME NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '创建者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '更新者',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    name VARCHAR(64) NOT NULL COMMENT '商品名称',
    subtitle VARCHAR(128) NOT NULL DEFAULT '' COMMENT '商品副标题',
    product_type TINYINT NOT NULL COMMENT '商品类型：1实物，2优惠券，3虚拟权益',
    fulfillment_type TINYINT NOT NULL COMMENT '履约方式：1物流发货，2自动发券，3人工履约',
    main_image VARCHAR(64) NOT NULL COMMENT '商品主图文件Key',
    detail_desc MEDIUMTEXT COMMENT '商品详情',
    coupon_id BIGINT DEFAULT NULL COMMENT '自动发放的优惠券ID',
    per_member_limit INT NOT NULL DEFAULT 0 COMMENT '每人限兑数量，0表示不限',
    begin_time DATETIME NOT NULL COMMENT '兑换开始时间',
    end_time DATETIME NOT NULL COMMENT '兑换结束时间',
    is_enabled TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否上架',
    sort INT NOT NULL DEFAULT 1 COMMENT '排序',
    PRIMARY KEY (id),
    KEY idx_category_enabled_time (category_id, is_enabled, begin_time, end_time),
    KEY idx_coupon_id (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分商品SPU';

CREATE TABLE IF NOT EXISTS s_points_sku (
    id BIGINT NOT NULL COMMENT '主键ID',
    delete_time DATETIME NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '创建者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '更新者',
    spu_id BIGINT NOT NULL COMMENT '积分商品SPU ID',
    name VARCHAR(64) NOT NULL COMMENT 'SKU名称',
    points_price BIGINT NOT NULL COMMENT '单件积分价格',
    stock INT NOT NULL DEFAULT 0 COMMENT '可用库存',
    total_stock INT NOT NULL DEFAULT 0 COMMENT '累计配置库存',
    main_image VARCHAR(64) NOT NULL DEFAULT '' COMMENT 'SKU主图文件Key',
    is_enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    sort INT NOT NULL DEFAULT 1 COMMENT '排序',
    PRIMARY KEY (id),
    KEY idx_spu_enabled_sort (spu_id, is_enabled, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分商品SKU';

CREATE TABLE IF NOT EXISTS s_points_order (
    id BIGINT NOT NULL COMMENT '主键ID',
    delete_time DATETIME NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '创建者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '更新者',
    order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
    request_no VARCHAR(64) NOT NULL COMMENT '客户端幂等请求号',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    spu_id BIGINT NOT NULL COMMENT '积分商品SPU ID',
    sku_id BIGINT NOT NULL COMMENT '积分商品SKU ID',
    product_name VARCHAR(64) NOT NULL COMMENT '商品名称快照',
    sku_name VARCHAR(64) NOT NULL COMMENT 'SKU名称快照',
    product_image VARCHAR(64) NOT NULL DEFAULT '' COMMENT '商品图片快照',
    product_type TINYINT NOT NULL COMMENT '商品类型',
    fulfillment_type TINYINT NOT NULL COMMENT '履约方式',
    quantity INT NOT NULL COMMENT '兑换数量',
    points_price BIGINT NOT NULL COMMENT '单件积分价格',
    total_points BIGINT NOT NULL COMMENT '兑换总积分',
    status VARCHAR(2) NOT NULL COMMENT '状态：1待履约，2已发货，3已完成，4已取消',
    member_coupon_id BIGINT DEFAULT NULL COMMENT '自动发放的会员优惠券ID',
    receiver_name VARCHAR(64) NOT NULL DEFAULT '' COMMENT '收货人姓名快照',
    receiver_contact VARCHAR(32) NOT NULL DEFAULT '' COMMENT '收货人联系方式快照',
    receiver_full_address VARCHAR(255) NOT NULL DEFAULT '' COMMENT '收货地址快照',
    delivery_provider_code VARCHAR(32) NOT NULL DEFAULT '' COMMENT '物流公司编码',
    tracking_no VARCHAR(64) NOT NULL DEFAULT '' COMMENT '物流单号',
    fulfillment_remark VARCHAR(255) NOT NULL DEFAULT '' COMMENT '履约说明',
    ship_time DATETIME DEFAULT NULL COMMENT '发货时间',
    complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
    cancel_time DATETIME DEFAULT NULL COMMENT '取消时间',
    cancel_reason VARCHAR(255) NOT NULL DEFAULT '' COMMENT '取消原因',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    UNIQUE KEY uk_member_request (member_id, request_no),
    KEY idx_member_status_create (member_id, status, create_time),
    KEY idx_spu_member_status (spu_id, member_id, status),
    KEY idx_status_ship_time (status, ship_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分兑换订单';

CREATE TABLE IF NOT EXISTS s_points_order_log (
    id BIGINT NOT NULL COMMENT '主键ID',
    delete_time DATETIME NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '创建者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by VARCHAR(32) NOT NULL DEFAULT '' COMMENT '更新者',
    order_id BIGINT NOT NULL COMMENT '积分订单ID',
    order_no VARCHAR(32) NOT NULL COMMENT '积分订单编号',
    status_from VARCHAR(2) NOT NULL DEFAULT '' COMMENT '原状态',
    status_to VARCHAR(2) NOT NULL COMMENT '新状态',
    remark VARCHAR(255) NOT NULL DEFAULT '' COMMENT '操作说明',
    PRIMARY KEY (id),
    KEY idx_order_create (order_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分订单状态日志';

ALTER TABLE s_mch_coupon
    ADD COLUMN is_points_exchange_enabled TINYINT(1) NOT NULL DEFAULT 0
    COMMENT '是否允许作为积分商品兑换'
    AFTER is_support_group_buy;

ALTER TABLE s_member_coupon
    ADD COLUMN receive_channel TINYINT NOT NULL DEFAULT 1
    COMMENT '领取渠道：1普通领取，2积分兑换'
    AFTER is_support_group_buy,
    ADD COLUMN source_points_order_id BIGINT DEFAULT NULL
    COMMENT '来源积分订单ID'
    AFTER receive_channel,
    ADD KEY idx_source_points_order (source_points_order_id);

INSERT INTO sys_config
    (id, group_code, config_key, config_name, config_value, value_type, status, public_access, sort, remark)
SELECT @points_seed_id + 11, 'mall_points', 'mall.points.mall.enabled', '积分商城开关', 'true', 4, '1', 0, 1,
       '关闭后禁止浏览和新兑换，不影响历史订单履约'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_config WHERE deleted = 0 AND config_key = 'mall.points.mall.enabled'
);

INSERT INTO sys_config
    (id, group_code, config_key, config_name, config_value, value_type, status, public_access, sort, remark)
SELECT @points_seed_id + 12, 'mall_points', 'mall.points.order.auto-complete-days', '积分订单自动确认天数', '15', 2, '1', 0, 2,
       '物流积分订单发货后超过该天数自动完成'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_config WHERE deleted = 0 AND config_key = 'mall.points.order.auto-complete-days'
);

SET @points_res_id := (
    SELECT id FROM sys_res WHERE deleted = 0 AND component = 'mall/member-points/index' LIMIT 1
);

INSERT INTO sys_res
    (id, type, parent_id, group_id, name, permission, path, component, icon, is_link, link_url, status, visible, sort, remark)
SELECT @points_seed_id + 21, 2, 27, 0, '会员积分', '', 'member-points', 'mall/member-points/index', 'form', 0, '', '1', 1, 5, '会员积分账户、流水与规则'
WHERE @points_res_id IS NULL;

SET @points_res_id := (
    SELECT id FROM sys_res WHERE deleted = 0 AND component = 'mall/member-points/index' LIMIT 1
);

INSERT INTO sys_role_res (id, role_id, res_id, mount_id)
SELECT @points_seed_id + 22, 1, @points_res_id, 0
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role_res
    WHERE deleted = 0 AND role_id = 1 AND res_id = @points_res_id AND mount_id = 0
);

SET @mall_parent_id := (
    SELECT parent_id FROM sys_res
    WHERE deleted = 0 AND component = 'mall/member-points/index'
    LIMIT 1
);

SET @points_mall_res_id := (
    SELECT id FROM sys_res WHERE deleted = 0 AND component = 'mall/points-mall/index' LIMIT 1
);

INSERT INTO sys_res
    (id, type, parent_id, group_id, name, permission, path, component, icon, is_link, link_url, status, visible, sort, remark)
SELECT @points_seed_id + 23, 2, COALESCE(@mall_parent_id, 27), 0, '积分商城', '', 'points-mall', 'mall/points-mall/index',
       'goods', 0, '', '1', 1, 6, '积分商品、分类、库存、兑换订单与履约管理'
WHERE @points_mall_res_id IS NULL;

SET @points_mall_res_id := (
    SELECT id FROM sys_res WHERE deleted = 0 AND component = 'mall/points-mall/index' LIMIT 1
);

INSERT INTO sys_role_res (id, role_id, res_id, mount_id)
SELECT @points_seed_id + 24, 1, @points_mall_res_id, 0
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role_res
    WHERE deleted = 0 AND role_id = 1 AND res_id = @points_mall_res_id AND mount_id = 0
);
