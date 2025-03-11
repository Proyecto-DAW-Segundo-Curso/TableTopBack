package com.example.TableTop.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    @Value("${firebase.authentication.enabled:false}")
    private boolean firebaseAuthEnabled;

    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                logger.info("Inicializando Firebase Admin SDK...");
                
                // Si la autenticación de Firebase está desactivada, no intentamos inicializar
                if (!firebaseAuthEnabled) {
                    logger.info("La autenticación de Firebase está desactivada en la configuración. No se inicializará Firebase.");
                    return;
                }
                
                // Intenta cargar el archivo desde resources
                InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-credentials.json");
                
                if (serviceAccount == null) {
                    logger.error("No se pudo encontrar el archivo de credenciales en resources");
                    
                    // En desarrollo, podemos continuar sin Firebase
                    boolean isDevelopment = activeProfile.equals("default") || activeProfile.equals("dev");
                    if (isDevelopment) {
                        logger.warn("Continuando en modo desarrollo sin Firebase");
                        return;
                    } else {
                        throw new IOException("Archivo de credenciales de Firebase no encontrado en producción");
                    }
                }
                
                logger.info("Credenciales de Firebase encontradas, inicializando SDK...");
                
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                
                FirebaseApp.initializeApp(options);
                logger.info("Firebase Admin SDK inicializado correctamente");
            } else {
                logger.info("Firebase Admin SDK ya está inicializado");
            }
        } catch (Exception e) {
            boolean isDevelopment = activeProfile.equals("default") || activeProfile.equals("dev");
            
            if (isDevelopment) {
                logger.warn("Error al inicializar Firebase Admin SDK en modo desarrollo: {}", e.getMessage());
                logger.warn("La aplicación continuará pero la autenticación utilizará el modo temporal");
            } else {
                logger.error("Error al inicializar Firebase Admin SDK en producción", e);
                throw new RuntimeException("Error al inicializar Firebase Admin SDK", e);
            }
        }
    }
} 