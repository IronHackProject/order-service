package com.orderService.orderService.client;

import com.orderService.orderService.dto.Product.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@FeignClient(name = "productService", url = "http://localhost:8082")
public interface ProductClient {
    // find product by id
    @GetMapping("/api/product/type/{id}")
    ResponseEntity<ProductDTO> findById(@PathVariable("id") long id);
    @PostMapping("api/product/subQuantity/{id}/{quantity}")
    ResponseEntity<?>subQuantity(@PathVariable("id") long id, @PathVariable("quantity") int quantity);
    @GetMapping("api/product/{id}/{quantity}")
    Boolean isProductAvailable(@PathVariable("id") long id, @PathVariable("quantity") int quantity);
}
