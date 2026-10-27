-- 用户反馈与关于我们增量脚本：请人工确认后执行，程序不会自动执行。

CREATE TABLE IF NOT EXISTS `s_member_feedback` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `delete_time` datetime NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：1-是 0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新者',
  `member_id` bigint(20) NOT NULL COMMENT '会员ID',
  `content` varchar(500) NOT NULL COMMENT '反馈内容',
  `contact` varchar(50) NOT NULL DEFAULT '' COMMENT '联系方式',
  `status` varchar(2) NOT NULL DEFAULT '1' COMMENT '处理状态：1待处理 2处理中 3已解决',
  `reply_content` varchar(500) NOT NULL DEFAULT '' COMMENT '平台处理回复',
  `reply_time` datetime DEFAULT NULL COMMENT '平台回复时间',
  PRIMARY KEY (`id`),
  KEY `idx_member_create` (`member_id`, `create_time`),
  KEY `idx_status_create` (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户意见反馈表';

CREATE TABLE IF NOT EXISTS `s_member_feedback_image` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `delete_time` datetime NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：1-是 0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新者',
  `feedback_id` bigint(20) NOT NULL COMMENT '反馈ID',
  `image` varchar(255) NOT NULL COMMENT '图片文件键',
  `sort` int(11) NOT NULL DEFAULT '1' COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `idx_feedback_sort` (`feedback_id`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈图片表';

SET @feedback_seed_id := CAST(
    (UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000 - 1288834974657) * 4194304 + 2000 AS UNSIGNED
);

-- App 关于我们公开系统参数。内容修改请在平台后台“系统参数”中编辑，修改后刷新系统参数缓存。
INSERT INTO sys_config
    (id, group_code, config_key, config_name, config_value, value_type, status, public_access, sort, remark)
SELECT @feedback_seed_id + 1, 'app.content', 'app.about.content', '关于我们内容', '', 1, '1', 1, 1,
       'App 关于我们公开内容，支持换行文本'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_config WHERE config_key = 'app.about.content' AND deleted = 0
);

-- 平台反馈管理菜单。执行后请退出并重新登录刷新动态菜单。
SET @feedback_res_id := (
    SELECT id
    FROM sys_res
    WHERE deleted = 0 AND component = 'mall/feedback/index'
    LIMIT 1
);

INSERT INTO sys_res
    (id, type, parent_id, group_id, name, permission, path, component, icon, is_link, link_url, status, visible, sort, remark)
SELECT @feedback_seed_id + 2, 2, 27, 0, '意见反馈', '', 'feedback', 'mall/feedback/index',
       'ChatDotRound', 0, '', '1', 1, 8, '平台用户意见反馈管理'
WHERE @feedback_res_id IS NULL;

SET @feedback_res_id := (
    SELECT id
    FROM sys_res
    WHERE deleted = 0 AND component = 'mall/feedback/index'
    LIMIT 1
);

INSERT INTO sys_role_res (id, role_id, res_id, mount_id)
SELECT @feedback_seed_id + 3, 1, @feedback_res_id, 0
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_role_res
    WHERE deleted = 0 AND role_id = 1 AND res_id = @feedback_res_id AND mount_id = 0
);
