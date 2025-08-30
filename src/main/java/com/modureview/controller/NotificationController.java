package com.modureview.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.modureview.dto.request.NotificationRequest;
import com.modureview.dto.response.CustomPageResponse;
import com.modureview.dto.response.NotificationPushResponse;
import com.modureview.service.NotificationService;
import com.modureview.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users/me/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
	private final NotificationService notificationService;
	private final UserService userService;

	@GetMapping
	public ResponseEntity<CustomPageResponse<NotificationPushResponse>> getAllNotifications(
		@CookieValue("userEmail")String userEmail,
		@RequestParam(defaultValue = "1") int page
	){
		Long userId = userService.findUserId(userEmail);
		 log.info("알람 목록 조회 요청 - 사용자 이메일 : {} , ID : {} ", userEmail, userId);
		Page<NotificationPushResponse> result = notificationService.getNotifications(userId, page);

		List<NotificationPushResponse> results = result.getContent();
		CustomPageResponse<NotificationPushResponse> resp = new CustomPageResponse<>(
			results,
			result.getNumber()+1,
			result.getTotalPages()
		);


		 return ResponseEntity.ok(resp);
	}

	@GetMapping("/unread")
	public ResponseEntity<Boolean> hasUnread(@CookieValue("userEmail")String userEmail){
		Long userId = userService.findUserId(userEmail);
		boolean exists = notificationService.hasUnread(userId);
		return ResponseEntity.ok(exists);
	}

	@PatchMapping("/{notificationId}")
	public ResponseEntity<Void> updateNotification(
		@PathVariable Long notificationId,
		@CookieValue("userEmail") String userEmail,
		@RequestBody NotificationRequest request
	){
		if(request == null){
			return ResponseEntity.badRequest().build();
		}
		Long userId = userService.findUserId(userEmail);

		boolean read = request.isRead();
		boolean delete = request.isDeleted();


		if (read == delete ){
			log.warn("잘못된 알림 업데이트 요청 - notificationId : {} , read: {} , delete: {} ", notificationId, read, delete);
			return ResponseEntity.badRequest().build();
		}
		if(delete){
			notificationService.markAsDeleted(notificationId);
		}else{
			notificationService.markAsRead(notificationId);
		}
		return ResponseEntity.ok().build();


	}
}