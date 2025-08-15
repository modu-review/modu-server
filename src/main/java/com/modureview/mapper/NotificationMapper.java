package com.modureview.mapper;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.modureview.dto.response.NotificationPushResponse;
import com.modureview.entity.Notification;

public class NotificationMapper {
	private static final int TITLE_LIMIT = 15;

	private NotificationMapper(){}

	public static Page<NotificationPushResponse> toResponsePage(
		Page<Notification> page, Function<Long , String> titleResolver
	) {
		List<NotificationPushResponse> content = page.getContent().stream()
			.map(n -> NotificationPushResponse.from(
				n, ellipsize(titleResolver.apply(n.getBoardId()), TITLE_LIMIT)
			)).toList();

		return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
	}

	private static String ellipsize(String s , int limit) {
		if(s == null )
			return "";
		int count = s.codePointCount(0, s.length());
		if(count < limit ) return s;
		int endIndex = s.offsetByCodePoints(0, limit);
		return s.substring(0, endIndex) + "...";
	}


}
