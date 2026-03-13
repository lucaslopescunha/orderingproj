package com.sop.orderingproj;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrdersController {
    @GetMapping("testes")
    public ResponseEntity<String> getOrder() {
        return ResponseEntity.ok("mudou.");
    }
}
