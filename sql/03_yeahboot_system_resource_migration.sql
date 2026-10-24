-- 将 YeahBoot 标准“系统管理/基础管理”资源同步到商城库。
-- 依赖当前实例同时存在 yeah_boot 与 yeah_mall 两个库；执行前请先备份目标库。
-- 本脚本不自动给现有角色授予新增 API/按钮资源，避免权限扩大。

START TRANSACTION;

-- 同步 API 资源分组，供资源管理页正确展示系统与基础接口树。
INSERT INTO yeah_mall.sys_res_group (
    id, delete_time, deleted, create_time, create_by, update_time, update_by,
    parent_id, name, sort, remark
)
SELECT
    id, delete_time, deleted, create_time, create_by, update_time, update_by,
    parent_id, name, sort, remark
FROM yeah_boot.sys_res_group
WHERE deleted = 0
ON DUPLICATE KEY UPDATE
    parent_id = VALUES(parent_id),
    name = VALUES(name),
    sort = VALUES(sort),
    remark = VALUES(remark),
    deleted = 0;

-- 保留商城库既有菜单 ID 与角色关联，仅统一名称和统一日志入口。
UPDATE yeah_mall.sys_res
SET name = CASE id
        WHEN 4 THEN '资源管理'
        WHEN 5 THEN '组织管理'
        WHEN 8 THEN '系统参数'
        WHEN 9 THEN '系统日志'
        ELSE name
    END,
    type = CASE WHEN id = 9 THEN 2 ELSE type END,
    path = CASE WHEN id = 9 THEN 'log' ELSE path END,
    component = CASE WHEN id = 9 THEN 'system/log/index' ELSE component END,
    status = CASE WHEN id = 9 THEN '1' ELSE status END,
    visible = CASE WHEN id = 9 THEN 1 ELSE visible END
WHERE id IN (4, 5, 8, 9)
  AND deleted = 0;

-- 若依旧“操作日志/登录日志”页面已由 YeahBoot 统一日志页替代，保留数据但从菜单隐藏。
UPDATE yeah_mall.sys_res
SET status = '2',
    visible = 0
WHERE id IN (10, 11)
  AND deleted = 0;

-- 补齐 YeahBoot 的基础管理菜单：地区、文件、存储。
INSERT INTO yeah_mall.sys_res (
    id, delete_time, deleted, create_time, create_by, update_time, update_by,
    type, parent_id, group_id, name, permission, path, component, icon,
    is_link, link_url, status, visible, sort, remark
)
SELECT
    id, delete_time, deleted, create_time, create_by, update_time, update_by,
    type, parent_id, group_id, name, permission, path, component, icon,
    is_link, link_url, status, visible, sort, remark
FROM yeah_boot.sys_res
WHERE id IN (200, 201, 202, 203)
  AND deleted = 0
ON DUPLICATE KEY UPDATE
    parent_id = VALUES(parent_id),
    group_id = VALUES(group_id),
    name = VALUES(name),
    permission = VALUES(permission),
    path = VALUES(path),
    component = VALUES(component),
    icon = VALUES(icon),
    is_link = VALUES(is_link),
    link_url = VALUES(link_url),
    status = VALUES(status),
    visible = VALUES(visible),
    sort = VALUES(sort),
    remark = VALUES(remark),
    deleted = 0;

-- 同步系统/基础管理的 API 与按钮资源。
-- 页面资源沿用商城库既有 ID，因此按钮资源的父级 ID 在此处做映射。
INSERT INTO yeah_mall.sys_res (
    id, delete_time, deleted, create_time, create_by, update_time, update_by,
    type, parent_id, group_id, name, permission, path, component, icon,
    is_link, link_url, status, visible, sort, remark
)
SELECT
    id, delete_time, deleted, create_time, create_by, update_time, update_by,
    type,
    CASE parent_id
        WHEN 101 THEN 2
        WHEN 102 THEN 3
        WHEN 103 THEN 4
        WHEN 104 THEN 5
        WHEN 105 THEN 6
        WHEN 106 THEN 7
        WHEN 107 THEN 9
        WHEN 20338 THEN 8
        ELSE parent_id
    END,
    group_id, name, permission, path, component, icon,
    is_link, link_url, status, visible, sort, remark
FROM yeah_boot.sys_res
WHERE deleted = 0
  AND (
      (type = 3 AND parent_id IN (101, 102, 103, 104, 105, 106, 107, 201, 202, 203, 20338))
      OR (
          type = 4
          AND (
              permission LIKE 'api:admin:sys%'
              OR permission LIKE 'api:admin:area%'
              OR permission LIKE 'api:admin:auth:%'
              OR permission LIKE 'api:basic:file%'
              OR permission LIKE 'api:basic:storage%'
          )
      )
  )
ON DUPLICATE KEY UPDATE
    parent_id = VALUES(parent_id),
    group_id = VALUES(group_id),
    name = VALUES(name),
    permission = VALUES(permission),
    path = VALUES(path),
    component = VALUES(component),
    icon = VALUES(icon),
    is_link = VALUES(is_link),
    link_url = VALUES(link_url),
    status = VALUES(status),
    visible = VALUES(visible),
    sort = VALUES(sort),
    remark = VALUES(remark),
    deleted = 0;

COMMIT;

-- 执行后，在“资源管理 -> 接口资源/视图资源”中为各角色按最小权限分配新增资源。
-- 不建议按“拥有系统管理菜单”批量授予所有新增删除、导入、资源挂载等操作权限。
