package com.modureview.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.modureview.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	@Query("SELECT n FROM Notification n WHERE n.receiverUserId = :userId AND n.isDeleted = false ORDER BY n.createdAt DESC")
	List<Notification> findActiveNotifications(@Param("userId") Long userId);

	@Query("SELECT COUNT(n) FROM Notification n WHERE n.receiverUserId = :userId AND n.isRead = false AND n.isDeleted = false")
	int countUnreadNotifications(@Param("userId") Long userId);

	Page<Notification> findByReceiverUserIdAndIsDeletedFalse(Long userId, Pageable pageable);
}
