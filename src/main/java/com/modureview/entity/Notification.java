package com.modureview.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long receiverUserId;
	private Long senderUserId;
	private Long boardId;

	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;

	private boolean isRead = false;
	private boolean isDeleted = false;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "modified_at")
	private LocalDateTime modifiedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.modifiedAt = LocalDateTime.now();
	}

	@Builder
	public Notification(Long receiverUserId, Long senderUserId, Long boardId,
		NotificationType notificationType, String message) {
		this.receiverUserId = receiverUserId;
		this.senderUserId = senderUserId;
		this.boardId = boardId;
		this.notificationType = notificationType;
	}

	public void markAsRead() {
		this.isRead = true;
	}
	public void markAsDeleted() {
		this.isDeleted = true;
	}



}
