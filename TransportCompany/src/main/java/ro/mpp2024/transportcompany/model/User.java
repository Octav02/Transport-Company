package ro.mpp2024.transportcompany.model;

import java.io.Serializable;
import java.util.Objects;

public class User extends Entity<Long> implements Serializable {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String password;


    public User() {
        super();
    }

    public User(String username, String firstName, String lastName, String password) {
        super();
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public User(Long id, String username, String firstName, String lastName, String password) {
        super(id);
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
