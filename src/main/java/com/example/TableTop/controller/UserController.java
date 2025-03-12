package com.example.TableTop.controller;

import com.example.TableTop.service.FirebaseUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private FirebaseUserService firebaseUserService;
    
    /**
     * Obtiene el nombre de un usuario de Firebase por su UID
     * 
     * @param uid El UID del usuario en Firebase
     * @return El nombre del usuario
     */
    @GetMapping("/{uid}")
    public String getUserName(@PathVariable String uid) {
        logger.info("Solicitud de nombre para usuario con UID: {}", uid);
        return firebaseUserService.getUserName(uid);
    }
    
    /**
     * Obtiene los nombres de múltiples usuarios de Firebase por sus UIDs
     * 
     * @param uids Lista de UIDs de usuarios
     * @return Mapa con los UIDs como claves y los nombres como valores
     */
    @PostMapping("/names")
    public Map<String, String> getUserNames(@RequestBody List<String> uids) {
        logger.info("Solicitud de nombres para {} usuarios", uids.size());
        return firebaseUserService.getUserNames(uids);
    }
} 