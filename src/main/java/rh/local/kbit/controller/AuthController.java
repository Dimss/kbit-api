package rh.local.kbit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rh.local.kbit.model.RoleName;
import rh.local.kbit.model.User;
import rh.local.kbit.payload.*;
import rh.local.kbit.repository.UserRepository;
import rh.local.kbit.security.JwtTokenProvider;
import rh.local.kbit.security.UserPrincipal;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Response response;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private Response responsePayload;

    @PostMapping("/auth/signup")
    public ResponseEntity createUser(@Valid @RequestBody SignUpRequest signup) {
        User user = new User(signup.getEmail(), signup.getPassword(), signup.getFirstName(), signup.getLastName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleName(RoleName.user);
        userRepository.save(user);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getJsonPayload());

    }

    @PostMapping("/auth/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        LoginResponse tr = context.getBean(LoginResponse.class);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        tr.setEmail(userPrincipal.getEmail());
        tr.setRole(userPrincipal.getRole());
        tr.setToken(jwt);

        return ResponseEntity
                .ok()
                .body(responsePayload.setData(tr).getJsonPayload());
    }



}
