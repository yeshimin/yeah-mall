package com.yeshimin.yeahboot.admin.notification;

import com.yeshimin.yeahboot.notification.common.enums.NotifMessageTargetTypeEnum;
import com.yeshimin.yeahboot.notification.common.enums.NotifMessageTypeEnum;
import com.yeshimin.yeahboot.notification.event.NotifMessageCreatedEvent;
import com.yeshimin.yeahboot.ws.websocket.service.WebSocketService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AdminNotifMessagePushListenerTest {

    @Test
    void globalMessageOnlyPushesToPersistedReceiverSnapshot() {
        WebSocketService webSocketService = mock(WebSocketService.class);
        AdminNotifMessagePushListener listener = new AdminNotifMessagePushListener(webSocketService);
        NotifMessageCreatedEvent event = new NotifMessageCreatedEvent(
                100L,
                "系统通告",
                NotifMessageTypeEnum.ANNOUNCEMENT.getValue(),
                NotifMessageTargetTypeEnum.ALL.getValue(),
                "admin",
                Arrays.asList(10L, 11L));

        listener.handle(event);

        verify(webSocketService, times(2)).sendMessageToUser(anyString(), anyString(), anyString());
        verify(webSocketService, never()).sendMessageBroadcast(anyString(), anyString());
    }
}
