package org.una.programmingIII.utemp_app.services.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PageResponse<T> {
    private List<T> data;
    private long totalElements;
    private int totalPages;
    private int pageNumber; // número de la página actual
    private int pageSize;   // tamaño de la página

    // Constructor
    public PageResponse(List<T> data, long totalElements, int totalPages, int pageNumber, int pageSize) {
        this.data = data;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
}
