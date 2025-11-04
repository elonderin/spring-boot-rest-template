package com.example.hello.impl;

import com.example.hello.api.HelloApi;
import com.example.hello.model.HelloResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloApiController implements HelloApi {

    @Override
    public ResponseEntity<HelloResponse> getHello() {
        HelloResponse response = new HelloResponse();
        response.setMessage("Hello, World!");
        return ResponseEntity.ok(response);
    }
}
