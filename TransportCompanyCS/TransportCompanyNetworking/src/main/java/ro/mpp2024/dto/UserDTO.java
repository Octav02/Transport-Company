package ro.mpp2024.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private String username;
    private String password;

    public UserDTO() {
    }

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
