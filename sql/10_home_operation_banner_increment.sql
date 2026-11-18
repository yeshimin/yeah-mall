-- 平台 Banner 排序与启停增量：请人工确认后执行，程序不会自动执行。

START TRANSACTION;

ALTER TABLE plat_banner
    ADD COLUMN sort INT NOT NULL DEFAULT 1
        COMMENT '排序：大于等于1，越小越靠前'
        AFTER image_url,
    ADD COLUMN is_enabled TINYINT(1) NOT NULL DEFAULT 1
        COMMENT '是否启用：1-启用，0-禁用'
        AFTER sort,
    ADD KEY idx_plat_banner_enabled_sort (is_enabled, sort, id);

COMMIT;
