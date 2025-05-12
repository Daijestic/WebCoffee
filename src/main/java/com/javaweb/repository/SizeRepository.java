package com.javaweb.repository;

import com.javaweb.entity.SizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SizeRepository extends JpaRepository<SizeEntity, Long> {
    Optional<SizeEntity> findByTenSize(String tenSize);

}
