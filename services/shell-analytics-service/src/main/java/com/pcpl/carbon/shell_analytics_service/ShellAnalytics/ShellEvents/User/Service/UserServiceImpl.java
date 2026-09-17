package com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Service;

import com.pcpl.carbon.pcplsdk.Common.Response.ApplicationResponse;
import com.pcpl.carbon.pcplsdk.Common.Response.PageResponse;
import com.pcpl.carbon.pcplsdk.Common.Role.DTO.RoleDTO;
import com.pcpl.carbon.pcplsdk.Common.Role.Model.Role;
import com.pcpl.carbon.pcplsdk.Common.Role.Model.RoleAccount;
import com.pcpl.carbon.pcplsdk.Common.User.DTO.UserDTO;
import com.pcpl.carbon.pcplsdk.Common.User.Model.User;
import com.pcpl.carbon.pcplsdk.Common.User.Response.UserResponse;
import com.pcpl.carbon.pcplsdk.Generic.Service.AbstractLazyService;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.DTO.UserCountriesDTO;
import com.pcpl.carbon.pcplsdk.ShellAnalytics.UserCountries.Model.UserCountries;
import com.pcpl.carbon.shell_analytics_service.Mailer.MailClient;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.Role.Service.RoleAccountService;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.User.Repository.UserRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Repository.UserCountriesRepository;
import com.pcpl.carbon.shell_analytics_service.ShellAnalytics.ShellEvents.UserCountries.Service.UserCountriesServiceImpl;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl extends AbstractLazyService<User, UserDTO, UserRepository> implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserInformationService userInformationService;

    @Autowired
    RoleAccountService roleAccountService;

    @Autowired
    UserCountriesRepository userCountriesRepository;

    @Autowired
    UserCountriesServiceImpl userCountriesService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    MailClient mailClient;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";

    @Override
    public User getEntityObject() {
        return new User();
    }

    @Override
    public UserDTO getDtoObject() {
        return new UserDTO();
    }

    @Override
    public String getUniqueConstraintCheckMethodName() {
        return "getId";
    }

    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public User save(User user) throws Exception {
        return userRepository.save(user);
    }

    @Override
    public UserResponse saveUser(UserDTO user) throws Exception {
        log.trace("Entering");
        UserResponse userResponse = new UserResponse();
        User userInfo = userInformationService.getUser();
        String Password = user.getPassword();
        String emailAddress = user.getEmailAddress();
        Integer isNewUser = 0;
        try {
            if (checkDuplicate(user)) {
                userResponse.setUser(user);
                userResponse.setSuccess(false);
                userResponse.setError("Duplicate user name!");
            } else {
                if (user.getId() == null) {
                    isNewUser = 1;
                    if (user.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    user.setAccountStatus(2);
                    user.setCreatedBy(userInfo.getId());
                    user.setCreationTime(new Date());
                } else {
                    Optional<User> optionalUser = userRepository.findById(user.getId());
                    if (optionalUser.isPresent()) {
                        user.setPassword(optionalUser.get().getPassword());
                        user.setAccountStatus(optionalUser.get().getAccountStatus());
                    }

                    user.setLastModifiedBy(userInfo.getId());
                    user.setLastModifiedTime(new Date());
                }

                User user1 = this.save(convertDtoToEntity(user));
                UserDTO userDTO = convertEntityToDto(user1);

                //deleting removed countries and adding new
                String[] countryIds = user.getCountryIds().split(",");
                List<Long> ids = new ArrayList<>();
                for (String countryId : countryIds) {
                    ids.add(Long.parseLong(countryId));
                }
                Set<Long> formdataCountryIds = new HashSet<>(ids);
                List<UserCountries> userCountries = userCountriesRepository.findAllByIsDeletedAndUserId(0, userDTO.getId());
                Set<Long> userCountryIds = new HashSet<>(Arrays.asList(userCountries.stream().map(UserCountries::getCountryId).toArray(Long[]::new)));
                Set<Long> removableCountryIds=new HashSet<>(userCountryIds);
                removableCountryIds.removeAll(formdataCountryIds);
                List<Long> list = new ArrayList<>(removableCountryIds);
                List<UserCountries> userCountriesList = userCountriesRepository.findAllByIsDeletedAndCountryIdIn(0, list);
                userCountriesService.multipleMoveToTrash(userCountriesList);
                Set<Long> newCountryIds=formdataCountryIds;
                newCountryIds.removeAll(userCountryIds);
                List<UserCountriesDTO> userCountriesDTOList = new ArrayList<>();
                for (Long countryId : newCountryIds) {
                    UserCountriesDTO userCountriesDTO1 = new UserCountriesDTO();
                    userCountriesDTO1.setCountryId(countryId);
                    userCountriesDTO1.setUserId(userDTO.getId());
                    userCountriesDTOList.add(userCountriesDTO1);
                }
                userCountriesService.saveAll(userCountriesDTOList);
                if(userDTO!=null){
                    if(isNewUser==1){
                        mailClient.sendInviteUserEmail(user1);
                    }
                }
                userResponse.setUser(userDTO);
                userResponse.setSuccess(true);
                userResponse.setError("");
            }
            log.trace("Completed Successfully");
        } catch (Exception ex) {
            throw ex;
        }
        return userResponse;
    }


    @Override
    public UserResponse saveUserByRole(User user) throws Exception {
        log.trace("Entering");
        UserResponse userResponse = new UserResponse();
        User userInfo = userInformationService.getUser();
        String Password = user.getPassword();
        String emailAddress = user.getEmailAddress();
        Integer isNewUser = 0;
        try {
            if (checkDuplicate(user)) {
                userResponse.setUser(convertEntityToDto(user));
                userResponse.setSuccess(false);
                userResponse.setError("Duplicate user name!");
            } else {
                if (user.getId() == null) {
                    isNewUser = 1;
                    if (user.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    user.setAccountStatus(2);
                    user.setCreatedBy(userInfo.getId());
                    user.setCreationTime(new Date());
                } else {
                    Optional<User> optionalUser = userRepository.findById(user.getId());
                    if (optionalUser.isPresent()) {
                        user.setPassword(optionalUser.get().getPassword());
                        user.setAccountStatus(optionalUser.get().getAccountStatus());
                    }
                    user.setLastModifiedBy(userInfo.getId());
                    user.setLastModifiedTime(new Date());
                }

                List<Role> roles = user.getRoles();
                user.setRoles(null);

                User user1 = this.save(user);
                UserDTO userDTO = convertEntityToDto(user1);

                if (userDTO != null) {
                    if (isNewUser == 1) {
                        mailClient.sendInviteUserEmail(user1);
                    }
                }

                if (roles != null && !roles.isEmpty()) {
                    RoleAccount roleAccount = new RoleAccount();
                    roleAccount.setAccountId(user1.getId());
                    roleAccount.setRoleId(roles.get(0).getId());
                    roleAccountService.saveRoleAccount(roleAccount);
                }

                entityManager.clear();
                Optional<User> savedUser = userRepository.findById(user1.getId());
                if (savedUser.isPresent()) {
                    userDTO = convertEntityToDto(savedUser.get());
                }

                userResponse.setUser(userDTO);
                userResponse.setSuccess(true);
                userResponse.setError("");
            }
            log.trace("Completed Successfully");
        } catch (Exception ex) {
            throw ex;
        }
        return userResponse;
    }

    @Override
    public Optional<User> findUserByEmailAndIsDeleted(String userName,int isDeleted) {
        Optional<User> optionalUser = userRepository.findAllByIsDeletedAndEmailAddress(isDeleted, userName);
        return optionalUser;
    }

    @Override
    public UserResponse resetNewPassword(Map<String, String> formData) throws Exception {
        log.trace("Entering");
        UserResponse userResponse = new UserResponse();
        try {
            Optional<User> optionalUser = userRepository.findById(Long.parseLong(formData.get("id")));
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setPassword(passwordEncoder.encode(formData.get("newPassword")));
                this.save(user);
                userResponse.setSuccess(true);
                userResponse.setError("");
            } else {
                throw new Exception("Cannot find user");
            }
            log.trace("Completed Successfully");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            userResponse.setSuccess(false);
            userResponse.setError(ex.getMessage());
        }
        log.trace("Exiting");
        return userResponse;
    }

    @Override
    public UserResponse getMyProfile() throws Exception {
        log.trace("Entering");
        User userInfo = userInformationService.getUser();
        UserResponse userResponse = new UserResponse();
        try {

            Optional<User> optionalUser = userRepository.findUserByUserNameAndIsDeleted(userInfo.getUserName(),0);
            if (optionalUser.isPresent()) {
                UserDTO userDTO = new UserDTO(optionalUser.get());
                userResponse.setUser(
                        userDTO
                );
            } else {
                throw new Exception("Cannot find user");
            }
            userResponse.setSuccess(true);
            userResponse.setError("");
            log.trace("Completed Successfully");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            userResponse.setSuccess(true);
            userResponse.setError(ex.getMessage());
        }
        log.trace("Exiting");
        return userResponse;
    }

    @Override
    public ApplicationResponse setUserData(ApplicationResponse applicationResponse) {
        UserDTO userDTO = (UserDTO) applicationResponse.getData();
        List<UserCountriesDTO> userCountriesDTOS = userCountriesRepository.getUserCountriesByUserId(userDTO.getId());
        userDTO.setUserCountries(userCountriesDTOS);
        applicationResponse.setData(userDTO);
        return applicationResponse;
    }

    @Override
    public ApplicationResponse getAllUsers(Map<String, String> formData) throws Exception {
        return null;
    }

    @Override
    public UserResponse changePassword(Map<String, String> formData) throws Exception {
        log.trace("Entering");
        User userInfo = userInformationService.getUser();
        UserResponse userResponse = new UserResponse();
        try {
            Optional<User> optionalUser = userRepository.findById(userInfo.getId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                boolean isValid = passwordEncoder.matches(formData.get("currentPassword"),user.getPassword());
                if (isValid) {
                    user.setPassword(
                            passwordEncoder.encode(formData.get("newPassword"))
                    );
                    userResponse.setUser(
                            new UserDTO(
                                    this.save(user)
                            )
                    );
                    userResponse.setSuccess(true);
                    userResponse.setError("");
                } else {
                    userResponse.setSuccess(false);
                    userResponse.setError("Current password is wrong");
                }
            } else {
                throw new Exception("Cannot find user");
            }
            log.trace("Completed Successfully");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            userResponse.setSuccess(false);
            userResponse.setError(ex.getMessage());
        }
        log.trace("Exiting");
        return userResponse;
    }

    @Override
    public ApplicationResponse searchPaginated(Map<String, String> formData) throws Exception {
        log.trace("Entering {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        ApplicationResponse applicationResponse = null;
        try {
            int pageNumber = formData.get("current_page") == null ? 0 : Integer.parseInt(formData.get("current_page"));
            int pageSize = formData.get("page_size") == null ? 10 : Integer.parseInt(formData.get("page_size"));
            String sortFiled = formData.get("sort_field") == null ? "creationTime" : formData.get("sort_field");
            String sortOrder = formData.get("sort_order") == null ? "asc" : formData.get("sort_order");
            String searchText = formData.get("search_text") == null ? "%%" : String.valueOf(formData.get("search_text"));
            Sort sort;
            if (!searchText.contains("%")) {
                searchText = "%" + searchText + "%";
            }
            if (sortOrder.equals("asc")) {
                sort = Sort.by(Sort.Direction.ASC, sortFiled);
            } else {
                sort = Sort.by(Sort.Direction.DESC, sortFiled);
            }
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            Page<UserDTO> page = userRepository.searchPaginationByName(
                    searchText,
                    pageable
            );
            for(UserDTO userDTO : page.getContent()) {
                userDTO.setUserCountries(userCountriesRepository.getUserCountriesByUserId(userDTO.getId()));
            }
            PageResponse pageResponse = PageResponse.builder()
                    .data(page.getContent())
                    .pageSize(page.getPageable().getPageSize())
                    .recordsFiltered(page.getNumberOfElements())
                    .totalRecords(page.getTotalElements())
                    .pageNumber(page.getPageable().getPageNumber())
                    .totalPages(page.getTotalPages())
                    .build();
            applicationResponse = ApplicationResponse.builder().success(true).data(page.getContent()).build();
            applicationResponse.setCurrentRecords(pageResponse.getRecordsFiltered());
            applicationResponse.setRecordsTotal(pageResponse.getTotalRecords());
            applicationResponse.setRecordsFiltered(pageResponse.getTotalRecords());
            applicationResponse.setTotalPages(page.getTotalPages());
            log.trace("Completed Successfully {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            applicationResponse = ApplicationResponse.builder()
                    .success(false)
                    .error(ex.getMessage())
                    .build();
        }
        log.trace("Exiting {}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return applicationResponse;
    }

    @Override
    public ApplicationResponse moveToTrash(Long id)  {

        return super.moveToTrash(id);
    }


    private boolean checkDuplicate(UserDTO userDTO) {
        List<User> usersList;
        if (userDTO.getId() == null) {
            usersList = userRepository.findAllByIsDeletedAndUserNameOrEmailAddress(0, userDTO.getUserName(),userDTO.getEmailAddress());
        } else {
            usersList = userRepository.findAllByIsDeletedAndUserNameAndEmailAddressAndIdIsNot(0, userDTO.getUserName(),userDTO.getEmailAddress(), userDTO.getId());
        }
        return !usersList.isEmpty();
    }

    public static String generatePassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }

    @Override
    public ApplicationResponse getPlayMobilUsers(Map<String, String> formData) throws Exception {

        ApplicationResponse applicationResponse;

        try {
            int pageNumber = formData.get("current_page") == null ? 0 : Integer.parseInt(formData.get("current_page"));
            int pageSize = formData.get("page_size") == null ? 10 : Integer.parseInt(formData.get("page_size"));
            String sortField = formData.get("sort_field") == null ? "creationTime" : formData.get("sort_field");
            String sortOrder = formData.get("sort_order") == null ? "desc" : formData.get("sort_order");

            Sort sort;

            if (sortOrder.equalsIgnoreCase("asc")) {
                sort = Sort.by(Sort.Direction.ASC, sortField);
            } else {
                sort = Sort.by(Sort.Direction.DESC, sortField);
            }

            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

            Page<User> page =
                    userRepository.findAllByIsPlayMobilUserAndIsDeleted(
                            1,
                            0,
                            pageable
                    );

            PageResponse pageResponse = PageResponse.builder()
                    .data(page.getContent())
                    .pageSize(page.getSize())
                    .recordsFiltered(page.getNumberOfElements())
                    .totalRecords(page.getTotalElements())
                    .pageNumber(page.getNumber())
                    .totalPages(page.getTotalPages())
                    .build();

            applicationResponse = ApplicationResponse.builder()
                    .success(true)
                    .data(pageResponse.getData())
                    .build();

            applicationResponse.setCurrentRecords(pageResponse.getRecordsFiltered());
            applicationResponse.setRecordsFiltered(pageResponse.getTotalRecords());
            applicationResponse.setRecordsTotal(pageResponse.getTotalRecords());
            applicationResponse.setTotalPages(pageResponse.getTotalPages());

        } catch (Exception ex) {

            log.error(ex.getMessage(), ex);

            applicationResponse = ApplicationResponse.builder()
                    .success(false)
                    .error(ex.getMessage())
                    .build();
        }
        return applicationResponse;
    }

    private boolean checkDuplicate(User user) {
        List<User> usersList;
        if (user.getId() == null) {
            usersList = userRepository.findAllByIsDeletedAndUserNameOrEmailAddress(0, user.getUserName(),user.getEmailAddress());
        } else {
            usersList = userRepository.findAllByIsDeletedAndUserNameAndEmailAddressAndIdIsNot(0, user.getUserName(),user.getEmailAddress(), user.getId());
        }
        return !usersList.isEmpty();
    }
}
