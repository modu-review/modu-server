package com.modureview.entity;

import lombok.Getter;

@Getter
public enum NotificationType {
	bookmark("북마크"), comment("댓글");

	private final String description;

	NotificationType(String description) {
		this.description = description;
	}
}
