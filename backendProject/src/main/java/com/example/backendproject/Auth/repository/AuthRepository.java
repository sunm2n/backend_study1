package com.example.backendproject.Auth.repository;

import com.example.backendproject.Auth.entity.Auth;
import com.example.backendproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository // 안달아도 되지만 레퍼지토리를 담당하는 클래스이다를 명시하기 위해
public interface AuthRepository extends JpaRepository<Auth, Long> {

    boolean existsByUser(User user);

    Optional<Auth> findByRefreshToken(String refreshToken);

    Optional<Auth> findByUser(User user);
}
