package com.example.projectlxp.Exception;

public class SectionNotFoundException extends RuntimeException {
    
    public SectionNotFoundException(Long id) {
        super("Section not found with id: " + id);
    }
    
    public SectionNotFoundException(String message) {
        super(message);
    }
}
