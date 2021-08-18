package com.application.ems.resource;

import com.application.ems.exception.domain.*;
import com.application.ems.model.HttpResponse;
import com.application.ems.model.User;
import com.application.ems.model.UserPrincipal;
import com.application.ems.service.UserService;
import com.application.ems.utility.JWTTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.application.ems.constant.FileConstant.*;
import static com.application.ems.constant.SecurityConstant.JWT_TOKEN_HEADER;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

//@CrossOrigin("http://localhost:4200/**")
@RestController
@AllArgsConstructor
@RequestMapping(path = {"/","/user"}) //instead of overiding the base url with user, we can acutally leave the default...
public class UserResource extends ExceptionHandling {

    public static final String EMAIL_SENT = "A Email with a new password was sent to: ";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JWTTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        //this method is going to take the username and password
        authenticate(user.getUsername(), user.getPassword());
        User loginUser =  userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser,jwtHeader, OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws EmailExistException, UserNotFoundException, UserNameExistException, MessagingException {
        User newUser = userService.register(user.getName(),user.getUsername(),user.getEmail());
        return new ResponseEntity<>(newUser, OK);
    }

    @PostMapping("/add")
//    @PreAuthorize("hasAnyAuthority('user:create')")
    public ResponseEntity<User> addNewUser (@RequestParam("name") String name,
                                            @RequestParam("username") String username,
                                            @RequestParam("email") String email,
                                            @RequestParam("role") String role,
                                            @RequestParam("isActive") String isActive,
                                            @RequestParam("isNonLocked") String isNonLocked,
                                            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UserNameExistException, IOException {
        User newUser = userService.addNewUser(name,username,email,role,Boolean.parseBoolean(isActive),Boolean.parseBoolean(isNonLocked),profileImage);
        return new ResponseEntity<>(newUser, OK);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User> getUser(@PathVariable("username") String username){
        User user = userService.findUserByUsername(username);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/resetPassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return response(OK, EMAIL_SENT + email);
    }

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAnyAuthority('user:delete')")//in order for someone to delete a user, you need the correct authorities
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username){
        userService.deleteUser(username);
        return response(OK, USER_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User> updateProfileImage (@RequestParam("username") String username, @RequestParam(value = "profileImage") MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UserNameExistException, IOException {
        User user = userService.updateProfileImage(username,profileImage);
        return new ResponseEntity<>(user, OK);
    }

    //this method goes into the url path and gets the user picture
    @GetMapping(path = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)//This static import is needed for pictures
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {  //bytes represent the image, the broweser will be able to parse and render the image
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName)); //("user.home") + "/cogentCMSSystem/user/ + filename"
    }

    //to get image from robohash... this is when they did not pass a image in there profile. we give them a profile using robohash api
    @GetMapping(path = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)//This static import is needed for pictures
    public byte[] getTempImage(@PathVariable("username") String username) throws IOException {  //bytes represent the image, the broweser will be able to parse and render the image
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username); //fetching url
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();//byte array to store all the data from that url
        try(InputStream inputStream = url.openStream()) {  //once we open this url we going to get all of the stream. we are going to get the stream and put it into byteArrayOutputStream
            int byteRead;
            byte[] chunk = new byte[1024]; //how much were going to read everytime...
            while((byteRead = inputStream.read(chunk)) > 0) { //this while loop gives me that many bytes everytime we loop through this until there is no more.
                byteArrayOutputStream.write(chunk,0,byteRead); //its reading 1024 until ther is no more bytes in in the outputStream
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    //we madew this method to return a http responce body for methods that do not return anything (such as delete)
    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(),httpStatus, httpStatus.getReasonPhrase().toUpperCase()
                ,message.toUpperCase()),httpStatus);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResponseEntity<User> update (@RequestParam("currentUsername") String currentUsername,
                                        @RequestParam("name") String name,
                                        @RequestParam("username") String username,
                                        @RequestParam("email") String email,
                                        @RequestParam("role") String role,
                                        @RequestParam("isActive") String isActive,
                                        @RequestParam("isNonLocked") String isNonLocked,
                                        @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, UserNameExistException, IOException {
        User updatedUser = userService.updateUser(currentUsername,name,username,email,role,Boolean.parseBoolean(isActive),Boolean.parseBoolean(isNonLocked),profileImage);
        return new ResponseEntity<>(updatedUser, OK);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }

}

//ResourceHttpRequestHandler is created everytime spring is loadedup.. this class checks for alot of things
//it handles request when there is no handler
