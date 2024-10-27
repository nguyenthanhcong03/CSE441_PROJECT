package com.example.cse441_project.model;
import java.util.List;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private String id;
    private String name;
    List<Book> books;

}
