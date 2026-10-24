-- 执行 ALTER 前先检查是否存在重复有效会话；有结果时需先合并重复会话及其消息。
SELECT mch_id, shop_id, member_id, COUNT(*) AS duplicate_count
FROM s_cs_conversation
WHERE deleted = 0
GROUP BY mch_id, shop_id, member_id
HAVING COUNT(*) > 1;

-- 同一商家、店铺和买家只保留一个有效会话。
ALTER TABLE s_cs_conversation
    ADD UNIQUE KEY uk_mch_shop_member_delete_time (mch_id, shop_id, member_id, delete_time);

-- 支撑会话历史消息按主键倒序分页。
ALTER TABLE s_cs_message
    ADD KEY idx_conversation_id_id (conversation_id, id);
