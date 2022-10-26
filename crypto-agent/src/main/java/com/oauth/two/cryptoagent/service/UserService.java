package com.oauth.two.cryptoagent.service;

import com.oauth.two.cryptoagent.entity.UserDetail;
import com.oauth.two.cryptoagent.entity.VerificationToken;
import com.oauth.two.cryptoagent.model.UserModel;

import java.util.Optional;

public interface UserService {


     UserDetail findUseByEmail(String email);

    UserDetail userRegistration(UserModel userModel);

    void saveVerificationTokenForUser(UserDetail user, String token);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    void createPasswordResetTokenForUser(UserDetail userDetail, String token);

    String validatePasswordResetToken(String token);

    Optional<UserDetail> getUserByPasswordResetToken(String token);

    void changePasswor(UserDetail userDetail, String newPassword);

    boolean checkIfValidOldPassword(UserDetail userDetail, String oldPassword);
}
