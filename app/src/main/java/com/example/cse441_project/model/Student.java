package com.example.cse441_project.model;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private String userId;
    private boolean violent;
    List<BorrowedBook> books;
}
