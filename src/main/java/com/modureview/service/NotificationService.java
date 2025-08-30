package com.modureview.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.modureview.dto.response.NotificationPushResponse;
import com.modureview.entity.Board;
import com.modureview.entity.Notification;
import com.modureview.entity.NotificationType;
import com.modureview.enums.errors.BoardErrorCode;
import com.modureview.enums.errors.UserErrorCode;
import com.modureview.exception.CustomException;
import com.modureview.mapper.NotificationMapper;
import com.modureview.repository.BoardRepository;
import com.modureview.repository.NotificationRepository;
import com.modureview.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final BoardRepository boardRepository;

	@Transactional
	public NotificationPushResponse sendNotification(Long receiverUserId,Long senderUserId,Long boardId,
		NotificationType type) {
		log.debug("Create notification: receiver={}, sender={}, board={}, type={}",
			receiverUserId, senderUserId, boardId, type);
		userRepository.findById(receiverUserId).orElseThrow(
			() -> new CustomException(UserErrorCode.USER_NOT_FOUND)
		);
		Notification notification = Notification.builder()
			.receiverUserId(receiverUserId)
			.senderUserId(senderUserId)
			.boardId(boardId)
			.notificationType(type)
			.build();
		notificationRepository.save(notification);
		log.info("Notification persisted: id={}", notification.getId());
		Board targetBoard = boardRepository.findById(boardId)
			.orElseThrow(() -> new CustomException(BoardErrorCode.BOARD_ID_NOTFOUND));
		return NotificationPushResponse.from(notification, targetBoard.getTitle());
	}

	@Transactional(readOnly = true)
	public Page<NotificationPushResponse> getNotifications(Long userId,int page) {
		Pageable pageable = PageRequest.of(page -1 , 12 , Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<Notification> result = notificationRepository.findByReceiverUserIdAndIsDeletedFalse(userId, pageable);

		if(result.isEmpty()){
			return new PageImpl<>(List.of(),pageable,0);
		}

		List<Long> boardIds = result.getContent().stream()
			.map(Notification::getBoardId)
			.filter(Objects::nonNull)
			.distinct()
			.toList();

		Map<Long, String> titleMap = boardRepository.findAllById(boardIds).stream()
			.collect(Collectors.toMap(Board::getId, Board::getTitle));

		Function<Long , String> titleResolver =
			id -> titleMap.getOrDefault(id, "원본 게시물이 없음");

		return NotificationMapper.toResponsePage(result, titleResolver);
	}

	@Transactional
	public void markAsRead(Long notificationId) {
		notificationRepository.findById(notificationId).ifPresent(Notification::markAsRead);
	}

	@Transactional
	public void markAsDeleted(Long notificationId) {
		notificationRepository.findById(notificationId).ifPresent(Notification::markAsDeleted);
	}

	@Transactional(readOnly = true)
	public boolean hasUnread(Long userId){
		return notificationRepository.countUnreadNotifications(userId) > 0;
	}




}
