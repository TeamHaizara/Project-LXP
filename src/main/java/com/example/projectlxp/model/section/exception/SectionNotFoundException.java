package com.example.projectlxp.model.section.exception;

import com.example.projectlxp.exception.BusinessException;

import static com.example.projectlxp.exception.ExceptionCode.SECTION_NOT_FOUND;


public class SectionNotFoundException extends BusinessException {
    public SectionNotFoundException(Long id) {
        super(SECTION_NOT_FOUND, id);
    }
}
