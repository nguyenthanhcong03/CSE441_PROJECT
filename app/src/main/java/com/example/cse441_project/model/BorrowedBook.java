package com.example.cse441_project.model;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowedBook {
    private String id;
    private String userId;
    private String bookId;
    private String borrowDate;
    private String returnDate;
    private String status;
    private String expectedReturnDate;
}
