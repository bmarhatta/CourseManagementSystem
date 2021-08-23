package com.application.ems.service.impl;

import com.application.ems.enumeration.Role;
import com.application.ems.exception.domain.EmailExistException;
import com.application.ems.exception.domain.EmailNotFoundException;
import com.application.ems.exception.domain.UserNameExistException;
import com.application.ems.exception.domain.UserNotFoundException;
import com.application.ems.model.User;
import com.application.ems.model.UserPrincipal;
import com.application.ems.repo.UserRepo;
import com.application.ems.service.EmailService;
import com.application.ems.service.LoginAttemptService;
import com.application.ems.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;


import static com.application.ems.constant.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static com.application.ems.constant.UserImplConstant.*;
import static com.application.ems.enumeration.Role.*;





@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImp implements UserService, UserDetailsService {
    //loggs information into the console.
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private UserRepo userRepo;
    private BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;
    private EmailService emailService;

    @Autowired
    public UserServiceImp( UserRepo userRepo, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService,EmailService emailService) {
        this.userRepo = userRepo;
        this.passwordEncoder=passwordEncoder;
        this.loginAttemptService=loginAttemptService;
        this.emailService=emailService;
    }

    //this method is called whenever spring secuirty is trying to check the authentication of the user
    //this method is called whenever spring secuirty is trying to check the authentication of the user
    @Override //this method is simply looking for a username
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findUserByUsername(username);
        if (user == null) {
            LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
            throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
        } else {
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepo.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info(FOUND_USER_BY_USERNAME + username);
            return userPrincipal;
        }
    }

    //this method is going check to make sure that the user is not locked.
    private void validateLoginAttempt(User user) {
        if(user.isNotLocked()){ //checks to see if user is locked or not
            if(loginAttemptService.hasExceededMaxAttempt(user.getUsername())){//we check to see if they exceeded the 5 allowed attempts
                user.setNotLocked(false); //locks user
            } else {
                user.setNotLocked(true);
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername()); //if user is not locked we remove there cache
        }
    }

    @Override
    public User register(String name, String username, String email) throws UserNotFoundException, EmailExistException, UserNameExistException, MessagingException {
        validateNewUsernameAndEmail(EMPTY, username, email);
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodePassword(password));
        user.setActive(true);
        user.setNotLocked(true);
        user.setRoles(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepo.save(user);
        LOGGER.info("New user password: " + password);
        emailService.sendNewPasswordEmail(name,password,email);
        return user;
    }

    @Override
    public User addNewUser(String name, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UserNameExistException, IOException {
        validateNewUsernameAndEmail(EMPTY,username,email);
        User user = new User();
        String password = generatePassword();
        user.setUserId(generateUserId());
        user.setName(name);
        user.setJoinDate(new Date());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodePassword(password));
        user.setActive(isActive);
        user.setNotLocked(isNonLocked);
        user.setRoles(getRoleEnumName(role).name());
        user.setAuthorities(getRoleEnumName(role).getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl(username));
        userRepo.save(user);
        saveProfileImage(user, profileImage);
        LOGGER.info("New user password: " + password);
        return user;
    }


    @Override
    public User updateUser(String currentUsername, String newName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UserNameExistException, IOException {
        User currentUser = validateNewUsernameAndEmail(currentUsername,newUsername,newEmail);
        currentUser.setName(newName);
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail);
        currentUser.setActive(isActive);
        currentUser.setNotLocked(isNonLocked);
        currentUser.setRoles(getRoleEnumName(role).name());
        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
        userRepo.save(currentUser);
        saveProfileImage(currentUser, profileImage);
        return currentUser;
    }

    @Override
    public void deleteUser(String username) {
        User user = userRepo.findUserByUsername(username);
        userRepo.deleteById(user.getId());
    }

    @Override
    public void resetPassword(String email) throws EmailNotFoundException, MessagingException {
        User user = userRepo.findUserByEmail(email);
        if(user == null){
            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        String password = generatePassword();
        user.setPassword(encodePassword(password));
        userRepo.save(user);
        LOGGER.info("New user password: " + password);
        emailService.sendNewPasswordEmail(user.getName(),password,user.getEmail());
    }

    @Override
    public User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UserNameExistException, IOException {
        User user = validateNewUsernameAndEmail(username,null,null);
        saveProfileImage(user,profileImage);
        return user;
    }

    //this is how we change the profile picture
    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
        if (profileImage != null){
            //finding user folder
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();//this is going to get use the user folder
            if(!Files.exists(userFolder)) { //if files does not exist we create the user directory
                Files.createDirectories(userFolder);
                LOGGER.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION)); //Delete the file if they exist
            Files.copy(profileImage.getInputStream(),userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING); //saves new picture
            user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));
            userRepo.save(user);
            LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage);
        }
    }

    //This is going to return the acutall location of the image string. so we can point to that location.
    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH +
                username + DOT + JPG_EXTENSION).toUriString();
    }

    //this is going to return role for tht specific role name... if we send "user" it returnt the role USER
    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());
    }

    private String getTemporaryProfileImageUrl(String username) {
        //this is gonna return whatever url is for the server... default user image
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    @Override
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepo.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepo.findUserByEmail(email);
    }

        //this method checks if the username or email is already taken or not... so we never get duplicate values
    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws UserNotFoundException, UserNameExistException, EmailExistException {
        User userByNewUsername = findUserByUsername(newUsername);
        User userByNewEmail = findUserByEmail(newEmail);
        //if they pass in a currentUsername (and trying to change username, or emails, or something)
        if(StringUtils.isNotBlank(currentUsername)){ //makes sure that username is not blank. (if it is, we are dealing with a new user)
            User currentUser = findUserByUsername(currentUsername);
            //if i try to find a user in the database and it is null, then it does not exist
            if(currentUser == null){
                throw new UserNotFoundException(USER_FOUND_BY_USERNAME + currentUsername);
            }

            //we take there newUsername and try to find a user. if that user id does not match, that means we are dealing with a new user
            if(userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())){
                throw new UserNameExistException(USERNAME_ALREADY_EXIST);
            }
            //we take there email and try to find a user. if that user id does not match, that means we are dealing with a new user
            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())){
                throw new EmailExistException(EMAIL_ALREADY_EXIST);
            }
            return currentUser;

            //if they left currentUsername blank, that means they are a new user
        } else {
            if(userByNewUsername != null) {
                throw new UserNameExistException(USERNAME_ALREADY_EXIST);
            }
            if(userByNewEmail != null){
                throw new EmailExistException(EMAIL_ALREADY_EXIST);
            }
            return null;
        }
    }



}
