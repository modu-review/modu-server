package com.modureview.repository;

import com.modureview.entity.Category;
import com.modureview.entity.CategoryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

  Optional<CategoryEntity> findByCategoryName(Category categoryName);
}