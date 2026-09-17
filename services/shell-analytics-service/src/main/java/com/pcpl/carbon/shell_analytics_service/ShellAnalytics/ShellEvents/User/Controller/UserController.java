package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Controller;

import com.pcpl.carbon.pcplsdk.Common.Exception.CustomException;
import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.User.DTO.UserDTO;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Common.User.Response.UserResponse;
import com.pcpl.carbon.pcplsdk.Generic.Controller.AbstractCRUDController;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Repository.UserRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service.UserInformationService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service.UserService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/user")
@Slf4j
public class UserController extends AbstractCRUDController<User, UserDTO, UserRepository, UserServiceImpl> {
    @Autowired
    UserService userService;
    @Autowired
    private UserInformationService userInformationService;


    @RequestMapping(value = "/save-user", method = RequestMethod.POST)
    public UserResponse saveUser(@Valid @RequestBody UserDTO userDTO) {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        UserResponse userResponse = new UserResponse();
        try {
            userResponse = userService.saveUser(userDTO);
        }catch (Exception ex){
            userResponse.setSuccess(false);
            userResponse.setError(ex.getMessage());
        }
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return userResponse;
    }

    @RequestMapping(value = "/save-user-by-role", method = RequestMethod.POST)
    public UserResponse saveUserByRole(@Valid @RequestBody User user) {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        UserResponse userResponse = new UserResponse();
        try {
            userResponse = userService.saveUserByRole(user);
        }catch (Exception ex){
            userResponse.setSuccess(false);
            userResponse.setError(ex.getMessage());
        }
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return userResponse;
    }


    @RequestMapping(value = "/reset-new-password", method = RequestMethod.POST)
    public UserResponse resetNewPassword(@RequestBody Map<String, String> formData)throws Exception {
        UserResponse userResponse = new UserResponse();
        try {
            userResponse = userService.resetNewPassword(formData);
        } catch (Exception ex) {
            userResponse.setSuccess(false);
            userResponse.setError(ex.getMessage());
        }
        return userResponse;
    }
    @RequestMapping(value = "/get-my-profile", method = RequestMethod.POST)
    public UserResponse getMyProfile()throws Exception {
        UserResponse userResponse = new UserResponse();
        try {
            userResponse =  userService.getMyProfile();
            userResponse.setSuccess(true);
        } catch (Exception ex) {
            userResponse.setSuccess(false);
            userResponse.setError(ex.getMessage());
        }
        return userResponse;
    }

    @Override
    public ResponseEntity<ApplicationResponse> getById(@PathVariable Long id){
        ResponseEntity<ApplicationResponse> responseResponseEntity = super.getById(id);
        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();
        try{
            applicationResponse = userService.setUserData(responseResponseEntity.getBody());

        }catch (Exception ex){
            throw new CustomException("Sorry, request failed. | Reason : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(applicationResponse);
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    public UserResponse changePassword(@RequestBody Map<String, String> formData)throws Exception {
        UserResponse userResponse = new UserResponse();
        try {
            userResponse = userService.changePassword(formData);
        } catch (Exception ex) {
            userResponse.setSuccess(false);
            userResponse.setError(ex.getMessage());
        }
        return userResponse;
    }

    @RequestMapping(value = "/keep-session", method = RequestMethod.POST)
    public boolean keepSession() {
        return true;
    }

    @RequestMapping(value = "/search-paginated", method = RequestMethod.POST)
    public ApplicationResponse searchPaginated(@RequestBody Map<String, String> formData)throws Exception {
        ApplicationResponse applicationResponse =  ApplicationResponse.builder().build();
        try {
            applicationResponse = userService.searchPaginated(formData);
        } catch (Exception ex) {
            applicationResponse.setSuccess(false);
            applicationResponse.setError(ex.getMessage());
        }
        return applicationResponse;
    }

    @RequestMapping(value = "/get-play-mobil-users", method = RequestMethod.POST)
    public ApplicationResponse getPlayMobilUsers(@RequestBody Map<String, String> formData) {

        ApplicationResponse applicationResponse = ApplicationResponse.builder().build();

        try {
            applicationResponse = userService.getPlayMobilUsers(formData);

        } catch (Exception ex) {
            applicationResponse.setSuccess(false);
            applicationResponse.setError(ex.getMessage());
        }
        return applicationResponse;
    }
}
