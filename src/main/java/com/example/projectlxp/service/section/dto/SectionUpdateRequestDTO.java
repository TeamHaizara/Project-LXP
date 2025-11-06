package com.example.projectlxp.service.section.dto;

public class SectionUpdateRequestDTO {

    private String title;
    private Integer order;

    // Constructors
    public SectionUpdateRequestDTO() {
    }

    public SectionUpdateRequestDTO(String title, Integer order) {
        this.title = title;
        this.order = order;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
