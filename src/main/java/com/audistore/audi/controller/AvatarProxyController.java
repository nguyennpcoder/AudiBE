package com.audistore.audi.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/proxy")
@CrossOrigin(origins = "*")
public class AvatarProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/avatar")
    public ResponseEntity<byte[]> proxyAvatar(@RequestParam String url) {
        try {
            System.out.println("=== Avatar Proxy Debug ===");
            System.out.println("Requested URL: " + url);
            
            // Validate URL
            if (url == null || url.trim().isEmpty()) {
                System.out.println("Error: Invalid URL provided");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            // Check if it's a Google or Facebook URL
            if (!url.contains("googleusercontent.com") && !url.contains("facebook.com")) {
                System.out.println("Error: URL is not from Google or Facebook: " + url);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            System.out.println("Making request to: " + url);
            
            // Make the request
            ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Set response headers
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.setContentType(MediaType.IMAGE_JPEG); // Default to JPEG
                responseHeaders.setCacheControl("public, max-age=3600"); // Cache for 1 hour
                responseHeaders.set("Access-Control-Allow-Origin", "*");
                responseHeaders.set("Access-Control-Allow-Methods", "GET");
                responseHeaders.set("Access-Control-Allow-Headers", "*");
                
                System.out.println("Success: Proxied avatar successfully, size: " + response.getBody().length + " bytes");
                
                return new ResponseEntity<>(response.getBody(), responseHeaders, HttpStatus.OK);
            } else {
                System.out.println("Error: Failed to get avatar, status: " + response.getStatusCode());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            
        } catch (HttpClientErrorException e) {
            System.out.println("Error: HTTP error while proxying avatar: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ResourceAccessException e) {
            System.out.println("Error: Network error while proxying avatar: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            System.out.println("Error: Unexpected error while proxying avatar: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 