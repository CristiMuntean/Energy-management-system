package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.dtos.MessageDTO;

@Component
public class WebSocketSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketSender.class);
    private final SimpMessagingTemplate template;

    @Autowired
    public WebSocketSender(SimpMessagingTemplate template) {
        this.template = template;
    }

//    public void sendMessage(String destination, WarningMessageDTO messageDTO) {
//        template.convertAndSend(destination, messageDTO);
//    }
//
//    public void sendMessage(String destination, LiveReadingDTO messageDTO) {
//        template.convertAndSend(destination, messageDTO);
//    }

    public void sendMassage(String destination, MessageDTO messageDTO) {
        template.convertAndSend(destination, messageDTO);
    }
}
