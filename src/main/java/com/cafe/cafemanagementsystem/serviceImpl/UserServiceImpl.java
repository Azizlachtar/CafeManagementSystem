package com.cafe.cafemanagementsystem.serviceImpl;

import com.cafe.cafemanagementsystem.POJO.User;
import com.cafe.cafemanagementsystem.constants.CafeConstants;
import com.cafe.cafemanagementsystem.dao.UserDao;
import com.cafe.cafemanagementsystem.jwt.CustomerUsersDetailsService;
import com.cafe.cafemanagementsystem.jwt.JwtFilter;
import com.cafe.cafemanagementsystem.jwt.JwtUtil;
import com.cafe.cafemanagementsystem.service.UserService;
import com.cafe.cafemanagementsystem.utils.CafeUtils;
import com.cafe.cafemanagementsystem.utils.EmailUtils;
import com.cafe.cafemanagementsystem.wrapper.UserWrapper;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {


    @Autowired
    UserDao userDao;

    @Autowired
     AuthenticationManager authenticationManager;
    @Autowired
    CustomerUsersDetailsService customerUsersDetailsService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtils emailUtils;

    @Override
    public ResponseEntity<String> singUp(Map<String, String> requestMap) {
        log.info("inside signUp{}", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userDao.findByEmailId(requestMap.get("email"));

                if (Objects.isNull(user)) {
                    userDao.save(getUserForMap(requestMap));
                    return CafeUtils.getResponseEntity("Successful Registered", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
                }

            } else
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap){
        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber") &&requestMap.containsKey("email")
                && requestMap.containsKey("password")){
            return true;
        }
        return false;

    }

    private User getUserForMap(Map<String,String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus(requestMap.get("status"));
        user.setRole(requestMap.get("role"));

        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("inside login");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))

            );
            if(auth.isAuthenticated()){
                if(customerUsersDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\""+
                jwtUtil.generateToken(customerUsersDetailsService.getUserDetail().getEmail(),
                        customerUsersDetailsService.getUserDetail().getRole()) +"\"}",
                    HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<String>("{\"message\":\""+"Wait for the admin approval."+"\"}",
                            HttpStatus.BAD_REQUEST);
                }
            }

        }catch (Exception ex){
            log.error("{}",ex);
        }
        return new ResponseEntity<String>("{\"message\":\""+"Bad Credentials."+"\"}",
                HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if(jwtFilter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);

            }else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }catch (Exception ex){
            log.error("{}",ex);
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if(jwtFilter.isAdmin()){
                    Optional<User> optional=userDao.findById(Integer.parseInt(requestMap.get("id")));
                    if(!optional.isEmpty()){
                        userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                        sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
                        return CafeUtils.getResponseEntity("user status updated successfully",HttpStatus.OK);
                    }else {
                        return CafeUtils.getResponseEntity("user id is not exist",HttpStatus.OK);

                    }
            }else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {

        allAdmin.remove(jwtFilter.getCurrentUser());
        if(status != null && status.equalsIgnoreCase("true")){
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"account approved","USER:-"+user +
                    "\n is approved by \nAdmin:-"+jwtFilter.getCurrentUser(),allAdmin);

        }else {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"account Disabled","USER:-"+user +
                    "\n is Disabled by \nAdmin:-"+jwtFilter.getCurrentUser(),allAdmin);

        }
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
                User userObj= userDao.findByEmail(jwtFilter.getCurrentUser());
                if(!userObj.equals(null)){
                    if(userObj.getPassword().equals(requestMap.get("oldPassword"))){
                            userObj.setPassword(requestMap.get("newPassword"));
                            userDao.save(userObj);
                        return CafeUtils.getResponseEntity("Password updated successfully",HttpStatus.OK);

                    }
                    return CafeUtils.getResponseEntity("incorrect old password",HttpStatus.INTERNAL_SERVER_ERROR);
                }
            return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user= userDao.findByEmail(requestMap.get("email"));
            if(!Objects.isNull(user)&& !Strings.isNullOrEmpty(user.getEmail()))
                emailUtils.forgotMail(user.getEmail(),"crediantial by cafe maangement",user.getPassword());


            return CafeUtils.getResponseEntity("check your mail for credentials",HttpStatus.INTERNAL_SERVER_ERROR);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOME_THING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

