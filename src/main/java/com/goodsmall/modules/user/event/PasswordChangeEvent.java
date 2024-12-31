package com.goodsmall.modules.user.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PasswordChangeEvent extends ApplicationEvent {
    private final Long userId;
    public PasswordChangeEvent(Object source,Long userId) {
        super(source);
        this.userId = userId;
    }
}
