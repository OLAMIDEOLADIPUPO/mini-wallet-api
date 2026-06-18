package com.olamide.miniwalletapi.DTO;

import java.util.List;

public record PagedResponseDTO<T>(
        List<T> content,
        boolean hasNext,
        int pageNumber,
        int pageSize

){
}
