package com.pcpl.carbon.playmobilservice.PlayMobil.User.Repository;

import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;

import java.util.Optional;

public interface UserRepository extends PCPLCRUDRepository<User> {

    Optional<User> findUserByUserNameAndIsDeleted(String userName, int isDeleted);
}
