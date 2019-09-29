package rh.local.kbit.payload;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import rh.local.kbit.model.RoleName;

@Component
@Scope("prototype")
public class LoginResponse {
    private String email;
    private String token;
    private RoleName role;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RoleName getRole() {
        return role;
    }

    public void setRole(RoleName role) {
        this.role = role;
    }
}
