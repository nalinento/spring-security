package com.oauth.two.cryptoagent.repository;

import com.oauth.two.cryptoagent.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDetail,Long> {
    UserDetail findByEmail(String email);
}
