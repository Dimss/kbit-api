package rh.local.kbit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rh.local.kbit.model.User;

import static org.assertj.core.api.Assertions.assertThat;

import rh.local.kbit.repository.UserRepository;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestEntityManager
//@DataJpaTest
public class UserRepositoryTests {
    Logger logger = LoggerFactory.getLogger(UserRepositoryTests.class);
    String email = "test@test.local";
    String password = "1234";
    @Autowired
    UserRepository userRepository;
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        User u = new User(email, password, "", "");
        u.setPassword(passwordEncoder.encode(password));
        testEntityManager.persist(u);
    }

    @Test
    @Transactional
    public void validateNewUser() {
        User found = userRepository.findUserByEmail(email).orElse(null);
        assertThat(found.getEmail()).isEqualTo(email);
    }


    @Test
    @Transactional
    public void validateUserPassword() {
        User user = userRepository.findUserByEmail(email).orElse(null);
        assertThat(passwordEncoder.matches(password, user.getPassword())).isEqualTo(true);
    }

}
