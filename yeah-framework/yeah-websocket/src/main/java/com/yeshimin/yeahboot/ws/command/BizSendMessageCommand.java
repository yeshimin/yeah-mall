package com.yeshimin.yeahboot.ws.command;

import lombok.Data;

@Data
public class BizSendMessageCommand extends BaseCommand {

    private Object payload;
}
