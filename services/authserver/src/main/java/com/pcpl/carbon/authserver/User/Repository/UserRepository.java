package com.pcpl.carbon.authserver.User.Repository;

import com.pcpl.carbon.authserver.User.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Long> {
    Optional<User> findByUserNameAndIsDeleted(String userName, int isDeleted );

    Optional<User> findByUserName(String userName);
}
