package com.example.cse441_project.model;


import java.io.Serializable;
import java.util.List;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor


public class Book implements Serializable {



    private String id;
    private String name;
    private String description;
    private String author;
    private String categoryId;
    private String image;
    private int quantity;
    private String publisherId;
    private int publishYear;





}

