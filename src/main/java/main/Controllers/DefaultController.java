package main.Controllers;

import main.Services.MessageService;
import main.model.Message;
import main.model.OutputMessage;
import main.model.OutputUsers;
import main.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@EnableAutoConfiguration
@Controller
public class DefaultController {

    @Autowired
    MessageService messageService;

    @Autowired
    UserRepository userRepository;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public OutputMessage send(Message message) throws Exception {
        Date date = new Date();
        String time = new SimpleDateFormat("HH:mm").format(date);
        String timeForDb = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss").format(date);
        message.setSendTime(timeForDb);
        System.out.println(message.getAuthor() + " " + message.getTextMessage());
        messageService.handleMessage(message);
        return new OutputMessage(message.getAuthor(), message.getTextMessage(), time);
    }

    @MessageMapping("/updusr")
    @SendTo("/topic/userlist")
    public OutputUsers userOnlineList() throws Exception {
        OutputUsers users = new OutputUsers();
        users.setUsersOnline(userRepository.findAllOnline());
        return users;
    }


}
