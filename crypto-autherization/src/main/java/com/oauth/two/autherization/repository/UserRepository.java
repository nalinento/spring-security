package com.oauth.two.autherization.repository;

import com.oauth.two.autherization.entity.UserDetail;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDetail,Long> {
    UserDetail findByEmail(String email);
}
