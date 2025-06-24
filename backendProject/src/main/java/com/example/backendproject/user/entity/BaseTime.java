package com.example.backendproject.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter

@EntityListeners(AuditingEntityListener.class)
//JPA엔티티의 생성/수정 시점을 자동으로 기록하도록하는 이벤트 리스너로 동작하도록하는 어노테이션

@MappedSuperclass
//이 클래스를 상속받는 엔티티들은 이 클래스의 필드를 컬럼으로 포함시켜라는 어노테이션
public abstract class BaseTime {

    //엔티티가 저잘될때 자동으로 시간을 기록
    @CreatedDate
    private LocalDateTime created_date;

    //수정될대마다 기록
    @LastModifiedDate
    private LocalDateTime updated_date;
}