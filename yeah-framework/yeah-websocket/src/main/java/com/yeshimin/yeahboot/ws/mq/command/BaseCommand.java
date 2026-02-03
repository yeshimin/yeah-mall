package com.yeshimin.yeahboot.ws.mq.command;

import lombok.Data;

@Data
public class BaseCommand {

    private String category;

    private String command;

    private String subCmd;

    private Object payload;
}
