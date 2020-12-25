package com.bookie_tbh.bookie.ui.book;

public class Book {

    private String bookName, writerName, image, pdf;

    public Book(){

    }

    public Book(String bookName, String writerName, String image, String pdf) {
        this.bookName = bookName;
        this.writerName = writerName;
        this.image = image;
        this.pdf = pdf;
    }

    public String getBookName() {
        return bookName;
    }

    public String getWriterName() {
        return writerName;
    }

    public String getImage() {
        return image;
    }

    public String getPdf() { return pdf; }
}
