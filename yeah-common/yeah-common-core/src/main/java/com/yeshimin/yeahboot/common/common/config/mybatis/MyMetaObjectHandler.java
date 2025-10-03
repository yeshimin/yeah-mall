package com.yeshimin.yeahboot.common.common.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 实体类字段填充处理器
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        String operator = this.getOperator();

        // id置空，自动生成
        metaObject.setValue("id", null);

        this.setFieldValByName("deleted", 0, metaObject);
        this.setFieldValByName("deleteTime", CommonConsts.MAX_TIME, metaObject);

        this.setFieldValByName("createTime", now, metaObject);
        this.setFieldValByName("createBy", operator, metaObject);

        this.setFieldValByName("updateTime", now, metaObject);
        this.setFieldValByName("updateBy", operator, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        String operator = this.getOperator();

        this.setFieldValByName("updateTime", now, metaObject);
        this.setFieldValByName("updateBy", operator, metaObject);

        // createTime, createBy 置空  !! 这里设置为null无效，改为字段设置updateStrategy = FieldStrategy.NEVER
//        this.setFieldValByName("createTime", null, metaObject);
//        this.setFieldValByName("createBy", null, metaObject);
    }

    private String getOperator() {
        return Optional.ofNullable(WebContextUtils.getUsername()).orElse("");
    }
}
