package com.example.TableTop.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FirebaseUserService {
    
    private static final Logger logger = LoggerFactory.getLogger(FirebaseUserService.class);
    private static final String DEFAULT_USERNAME = "Usuario";
    
    @Value("${firebase.authentication.enabled:false}")
    private boolean firebaseAuthEnabled;
    
    // Cache para almacenar los nombres de usuario y reducir consultas a Firebase
    private final Map<String, String> userNameCache = new ConcurrentHashMap<>();

    /**
     * Obtiene el nombre de un usuario de Firebase por su UID
     * 
     * @param uid El UID del usuario en Firebase
     * @return El nombre del usuario o un valor predeterminado si no se encuentra
     */
    public String getUserName(String uid) {
        // Si ya tenemos el nombre en caché, lo devolvemos directamente
        if (userNameCache.containsKey(uid)) {
            return userNameCache.get(uid);
        }
        
        // Si Firebase no está habilitado o disponible, devolver un nombre por defecto
        if (!isFirebaseAvailable() || !firebaseAuthEnabled) {
            String defaultName = DEFAULT_USERNAME + " " + uid.substring(0, Math.min(5, uid.length()));
            userNameCache.put(uid, defaultName);
            return defaultName;
        }
        
        try {
            // Obtener información del usuario desde Firebase Auth
            UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
            
            // Obtener el nombre de usuario (displayName)
            String userName = userRecord.getDisplayName();
            
            // Si el displayName es nulo o vacío, usar el email o un valor por defecto
            if (userName == null || userName.trim().isEmpty()) {
                userName = userRecord.getEmail();
                
                if (userName == null || userName.trim().isEmpty()) {
                    userName = DEFAULT_USERNAME + " " + uid.substring(0, Math.min(5, uid.length()));
                }
            }
            
            // Guardar en caché para futuras consultas
            userNameCache.put(uid, userName);
            
            return userName;
        } catch (FirebaseAuthException e) {
            logger.error("Error al obtener información de usuario Firebase con UID {}: {}", uid, e.getMessage());
            
            // En caso de error, usar un nombre por defecto
            String defaultName = DEFAULT_USERNAME + " " + uid.substring(0, Math.min(5, uid.length()));
            userNameCache.put(uid, defaultName);
            return defaultName;
        }
    }
    
    /**
     * Obtiene los nombres de varios usuarios de Firebase por sus UIDs
     * 
     * @param uids Lista de UIDs de usuarios
     * @return Mapa con los UIDs como claves y los nombres como valores
     */
    public Map<String, String> getUserNames(List<String> uids) {
        Map<String, String> userNames = new HashMap<>();
        
        for (String uid : uids) {
            userNames.put(uid, getUserName(uid));
        }
        
        return userNames;
    }
    
    // Método para verificar si Firebase está disponible
    private boolean isFirebaseAvailable() {
        try {
            return !FirebaseApp.getApps().isEmpty();
        } catch (Exception e) {
            logger.warn("Error al verificar si Firebase está disponible: {}", e.getMessage());
            return false;
        }
    }
} 