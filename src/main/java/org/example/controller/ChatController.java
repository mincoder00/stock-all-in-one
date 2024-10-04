package org.example.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@Log4j2
public class ChatController {
    private AtomicInteger userCount = new AtomicInteger(1);

    @GetMapping("/chat")
    public String chatGET(){
        log.info("@ChatController, chat GET()");
        return "chater";
    }


    @GetMapping("/get-username")
    public ResponseEntity<Map<String, String>> getUsername() {
        String username = "익명" + userCount.getAndIncrement();
        Map<String, String> response = new HashMap<>();
        response.put("username", username);
        return ResponseEntity.ok(response);
    }
}
