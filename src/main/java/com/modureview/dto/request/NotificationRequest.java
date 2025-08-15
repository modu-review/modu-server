package com.modureview.dto.request;

public record NotificationRequest(
	boolean isRead,
	boolean isDelete
) {
}
