package com.impostor.impostor_api.service;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WordBank {

    private final Map<String, List<String>> bank = new HashMap<>();
    private final Random r = new Random();

    public WordBank() {
        bank.put("TECNOLOGIA", List.of("api", "docker", "microservicios", "router", "servidor"));
        bank.put("COMIDA", List.of("arepa", "pizza", "sushi", "empanada", "hamburguesa"));
        bank.put("ANIMALES", List.of("gato", "perro", "tigre", "delfin", "aguila"));
        bank.put("LUGARES", List.of("playa", "montaña", "parque", "aeropuerto", "biblioteca"));
        bank.put("OBJETOS", List.of("celular", "audifonos", "silla", "reloj", "camara"));
    }

    public boolean exists(String category) {
        return category != null && bank.containsKey(category.toUpperCase());
    }

    public String random(String category) {
        String key = category.toUpperCase();
        List<String> words = bank.get(key);
        return words.get(r.nextInt(words.size()));
    }
}