-- 平台分类菜单增量：请人工确认后执行，程序不会自动执行。

START TRANSACTION;

SET @platform_category_seed_id := CAST(
    (UNIX_TIMESTAMP(CURRENT_TIMESTAMP(3)) * 1000 - 1288834974657) * 4194304 + 3200 AS UNSIGNED
);

SET @mall_parent_id := (
    SELECT parent_id
    FROM sys_res
    WHERE deleted = 0
      AND component = 'mall/member/index'
    LIMIT 1
);

SET @platform_category_sort := (
    SELECT COALESCE(MAX(sort), 0) + 1
    FROM sys_res
    WHERE deleted = 0
      AND parent_id = COALESCE(@mall_parent_id, 27)
);

SET @platform_category_res_id := (
    SELECT id
    FROM sys_res
    WHERE deleted = 0
      AND component = 'mall/platform-category/index'
    LIMIT 1
);

INSERT INTO sys_res
    (id, type, parent_id, group_id, name, permission, path, component, icon,
     is_link, link_url, status, visible, sort, remark)
SELECT @platform_category_seed_id + 1, 2, COALESCE(@mall_parent_id, 27), 0,
       '平台分类', '', 'platform-category', 'mall/platform-category/index', 'CollectionTag',
       0, '', '1', 1, @platform_category_sort, 'App分类导航，由分类名称进入商品搜索'
WHERE @platform_category_res_id IS NULL;

SET @platform_category_res_id := (
    SELECT id
    FROM sys_res
    WHERE deleted = 0
      AND component = 'mall/platform-category/index'
    LIMIT 1
);

UPDATE sys_res
SET name = '平台分类',
    path = 'platform-category',
    icon = 'CollectionTag',
    status = '1',
    visible = 1,
    remark = 'App分类导航，由分类名称进入商品搜索',
    update_time = NOW()
WHERE id = @platform_category_res_id;

INSERT INTO sys_role_res (id, role_id, res_id, mount_id)
SELECT @platform_category_seed_id + 2, 1, @platform_category_res_id, 0
WHERE @platform_category_res_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM sys_role_res
    WHERE deleted = 0
      AND role_id = 1
      AND res_id = @platform_category_res_id
      AND mount_id = 0
);

UPDATE mch_res
SET name = '店铺商品分类',
    remark = '维护当前店铺自己的商品分类',
    update_time = NOW()
WHERE deleted = 0
  AND component = 'mall/category/index';

COMMIT;
