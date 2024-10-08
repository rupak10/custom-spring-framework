package com.rupak.models.dto;

import com.rupak.models.Product;
import lombok.Data;

import java.util.List;

@Data
public class SearchResponse {
    private List<Product> products;
}
