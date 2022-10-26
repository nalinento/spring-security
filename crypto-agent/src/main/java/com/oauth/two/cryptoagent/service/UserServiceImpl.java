package com.oauth.two.cryptoagent.service;

import com.oauth.two.cryptoagent.entity.PasswordResetToken;
import com.oauth.two.cryptoagent.entity.UserDetail;
import com.oauth.two.cryptoagent.entity.VerificationToken;
import com.oauth.two.cryptoagent.model.UserModel;
import com.oauth.two.cryptoagent.repository.PasswordResetTokenRepository;
import com.oauth.two.cryptoagent.repository.UserRepository;
import com.oauth.two.cryptoagent.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetail findUseByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetail userRegistration(UserModel userModel) {
        UserDetail user = new UserDetail();
        user.setEmail(userModel.getEmail());
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
          userRepository.save(user);
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(UserDetail user, String token) {
        VerificationToken verificationToken = new VerificationToken(token,user);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken =verificationTokenRepository.findByToken(token);
        if (verificationToken == null){
            return "Invalid";
        }
        UserDetail userDetail = verificationToken.getUser();
        Calendar cal =Calendar.getInstance();

        if ((verificationToken.getExpiration().getTime()-cal.getTime().getTime()) <=0){
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }
        userDetail.setEnable(true);
        userRepository.save(userDetail);
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken =
                verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public void createPasswordResetTokenForUser(UserDetail userDetail, String token) {
        PasswordResetToken passwordResetToken=
                new PasswordResetToken(token,userDetail);
        passwordResetTokenRepository.save(passwordResetToken);

    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken   passwordResetToken =passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null){
            return "Invalid";
        }
        UserDetail userDetail = passwordResetToken.getUser();
        Calendar cal =Calendar.getInstance();

        if ((passwordResetToken.getExpiration().getTime()-cal.getTime().getTime()) <=0){
            passwordResetTokenRepository.delete(passwordResetToken);
            return "expired";
        }

        return "valid";
    }

    @Override
    public Optional<UserDetail> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePasswor(UserDetail userDetail, String newPassword) {
        userDetail.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userDetail);
    }

    @Override
    public boolean checkIfValidOldPassword(UserDetail userDetail, String oldPassword) {

        return passwordEncoder.matches(oldPassword,userDetail.getPassword());
    }
}
