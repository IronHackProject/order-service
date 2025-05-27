package com.orderService.orderService.client;

import com.orderService.orderService.dto.Product.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "productService", url = "http://localhost:8082")
public interface ProductClient {
    // find product by id
    @GetMapping("/api/product/find/{id}")
    ResponseEntity<ProductDTO> findById(@PathVariable("id") long id);
    ResponseEntity<?>subQuantity(@PathVariable("id") long id, @PathVariable("quantity") int quantity);
}
