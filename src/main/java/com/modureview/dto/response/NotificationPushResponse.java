package com.modureview.dto.response;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.modureview.entity.Notification;

public record NotificationPushResponse(
	Long id,
	Long board_id,
	String type,
	String title,
	boolean isRead,
	boolean isDeleted,
	@JsonProperty("created_at")
	@JsonFormat(pattern = "MM월 dd일 HH시 mm분",timezone = "Asia/Seoul")
	LocalDateTime createdAt
	) {
		public static NotificationPushResponse from(Notification notification,String title) {
			return new NotificationPushResponse(
				notification.getId(),
				notification.getBoardId(),
				notification.getNotificationType().name(),
				title,
				notification.isRead(),
				notification.isDeleted(),
				notification.getCreatedAt()
			);
		}
	}
