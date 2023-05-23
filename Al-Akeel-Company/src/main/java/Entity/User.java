package Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "UserEntity")
public class User {
    public enum UserRole {
        customer,
        runner,
        owner
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private UserRole role;

    private String name;

    @Column(unique = true)
    private String username;
    
    private String password;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
