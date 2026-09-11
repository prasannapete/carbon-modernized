package com.pcpl.carbon.authserver.User.Service;
import com.pcpl.carbon.authserver.AppFeature.Model.AppFeature;
import com.pcpl.carbon.authserver.AppFeature.Service.AppFeatureService;
import com.pcpl.carbon.authserver.User.Model.User;
import com.pcpl.carbon.authserver.User.Model.UserDetail;
import com.pcpl.carbon.authserver.User.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("userDetailService")
public class UserDetailServiceImpl  implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AppFeatureService appFeatureService;

    private Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUserNameAndIsDeleted(userName, 0);
        optionalUser.orElseThrow(() -> new UsernameNotFoundException("Invalid user name or password"));
        List<AppFeature> appFeatures = new ArrayList<>();
        try {

            appFeatures = appFeatureService.getAppFeaturesForUser(optionalUser.get().getId());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        UserDetail userDetail = new UserDetail(optionalUser.get(), appFeatures);
        new AccountStatusUserDetailsChecker().check(userDetail);
        return userDetail;
    }
}
