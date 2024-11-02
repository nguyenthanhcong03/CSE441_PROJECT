package com.example.cse441_project.model;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowedBook {
    private String id;
    private String studentId;
    private String bookId;
    private String borrowDate;
    private String returnDate;
    private String studentName;
    private String status;

}
