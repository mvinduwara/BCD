package lk.jiat.ecomm.user.dto;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String LastName;
    private String contactNo;

    public UserDTO() {
    }

    public UserDTO(Long id, String username, String email, String firstName, String lastName, String contactNo) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        LastName = lastName;
        this.contactNo = contactNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", LastName='" + LastName + '\'' +
                ", contactNo='" + contactNo + '\'' +
                '}';
    }
}
