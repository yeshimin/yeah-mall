package com.yeshimin.yeahboot.ws.command;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.yeshimin.yeahboot.ws.mq.command.BaseCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class MsgCommand extends BaseCommand {

    private Payload payload;

    @Data
    public static class Payload {
        @JSONField(serializeFeatures = JSONWriter.Feature.WriteLongAsString)
        @NotNull(message = "会话ID不能为空")
        private Long conversationId;

        @Length(max = 255, message = "content长度不能超过255")
        @NotBlank(message = "content不能为空")
        private String content;

        @NotNull(message = "type不能为空")
        private Integer type;

        /**
         * 已落库消息ID，仅由服务端推送时填写
         */
        @JSONField(serializeFeatures = JSONWriter.Feature.WriteLongAsString)
        private Long messageId;

        /**
         * 消息创建时间，仅由服务端推送时填写
         */
        private LocalDateTime createTime;
    }

    public static MsgCommand parse(BaseCommand baseCommand) {
        return JSON.parseObject(JSON.toJSONString(baseCommand), MsgCommand.class);
    }
}
