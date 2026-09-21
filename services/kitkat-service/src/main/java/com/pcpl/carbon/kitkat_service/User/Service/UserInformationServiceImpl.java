package com.pcpl.carbon.kitkat_service.User.Service;

import com.pcpl.carbon.kitkat_service.User.Repository.UserRepository;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInformationServiceImpl implements UserInformationService {

    @Autowired
    UserRepository userRepository;
    @Override
    public User getUser() {
        JwtAuthenticationToken authentication  =
                (JwtAuthenticationToken) SecurityContextHolder
                        .getContext()
                        .getAuthentication();
        String userName = authentication.getName();
        Optional<User> optionalUser = userRepository.findUserByUserNameAndIsDeleted(userName,0);
        User user;
        user = new User();
        if(optionalUser.isPresent()){
            user = optionalUser.get();
        }
        return user;
    }
}
