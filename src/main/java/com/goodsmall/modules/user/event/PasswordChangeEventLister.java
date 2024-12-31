package com.goodsmall.modules.user.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordChangeEventLister {
//    private final LogoutTimestampService logoutTimestampService;
//
//    @EventListener
//    @Async
//    public void onPasswordChange(final PasswordChangeEvent event) {
//        logoutTimestampService.updateLogoutTimestamp(event.getUserId());
//    }
}
