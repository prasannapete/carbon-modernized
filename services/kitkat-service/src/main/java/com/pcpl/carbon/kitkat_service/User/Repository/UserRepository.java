package com.pcpl.carbon.kitkat_service.User.Repository;

import com.pcpl.carbon.pcplsdk.Common.User.DTO.UserDTO;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Generic.Repository.PCPLCRUDRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends PCPLCRUDRepository<User> {
    Optional<User> findById(Long id);
    Optional<User> findUserByUserNameAndIsDeleted(String userName,int isDeleted);
    List<User> findAllByIsDeletedAndUserNameOrEmailAddress(int isDeleted, String userName, String emailAddress);

    Optional<User> findAllByIsDeletedAndEmailAddress(int isDeleted, String emailAddress);
    List<User> findAllByIsDeletedAndUserNameAndEmailAddressAndIdIsNot(int isDeleted,String userName,String emailAddress,Long id);
    List<User> findAllByIsDeletedAndUserName(int isDeleted, String userName);
    List<User> findAllByIsDeletedAndUserNameAndIdIsNot(int isDeleted, String userName, Long id);

    @Query(
            "SELECT new com.pcpl.carbon.pcplsdk.Common.User.DTO.UserDTO(" +
                    "user.id," +
                    "user.firstName," +
                    "user.lastName," +
                    "user.mobileNumber," +
                    "user.alternativePhone," +
                    "user.emailAddress," +
                    "user.photoId," +
                    "user.userName," +
                    "user.password," +
                    "user.accountStatus," +
                    "user.isGod," +
                    "user.isPlayMobilUser" +
                    ")" +
                    "FROM User user" +
                    " WHERE user.isDeleted = 0 AND (user.isPlayMobilUser = 0 or user.isPlayMobilUser is null) AND (user.firstName LIKE %:name% OR user.lastName LIKE %:name% OR user.userName LIKE %:name%)"
    )
    Page<UserDTO> searchPaginationByName(@Param("name") String name, Pageable pageable);
}
