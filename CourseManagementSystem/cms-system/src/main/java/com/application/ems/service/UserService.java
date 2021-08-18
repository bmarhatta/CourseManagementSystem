package com.application.ems.service;

import com.application.ems.exception.domain.EmailExistException;
import com.application.ems.exception.domain.EmailNotFoundException;
import com.application.ems.exception.domain.UserNameExistException;
import com.application.ems.exception.domain.UserNotFoundException;
import com.application.ems.model.User;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface UserService {
    User register(String name, String username, String email) throws UserNotFoundException, EmailExistException, UserNameExistException, MessagingException;
    List<User> getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User addNewUser(String name, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UserNameExistException, IOException;
    User updateUser(String currentUsername, String newName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UserNameExistException, IOException;
    void deleteUser(String username);
    void resetPassword(String email) throws EmailNotFoundException, MessagingException;
    User updateProfileImage(String username, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UserNameExistException, IOException;
}
