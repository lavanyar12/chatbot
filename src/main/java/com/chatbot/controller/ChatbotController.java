package com.chatbot.controller;

import com.chatbot.model.ChatbotMessage;
import com.chatbot.model.IncomingMessage;
import com.chatbot.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatbotController {

    @Autowired
    private ChatbotService service;

    @MessageMapping("/chat")
    @SendTo("/topic/chatbot")
    public ChatbotMessage chatMessage(IncomingMessage message) throws Exception {
        Thread.sleep(500); // delay
        return service.getChatbotMessage(message);
    }
}
