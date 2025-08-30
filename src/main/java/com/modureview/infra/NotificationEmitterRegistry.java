package com.modureview.infra;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationEmitterRegistry {
	private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(Long userId , long timeoutMs){
        SseEmitter emitter = new SseEmitter(timeoutMs);
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError((e)->{
            log.debug("NotificationEmitterRegistry :: register :: SSE emitter 에러입니다. userId = {} . cause = {} ", userId, e.toString());
            remove(userId , emitter);
        });
        return emitter;
    }

	public void remove(Long userId, SseEmitter emitter){
		List<SseEmitter> list = emitters.get(userId);
		if(list != null){
			list.remove(emitter);
			if(list.isEmpty()){
				emitters.remove(userId);
			}
		}
	}

    public boolean send(Long userId,String eventName , Object data){
        List<SseEmitter> list = emitters.get(userId);
        if(list == null || list.isEmpty()) return false;

        boolean delivered = false;

        for(SseEmitter emitter : List.copyOf(list)){
            try{
                emitter.send(SseEmitter.event().name(eventName).data(data));
                delivered = true;
                log.debug("NotificationEmitterRegistry :: send :: sent. userId={} , event={} , emitter={}", userId, eventName, emitter);
            }catch(Exception e){
                log.debug("NotificationEmitterRegistry :: send :: 발송 오류입니다. userId = {} , event = {} , cause = {} ", userId, eventName,e.toString());
                remove(userId,emitter);
            }
        }
        return delivered;
    }

    public boolean sendPing(Long userId){
        List<SseEmitter> list = emitters.get(userId);
        if(list == null || list.isEmpty()) return false;
        boolean delivered = false;
        for(SseEmitter emitter : List.copyOf(list)){
            try{
                emitter.send(SseEmitter.event().name("ping").comment("keep-alive"));
                delivered = true;
                log.debug("NotificationEmitterRegistry :: sendPing :: sent. userId={} , emitter={}", userId, emitter);
            } catch(Exception e){
                log.debug("NotificationEmitterRegistry :: sendPing :: 핑 발송 오류입니다. userId = {} . cause = {} ", userId, e.toString());
                remove(userId,emitter);
            }
        }
        return delivered;
    }
}
