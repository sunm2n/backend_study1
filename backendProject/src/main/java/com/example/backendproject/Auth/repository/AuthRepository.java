package com.example.backendproject.Auth.repository;

import com.example.backendproject.Auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository // 안달아도 되지만 레퍼지토리를 담당하는 클래스이다를 명시하기 위해
public interface AuthRepository extends JpaRepository<Auth, Long> {


}
