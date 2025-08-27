package com.modureview.controller;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.modureview.dto.response.MetaDto;
import com.modureview.enums.errors.TokenErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.service.NotificationService;
import com.modureview.service.NotificationSseService;
import com.modureview.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationSseController {
	private final NotificationSseService sseService;
	private final NotificationService notificationService;
	private final UserService userService;

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter stream(
		@CookieValue("userNickname") String userNickname,
		HttpServletResponse response
	) {
		if (userNickname == null || userNickname.isEmpty()) {
			String now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
				.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			throw new CustomException(TokenErrorCode.NICKNAME_NOT_FOUND, now);
		}

		// SSE 친화적 헤더
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("X-Accel-Buffering", "no");

		Long userId = userService.findUserIdByNickname(userNickname);
		log.info("SSE stream request for userId : {}", userId);

		// 명시적 타임아웃 (30분)
		long timeoutMs = 30 * 60 * 1000L;
		log.info("Creating SseEmitter for userId: {}", userId);

		SseEmitter emitter = sseService.createSseEmitter(userId, timeoutMs);
		log.info("SseEmitter created for userId: {}. Emitter: {}", userId, emitter);

		boolean hasUnread = notificationService.hasUnread(userId);
		log.info("Unread status for userId: {} is {}", userId, hasUnread);


		// 초기 meta 이벤트는 1회만, 약간의 지연 후 전송 (전송 성공 여부 로깅)
		scheduler.schedule(() -> {
			boolean ok = sseService.sendMeta(userId, new MetaDto(hasUnread));
			if (ok) {
				log.info("Initial 'meta' event delivered for userId: {}", userId);
			} else {
				log.info("Initial 'meta' event NOT delivered (no active emitters) for userId: {}", userId);
			}
		}, 150, TimeUnit.MILLISECONDS);

		// Heartbeat (핑)
		ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
			() -> {
				boolean ok = sseService.sendPing(userId);
				if (!ok) {
					log.debug("No active emitters for ping. userId: {}", userId);
				}
			},
			20, 20, TimeUnit.SECONDS
		);

		emitter.onCompletion(() -> {
			log.debug("Emitter completed. userId: {}", userId);
			future.cancel(false);
		});
		emitter.onTimeout(() -> {
			log.debug("Emitter timeout. userId: {}", userId);
			future.cancel(false);
		});
		emitter.onError(e -> {
			log.debug("Emitter error. userId: {}, cause: {}", userId, e.toString
				());
			future.cancel(false);
		});

		return emitter;
	}
}
