package com.example.cse441_project.model;

public class Book {
    private String name;
    private String author;
    private String genre;
    private String description;
    private String publisher;
    private int publishYear;
    private int quantity;
    private String image;

    public Book() {
        // Cần một constructor trống cho Firestore
    }

    public Book(String name, String author, String genre, String description, String publisher, int publishYear, int quantity, String image) {
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.quantity = quantity;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
