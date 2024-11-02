package com.example.cse441_project.model;
import java.util.Date;
import java.util.List;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String fullname;
    private String address;
    private String gender;
    private Date birthday;
    private String status;
    private String role;


}
