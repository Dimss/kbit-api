package rh.local.kbit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import rh.local.kbit.model.RoleName;
import rh.local.kbit.model.User;
import rh.local.kbit.repository.UserRepository;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class KbitApplication {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(KbitApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Create admin user if it's not exists in DB
        User u = userRepository.findUserByEmail("admin@admin").orElse(null);
        if (u == null) {
            u = new User("admin@admin", "admin", "admin", "admin");
            u.setPassword(passwordEncoder.encode(u.getPassword()));
            u.setRoleName(RoleName.admin);
            userRepository.save(u);
            // Comment for push



        }

    }

}
