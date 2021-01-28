package main.Services;

import main.model.Message;
import main.repo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Value("${msg_db_size}")
    int dbSize;

    @Autowired
    MessageRepository messageRepository;

    public void handleMessage(Message msg){

        //Сохраняем в базу и чистим базу от старых сообщений
        synchronized (msg) {
            messageRepository.save(msg);
            int count = messageRepository.countAll();
            if (count > dbSize){
                Message goodbyeOne = new Message();
                goodbyeOne = messageRepository.findOldest();
                messageRepository.delete(goodbyeOne);
            };
        }

    }
}
