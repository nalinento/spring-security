package com.oauth.two.cryptoagent.repository;

import com.oauth.two.cryptoagent.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends
        JpaRepository<VerificationToken,Long> {
    @Query("SELECT v FROM VerificationToken v WHERE v.token =?1")
    VerificationToken findByToken(String token);
}
