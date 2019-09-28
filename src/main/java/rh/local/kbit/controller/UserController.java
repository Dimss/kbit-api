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

@RestController
@RequestMapping("/v1")
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);
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
        User user = new User(signup.getEmail(), signup.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleName(RoleName.ROLE_USER);
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
        tr.setToken(jwt);
        return ResponseEntity
                .ok()
                .body(responsePayload.setData(tr).getJsonPayload());
    }

    @GetMapping("/user/info")
    public ResponseEntity userInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User u = userRepository.findUserByEmail(userPrincipal.getEmail()).orElse(null);
        if (u == null) {
            logger.warn("auth is ok, but no user found, strange");
            return ResponseEntity.status(404).body("User not found");
        }
        return ResponseEntity.ok()
                .body(responsePayload
                        .setData(new UserInfo(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName(), u.getRoleName()))
                        .getJsonPayload());
    }

}
