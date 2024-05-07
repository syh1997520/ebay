package com.homework.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.code.entity.ResourceRequest;
import com.homework.code.entity.User;
import com.homework.code.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccessController {

    @Autowired
    private AccessService accessService;

    @PostMapping("/admin/addUser")
    public ResponseEntity addUserAccess(@RequestHeader("Authorization") String authHeader, @RequestBody ResourceRequest request) {
        if (!isAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.Only admins can do this");
        }
        accessService.addUserAccess(request.getUserId(), request.getEndpoint());
        return ResponseEntity.ok("Access added successfully.");
    }

    @GetMapping("/user/{resource}")
    public ResponseEntity checkUserAccess(@RequestHeader("Authorization") String authHeader, @PathVariable String resource) {
        User user = extractUserFromHeader(authHeader);
        if (isAdmin(authHeader) || accessService.hasAccess(user.getUserId(), resource)) {
            return ResponseEntity.ok("Access granted.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        }
    }

    private boolean isAdmin(String authHeader) {
        try {
            User user = extractUserFromHeader(authHeader);
            return "admin".equals(user.getRole());
        } catch (Exception e) {
            throw new RuntimeException("Error decoding authorization header", e);
        }
    }

    private User extractUserFromHeader(String authHeader) {
        try {
            byte[] decodedBytes = Base64Utils.decodeFromString(authHeader);
            String decodedHeader = new String(decodedBytes);
            ObjectMapper mapper = new ObjectMapper();
            User userInfo = mapper.readValue(decodedHeader, User.class);
            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("Error decoding authorization header", e);
        }
    }
}
