package org.una.programmingIII.utemp_app.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PageDTO<T> {
    // Getters y setters
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int number;
    private int size;

    // Constructor

    public PageDTO() {

    }

    public PageDTO(List<T> content, int totalPages, long totalElements, int number, int size) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.number = number;
        this.size = size;
    }
}
