package rh.local.kbit.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rh.local.kbit.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u where email = :email")
    Optional<User> findUserByEmail(@Param("email") String email);
}
