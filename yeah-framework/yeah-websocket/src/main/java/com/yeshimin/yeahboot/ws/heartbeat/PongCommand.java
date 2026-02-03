package com.yeshimin.yeahboot.ws.heartbeat;

import com.yeshimin.yeahboot.ws.mq.command.BaseCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PongCommand extends BaseCommand {

    public PongCommand() {
        super.setCategory("heartbeat");
        super.setCommand("pong");
    }
}
