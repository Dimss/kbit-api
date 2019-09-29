package rh.local.kbit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import rh.local.kbit.model.RoleName;
import rh.local.kbit.model.User;
import rh.local.kbit.payload.*;
import rh.local.kbit.repository.UserRepository;
import rh.local.kbit.security.UserPrincipal;


import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Response responsePayload;


    @RequestMapping(value = "/user/info/{userEmail}", method = RequestMethod.GET)
    public ResponseEntity userInfo(@PathVariable("userEmail") String userEmail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User u;
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        // If user admin, just proceed with user selection
        if (userPrincipal.getRole().equals(RoleName.admin)) {
            u = userRepository.findUserByEmail(userEmail).orElse(null);
        } else {
            // User not admin, check if provided email is equals to email in the token
            if (!userPrincipal.getEmail().equals(userEmail)) {
                logger.warn("access forbidden, user email is not equal to token and user not in admin group");
                return ResponseEntity.status(401).body("Forbidden");
            } else {
                u = userRepository.findUserByEmail(userPrincipal.getEmail()).orElse(null);
            }
        }

        if (u == null) {
            logger.warn("auth is ok, but no user found, strange");
            return ResponseEntity.status(404).body("User not found");
        }

        return ResponseEntity.ok()
                .body(responsePayload
                        .setData(new UserInfo(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName(), u.getRoleName()))
                        .getJsonPayload());
    }

    @RequestMapping(value = "/user/{userEmail}", method = RequestMethod.PUT)
    public ResponseEntity updateUser(@PathVariable("userEmail") String userEmail, @Valid @RequestBody UpdateUserDetails updateUserDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User u;
        if (userPrincipal.getRole().equals(RoleName.admin)) {
            u = userRepository.findUserByEmail(userEmail).orElse(null);
            u.setFirstName(updateUserDetails.getFirstName());
            u.setLastName(updateUserDetails.getLastName());
        } else {
            if (!userPrincipal.getEmail().equals(userEmail)) {
                logger.warn("access forbidden, user email is not equal to token and user not in admin group");
                return ResponseEntity.status(401).body("Forbidden");
            } else {
                u = userRepository.findUserByEmail(userPrincipal.getEmail()).orElse(null);
                u.setFirstName(updateUserDetails.getFirstName());
                u.setLastName(updateUserDetails.getLastName());
            }
        }
        userRepository.save(u);
        return ResponseEntity.ok().body(responsePayload.getJsonPayload());
    }

    @RequestMapping(value = "/user/{userEmail}", method = RequestMethod.DELETE)
    public ResponseEntity deleteUser(@PathVariable("userEmail") String userEmail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User u;
        if (userPrincipal.getRole().equals(RoleName.admin)) {
            u = userRepository.findUserByEmail(userEmail).orElse(null);
        } else {
            if (!userPrincipal.getEmail().equals(userEmail)) {
                logger.warn("access forbidden, user email is not equal to token and user not in admin group");
                return ResponseEntity.status(401).body("Forbidden");
            } else {
                u = userRepository.findUserByEmail(userPrincipal.getEmail()).orElse(null);
            }
        }
        userRepository.delete(u);
        return ResponseEntity.ok().body(responsePayload.getJsonPayload());
    }


    @GetMapping("/users")
    public ResponseEntity usersList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (userPrincipal.getRole() == RoleName.admin) {
            List<User> users = userRepository.findAll();
            List<UserInfo> userInfoLst = new ArrayList<>();
            for (User u : users) {
                userInfoLst.add(new UserInfo(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName(), u.getRoleName()));
            }
            return ResponseEntity.ok().body(responsePayload.setData(userInfoLst).getJsonPayload());
        } else {
            return ResponseEntity.status(401).body("not admin user");
        }
    }

}
