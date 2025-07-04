package com.orderService.orderService.client;


import com.orderService.orderService.dto.User.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userService", url = "http://localhost:8081")
public interface UserClient {
    @GetMapping("/api/user/find/{email}")
    ResponseEntity<UserDTO> findUserByEmail(@PathVariable("email") String email);
}
