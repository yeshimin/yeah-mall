package com.yeshimin.yeahboot.ws.command;

import lombok.Data;

@Data
public class BaseCommand {

    private String command;

    private Object payload;
}
