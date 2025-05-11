package com.javaweb.repository;

import com.javaweb.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>, PagingAndSortingRepository<UserEntity, Long> {
    Optional<UserEntity> findByIdUser(Long aLong);
    void deleteByIdUser(Long id);
    boolean existsByIdUser(Long id);
    Optional<UserEntity> findByEmail(String email);
    Page<UserEntity> findAllByLoaiUser(String role, Pageable pageable);
    Optional<UserEntity> findByDangNhap(String username);
    List<UserEntity> findAllByLoaiUser(String role);
}
