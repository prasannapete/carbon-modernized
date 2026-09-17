package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.DTO.UserDTO;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Common.User.Response.UserResponse;
import com.pcpl.carbon.pcplsdk.Generic.Service.PCPLCRUDService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Repository.UserRepository;

import java.util.Map;
import java.util.Optional;

public interface UserService extends PCPLCRUDService<User, UserDTO, UserRepository> {
    User save(User user) throws Exception;

    UserResponse saveUser(UserDTO userDTO) throws Exception;
    UserResponse saveUserByRole(User user) throws Exception;
    Optional<User> findUserByEmailAndIsDeleted(String email,int isDeleted) throws Exception;
    UserResponse resetNewPassword(Map<String,String> formData) throws Exception;
    UserResponse getMyProfile() throws Exception;
    ApplicationResponse setUserData(ApplicationResponse applicationResponse);
    ApplicationResponse getAllUsers(Map<String,String> formData) throws Exception;
    UserResponse changePassword(Map<String, String> formData) throws Exception;
    ApplicationResponse searchPaginated(Map<String, String> formData) throws Exception;
    ApplicationResponse getPlayMobilUsers(Map<String, String> formData) throws Exception;
}
