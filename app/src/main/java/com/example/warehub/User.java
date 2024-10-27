package com.example.warehub;

public class User {
    private String username;
    private String fullName;
    private String email;
    private String companyName;
    private String phone;
    private String password;

    public User(String username, String fullName, String email, String companyName, String phone, String password) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.companyName = companyName;
        this.phone = phone;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getCompanyName() { return companyName; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
}
