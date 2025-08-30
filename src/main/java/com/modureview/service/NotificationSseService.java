package com.modureview.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.modureview.dto.response.MetaDto;
import com.modureview.dto.response.NotificationPushResponse;
import com.modureview.infra.NotificationEmitterRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSseService {
	private final NotificationEmitterRegistry emitterRegistry;

	public SseEmitter createSseEmitter(Long userId,long timeoutMs){
		return emitterRegistry.register(userId, timeoutMs);
	}

    public boolean sendMeta(Long userId, MetaDto meta){
        boolean ok = emitterRegistry.send(userId, "meta", meta);
        log.debug("NotificationSseService :: sendMeta :: userId={} , delivered={}", userId, ok);
        return ok;
    }

    public boolean sendNotification(Long userId , NotificationPushResponse payload){
        boolean ok = emitterRegistry.send(userId, "notification", payload);
        log.debug("NotificationSseService :: sendNotification :: userId={} , notificationId={} , delivered={}",
            userId, payload.id(), ok);
        return ok;
    }

    public boolean sendPing(Long userId){
        boolean ok = emitterRegistry.sendPing(userId);
        log.debug("NotificationSseService :: sendPing :: userId={} , delivered={}", userId, ok);
        return ok;
    }
}
