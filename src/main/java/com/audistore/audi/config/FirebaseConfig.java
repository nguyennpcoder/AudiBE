package com.audistore.audi.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Initialises the Firebase Admin SDK for project audi-1109.
 *
 * The service account is read from (in order):
 *   1. FIREBASE_SERVICE_ACCOUNT / FIREBASE_SERVICE_ACCOUNT_BASE64 env var (Render)
 *   2. firebase-service-account.json on the classpath (local development only)
 *
 * The JSON key must never be committed to the repository.
 */
@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_SERVICE_ACCOUNT:}")
    private String serviceAccountJson;

    @Value("${FIREBASE_SERVICE_ACCOUNT_BASE64:}")
    private String serviceAccountBase64;

    @Value("${FIREBASE_PROJECT_ID:audi-1109}")
    private String projectId;

    @PostConstruct
    public void initialize() {
        try (InputStream serviceAccount = openServiceAccount()) {
            if (serviceAccount == null) {
                System.out.println("Firebase service account not configured - Firebase features disabled");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(projectId)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully (project " + projectId + ")");
            }
        } catch (IOException e) {
            System.out.println("Firebase service account not found or invalid: " + e.getMessage());
            System.out.println("SMS OTP functionality will be disabled");
        } catch (Exception e) {
            System.out.println("Error initializing Firebase: " + e.getMessage());
            System.out.println("SMS OTP functionality will be disabled");
        }
    }

    private InputStream openServiceAccount() throws IOException {
        if (serviceAccountBase64 != null && !serviceAccountBase64.isBlank()) {
            byte[] decoded = Base64.getDecoder().decode(serviceAccountBase64.trim());
            return new ByteArrayInputStream(decoded);
        }
        if (serviceAccountJson != null && !serviceAccountJson.isBlank()) {
            return new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8));
        }
        ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
        return resource.exists() ? resource.getInputStream() : null;
    }
}
