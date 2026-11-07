package com.yeshimin.yeahboot.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.notification.domain.dto.NotifMessageQueryDto;
import com.yeshimin.yeahboot.notification.domain.entity.NotifMessageReceiverEntity;
import com.yeshimin.yeahboot.notification.domain.vo.NotifMessageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotifMessageReceiverMapper extends BaseMapper<NotifMessageReceiverEntity> {

    /**
     * 分页查询用户站内信。
     */
    IPage<NotifMessageVo> queryMine(Page<NotifMessageVo> page,
                                    @Param("receiverSubject") String receiverSubject,
                                    @Param("receiverUserId") Long receiverUserId,
                                    @Param("query") NotifMessageQueryDto query);

    /**
     * 查询用户最近站内信。
     */
    List<NotifMessageVo> findRecent(@Param("receiverSubject") String receiverSubject,
                                    @Param("receiverUserId") Long receiverUserId,
                                    @Param("limit") int limit);
}
