package com.example.TableTop.security;

import java.util.Base64;

public class TestKeys {
    public static void main(String[] args) {
        String secret = "urBzYFAp97kmkPTNjw25ZAWLUTP9EbYRbI/zXVk/OYY=";
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        System.out.println("Tamaño de la clave: " + keyBytes.length * 8 + " bits");
    }
}

