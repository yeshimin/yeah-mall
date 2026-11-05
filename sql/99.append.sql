-- 已初始化数据库的累计增量结构补丁。
--
-- 新建数据库请先按 README 的顺序导入 00 至 03 初始化包，再执行本文件。
-- 后续升级时，按时间顺序执行尚未应用的版本段落；每个段落仅执行一次。

-- 2026-10-23：扩展系统日志额外信息字段长度。
ALTER TABLE `sys_log`
    CHANGE COLUMN `extra` `extra` VARCHAR(512) NULL DEFAULT NULL COMMENT '额外信息（json字符串）';

-- 2026-11-02：新增管理后台全局导航模式参数。
INSERT INTO `sys_config`
    (`group_code`, `config_key`, `config_name`, `config_value`, `value_type`,
     `status`, `public_access`, `sort`, `remark`)
SELECT
    'admin.layout', 'admin.layout.navigation-mode', '管理后台导航模式', 'side', 1,
    '1', 1, 10, '管理后台全局导航模式：side-左侧导航 top-顶部导航'
WHERE NOT EXISTS (
    SELECT 1
    FROM `sys_config`
    WHERE `deleted` = 0
      AND `config_key` = 'admin.layout.navigation-mode'
);

-- 2026-11-05：新增站内信消息表和接收记录表。
CREATE TABLE IF NOT EXISTS `notif_message` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `delete_time` datetime NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：1-是 0-否',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新者',
    `message_type` tinyint NOT NULL COMMENT '消息类型：1-系统消息 2-业务消息 3-通告',
    `target_type` tinyint NOT NULL COMMENT '发送目标类型：1-指定用户 2-全部用户',
    `title` varchar(128) NOT NULL COMMENT '消息标题',
    `content` text NOT NULL COMMENT '消息内容',
    `biz_type` varchar(32) NOT NULL DEFAULT '' COMMENT '关联业务类型',
    `biz_id` varchar(64) NOT NULL DEFAULT '' COMMENT '关联业务ID',
    `link_url` varchar(255) NOT NULL DEFAULT '' COMMENT '前端跳转地址',
    `sender_subject` varchar(32) NOT NULL DEFAULT '' COMMENT '发送者主体',
    `sender_user_id` bigint NOT NULL DEFAULT '0' COMMENT '发送者用户ID',
    `sender_name` varchar(64) NOT NULL DEFAULT '' COMMENT '发送者名称',
    `receiver_count` int NOT NULL DEFAULT '0' COMMENT '接收人数',
    `send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (`id`),
    KEY `idx_notif_message_biz` (`biz_type`, `biz_id`),
    KEY `idx_notif_message_type` (`message_type`, `id`),
    KEY `idx_notif_message_send_time` (`send_time`)
) ENGINE=InnoDB COMMENT='站内信消息表';

CREATE TABLE IF NOT EXISTS `notif_message_receiver` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `delete_time` datetime NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：1-是 0-否',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` varchar(32) NOT NULL DEFAULT '' COMMENT '创建者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by` varchar(32) NOT NULL DEFAULT '' COMMENT '更新者',
    `message_id` bigint NOT NULL COMMENT '消息ID',
    `receiver_subject` varchar(32) NOT NULL COMMENT '接收者主体',
    `receiver_user_id` bigint NOT NULL COMMENT '接收者用户ID',
    `read_status` tinyint NOT NULL DEFAULT '0' COMMENT '阅读状态：0-未读 1-已读',
    `read_time` datetime NULL DEFAULT NULL COMMENT '阅读时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_notif_receiver_message_user`
        (`message_id`, `receiver_subject`, `receiver_user_id`, `delete_time`),
    KEY `idx_notif_receiver_inbox` (`receiver_subject`, `receiver_user_id`, `deleted`, `id`),
    KEY `idx_notif_receiver_unread` (`receiver_subject`, `receiver_user_id`, `deleted`, `read_status`, `id`),
    KEY `idx_notif_receiver_message` (`message_id`, `deleted`)
) ENGINE=InnoDB COMMENT='站内信接收记录表';

ALTER TABLE `sys_res`
CHANGE COLUMN `path` `path` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '前端路径' ,
CHANGE COLUMN `component` `component` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '前端组件' ;

-- 新增消息中心页面资源。
INSERT INTO `sys_res`
    (`type`, `parent_id`, `group_id`, `name`, `permission`, `path`, `component`, `icon`,
     `is_link`, `link_url`, `status`, `visible`, `sort`, `remark`)
SELECT
    2, 0, 0, '消息中心', '', '/notifications', 'notification/NotificationCenterView', 'Bell',
    0, '', '1', 1, 20, '管理后台站内信消息中心'
WHERE NOT EXISTS (
    SELECT 1
    FROM `sys_res`
    WHERE `deleted` = 0
      AND `path` = '/notifications'
);

-- 新增站内信接口分组和发送权限资源。
INSERT INTO `sys_res_group` (`parent_id`, `name`, `sort`, `remark`)
SELECT parent_group.`id`, '站内信接口', 100, '管理后台站内信接口'
FROM `sys_res_group` parent_group
WHERE parent_group.`deleted` = 0
  AND parent_group.`parent_id` = 0
  AND parent_group.`name` = '系统管理接口'
  AND NOT EXISTS (
      SELECT 1
      FROM `sys_res_group` current_group
      WHERE current_group.`deleted` = 0
        AND current_group.`parent_id` = parent_group.`id`
        AND current_group.`name` = '站内信接口'
  );

INSERT INTO `sys_res`
    (`type`, `parent_id`, `group_id`, `name`, `permission`, `status`, `visible`, `sort`, `remark`)
SELECT
    4, 0, message_group.`id`, '发送站内信接口', 'api:admin:notifMessage:send', '1', 1, 10,
    'GET /admin/notifMessage/receiverOptions; POST /admin/notifMessage/send'
FROM `sys_res_group` message_group
JOIN `sys_res_group` parent_group
  ON parent_group.`id` = message_group.`parent_id`
 AND parent_group.`deleted` = 0
 AND parent_group.`parent_id` = 0
 AND parent_group.`name` = '系统管理接口'
WHERE message_group.`deleted` = 0
  AND message_group.`name` = '站内信接口'
  AND NOT EXISTS (
      SELECT 1
      FROM `sys_res` current_res
      WHERE current_res.`deleted` = 0
        AND current_res.`permission` = 'api:admin:notifMessage:send'
  );

-- 将发送接口挂载到消息中心页面，供角色按页面分配发送权限。
INSERT INTO `sys_res_mount` (`view_res_id`, `api_res_id`, `sort`, `remark`)
SELECT message_page.`id`, send_api.`id`, 10, '消息中心发送站内信接口'
FROM `sys_res` message_page
JOIN `sys_res` send_api
  ON send_api.`deleted` = 0
 AND send_api.`permission` = 'api:admin:notifMessage:send'
WHERE message_page.`deleted` = 0
  AND message_page.`path` = '/notifications'
  AND NOT EXISTS (
      SELECT 1
      FROM `sys_res_mount` current_mount
      WHERE current_mount.`deleted` = 0
        AND current_mount.`view_res_id` = message_page.`id`
        AND current_mount.`api_res_id` = send_api.`id`
  );
