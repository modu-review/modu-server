package com.modureview.repository;

import com.modureview.entity.Bookmarks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmarks, Long> {

}
