package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.MessageDTO;
import ro.tuc.ds2020.services.WebSocketSender;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("api/chat")
public class ChatController {
    private final WebSocketSender webSocketSender;

    @Autowired
    public ChatController(WebSocketSender webSocketSender) {
        this.webSocketSender = webSocketSender;
    }

    @MessageMapping("/support")
    public void ceva(@Payload MessageDTO message) {
        System.out.println(message.getText() + " " + message.getReceiver() + " " + message.getSender());
        if(message.getReceiver().equals("admin"))
            webSocketSender.sendMassage("/topic/message/admin", message);
        else
            webSocketSender.sendMassage("/topic/message/" + message.getReceiver(), message);
    }
}
