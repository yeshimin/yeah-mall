package com.yeshimin.yeahboot.common.common.serialize;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;

import java.lang.reflect.Type;

public class DefaultDesensitizeSerializer implements ObjectWriter<String> {

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        jsonWriter.writeString("******");
    }
}
