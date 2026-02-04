package com.yeshimin.yeahboot.merchant.ws.command;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.ws.mq.command.BaseCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class MsgCommand extends BaseCommand {

    private Payload payload;

    @Data
    public static class Payload {
        @NotNull(message = "shopId不能为空")
        private Long shopId;
        @NotNull(message = "from不能为空")
        private Long from;
        @NotNull(message = "to不能为空")
        private Long to;
        @Length(max = 255, message = "content长度不能超过255")
        @NotBlank(message = "content不能为空")
        private String content;
        @NotNull(message = "type不能为空")
        private Integer type;
    }

    public static MsgCommand parse(BaseCommand baseCommand) {
        return JSON.parseObject(JSON.toJSONString(baseCommand), MsgCommand.class);
    }
}
