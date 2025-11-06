package com.example.projectlxp.Exception;

public class LectureNotFoundException extends RuntimeException {
    
    public LectureNotFoundException(Long id) {
        super("Lecture not found with id: " + id);
    }
    
    public LectureNotFoundException(String message) {
        super(message);
    }
}
