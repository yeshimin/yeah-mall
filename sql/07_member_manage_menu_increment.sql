-- 平台会员管理菜单增量：请人工确认后执行，程序不会自动执行。

SET @member_manage_seed_id := CAST(
    (UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000 - 1288834974657) * 4194304 + 3000 AS UNSIGNED
);

SET @mall_parent_id := (
    SELECT parent_id
    FROM sys_res
    WHERE deleted = 0
      AND component = 'mall/member-points/index'
    LIMIT 1
);

UPDATE sys_res
SET name = '积分管理',
    remark = '全局积分流水与积分规则管理',
    update_time = NOW()
WHERE deleted = 0
  AND component = 'mall/member-points/index';

SET @member_manage_res_id := (
    SELECT id
    FROM sys_res
    WHERE deleted = 0
      AND component = 'mall/member/index'
    LIMIT 1
);

INSERT INTO sys_res
    (id, type, parent_id, group_id, name, permission, path, component, icon,
     is_link, link_url, status, visible, sort, remark)
SELECT @member_manage_seed_id + 1, 2, COALESCE(@mall_parent_id, 27), 0,
       '会员管理', '', 'member', 'mall/member/index', 'User',
       0, '', '1', 1, 4, '平台会员资料与会员资产管理'
WHERE @member_manage_res_id IS NULL;

SET @member_manage_res_id := (
    SELECT id
    FROM sys_res
    WHERE deleted = 0
      AND component = 'mall/member/index'
    LIMIT 1
);

INSERT INTO sys_role_res (id, role_id, res_id, mount_id)
SELECT @member_manage_seed_id + 2, 1, @member_manage_res_id, 0
WHERE NOT EXISTS (
    SELECT 1
    FROM sys_role_res
    WHERE deleted = 0
      AND role_id = 1
      AND res_id = @member_manage_res_id
      AND mount_id = 0
);
