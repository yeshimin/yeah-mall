-- 平台/商家后台菜单排序与图标统一。
-- 先执行 03_yeahboot_system_resource_migration.sql，再执行本脚本。
-- 本脚本仅更新菜单资源，不调整角色资源关联。

START TRANSACTION;

-- 平台根菜单：静态首页始终在前，动态菜单依次为系统、基础、商城。
UPDATE yeah_mall.sys_res
SET sort = CASE id
        WHEN 1 THEN 1
        WHEN 200 THEN 2
        WHEN 27 THEN 3
        ELSE sort
    END,
    icon = CASE id
        WHEN 1 THEN 'Setting'
        WHEN 200 THEN 'Files'
        WHEN 27 THEN 'ShoppingBag'
        ELSE icon
    END
WHERE id IN (1, 200, 27)
  AND deleted = 0;

-- 系统/基础管理图标统一为 Element Plus 图标名称。
UPDATE yeah_mall.sys_res
SET icon = CASE id
        WHEN 2 THEN 'User'
        WHEN 3 THEN 'Avatar'
        WHEN 4 THEN 'Menu'
        WHEN 5 THEN 'OfficeBuilding'
        WHEN 6 THEN 'Briefcase'
        WHEN 7 THEN 'Collection'
        WHEN 8 THEN 'EditPen'
        WHEN 9 THEN 'Document'
        WHEN 201 THEN 'Location'
        WHEN 202 THEN 'Folder'
        WHEN 203 THEN 'UploadFilled'
        ELSE icon
    END
WHERE id IN (2, 3, 4, 5, 6, 7, 8, 9, 201, 202, 203)
  AND deleted = 0;

-- 商城管理：店铺置于秒杀前，秒杀与拼团相邻。
UPDATE yeah_mall.sys_res
SET sort = CASE id
        WHEN 21 THEN 1
        WHEN 26 THEN 2
        WHEN 2020501160789094402 THEN 3
        WHEN 2033192082802827266 THEN 4
        WHEN 2019977970853728257 THEN 5
        WHEN 2019691972324311042 THEN 6
        WHEN 2033193419871444995 THEN 7
        ELSE sort
    END,
    icon = CASE id
        WHEN 21 THEN 'Shop'
        WHEN 26 THEN 'Shop'
        WHEN 2020501160789094402 THEN 'Lightning'
        WHEN 2033192082802827266 THEN 'Connection'
        WHEN 2019977970853728257 THEN 'Grid'
        WHEN 2019691972324311042 THEN 'Picture'
        WHEN 2033193419871444995 THEN 'ChatDotRound'
        ELSE icon
    END
WHERE id IN (
    21, 26, 2020501160789094402, 2033192082802827266,
    2019977970853728257, 2019691972324311042, 2033193419871444995
)
  AND deleted = 0;

-- 秒杀、拼团子菜单补齐图标并统一顺序。
UPDATE yeah_mall.sys_res
SET sort = CASE id
        WHEN 2020507667362557954 THEN 1
        WHEN 2020507975752953858 THEN 2
        WHEN 2020508040148103170 THEN 3
        WHEN 2020508121337245697 THEN 4
        WHEN 2020508194729177090 THEN 5
        WHEN 2033192896908845058 THEN 1
        WHEN 2033193160902533122 THEN 2
        WHEN 2033193309141819393 THEN 3
        WHEN 2033193419871444994 THEN 4
        ELSE sort
    END,
    icon = CASE id
        WHEN 2020507667362557954 THEN 'Calendar'
        WHEN 2020507975752953858 THEN 'Goods'
        WHEN 2020508040148103170 THEN 'DocumentChecked'
        WHEN 2020508121337245697 THEN 'Monitor'
        WHEN 2020508194729177090 THEN 'DataAnalysis'
        WHEN 2033192896908845058 THEN 'Calendar'
        WHEN 2033193160902533122 THEN 'Goods'
        WHEN 2033193309141819393 THEN 'Tickets'
        WHEN 2033193419871444994 THEN 'DocumentChecked'
        ELSE icon
    END
WHERE id IN (
    2020507667362557954, 2020507975752953858, 2020508040148103170,
    2020508121337245697, 2020508194729177090,
    2033192896908845058, 2033193160902533122, 2033193309141819393,
    2033193419871444994
)
  AND deleted = 0;

-- 商家后台：静态首页在前，动态资源仅保留商城管理根菜单。
UPDATE yeah_mall.mch_res
SET sort = CASE id
        WHEN 1 THEN 1
        WHEN 2 THEN 1
        WHEN 5 THEN 2
        WHEN 3 THEN 3
        WHEN 4 THEN 4
        WHEN 7 THEN 5
        WHEN 8 THEN 6
        WHEN 6 THEN 7
        WHEN 9 THEN 8
        WHEN 10 THEN 9
        WHEN 14 THEN 10
        WHEN 19 THEN 11
        WHEN 23 THEN 12
        ELSE sort
    END,
    icon = CASE id
        WHEN 1 THEN 'ShoppingBag'
        WHEN 2 THEN 'Collection'
        WHEN 5 THEN 'Operation'
        WHEN 3 THEN 'Goods'
        WHEN 4 THEN 'Picture'
        WHEN 7 THEN 'Tickets'
        WHEN 8 THEN 'ChatDotRound'
        WHEN 6 THEN 'Van'
        WHEN 9 THEN 'Setting'
        WHEN 10 THEN 'Lightning'
        WHEN 14 THEN 'Connection'
        WHEN 19 THEN 'Ticket'
        WHEN 23 THEN 'ChatDotRound'
        WHEN 11 THEN 'Calendar'
        WHEN 12 THEN 'Goods'
        WHEN 13 THEN 'DocumentChecked'
        WHEN 15 THEN 'Calendar'
        WHEN 16 THEN 'Goods'
        WHEN 17 THEN 'DocumentChecked'
        WHEN 18 THEN 'Tickets'
        WHEN 20 THEN 'Ticket'
        WHEN 21 THEN 'Document'
        WHEN 22 THEN 'Collection'
        ELSE icon
    END
WHERE deleted = 0;

COMMIT;
