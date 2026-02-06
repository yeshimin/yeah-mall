package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.CsConversationQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import com.yeshimin.yeahboot.data.domain.vo.CsConversationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CsConversationMapper extends BaseMapper<CsConversationEntity> {

    /**
     * 查询会话列表
     */
    Page<CsConversationVo> query(Page<CsConversationVo> page,
                                 @Param("mchId") Long mchId,
                                 @Param("query") CsConversationQueryDto query);
}
