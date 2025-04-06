package com.javaweb.repository;

import com.javaweb.entity.MonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonRepository extends JpaRepository<MonEntity, Long> {
    List<MonEntity> findByTenMonContaining(String name);
}
