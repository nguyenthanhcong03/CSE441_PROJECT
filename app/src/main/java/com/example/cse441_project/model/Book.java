package com.example.cse441_project.model;
import java.util.List;

import  lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String id;
    private String name;
    private String description;
    List<String> authorId;
    private String categoryId;
    private String image;
    private int quantity;
    private String publisherId;
}
