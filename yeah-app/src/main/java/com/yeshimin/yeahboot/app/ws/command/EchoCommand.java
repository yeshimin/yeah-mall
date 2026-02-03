package com.yeshimin.yeahboot.app.ws.command;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.ws.mq.command.BaseCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EchoCommand extends BaseCommand {

    private Payload payload;

    @Data
    public static class Payload {
        private String content;
    }

    public static EchoCommand parse(BaseCommand baseCommand) {
        EchoCommand echoCommand = JSON.parseObject(JSON.toJSONString(baseCommand), EchoCommand.class);
        echoCommand.getPayload().setContent("echo: " + echoCommand.getPayload().getContent());
        return echoCommand;
    }
}
