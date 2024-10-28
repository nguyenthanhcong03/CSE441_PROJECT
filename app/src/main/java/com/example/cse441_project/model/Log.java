package com.example.cse441_project.model;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Log {

    private String id;
    private String userId;
    private String action;
    private String time;
    private String description;

}
