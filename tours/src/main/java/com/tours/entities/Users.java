package com.tours.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Users
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @NotBlank(message = "This is required!")
    @Size(min = 2 , max = 20 , message = "Min 2 and Max 20 characters are allowed! ")
    String name;

    @Email(regexp="^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message="Invalid Email!!")
    @Column(unique = true)
    String email;


    @NotBlank(message ="This is required !!")
    @Size(min=5, message ="Password must be at least 5 characters long!!")
    String password;

    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be a valid 10-digit number!!")
    @Column(unique = true)
    String contactNo;

    String role;

    Boolean enabled;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Users() {
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", userName='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password=" + password +
                ", contactNo=" + contactNo +
                ", role=" + role +
                ", enabled=" + enabled +
                '}';
    }
}
