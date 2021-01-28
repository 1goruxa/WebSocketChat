package Bot;


import main.model.UserLinkPair;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    //Список уникальных ссылок, если мы сгенерировали уже существующую ссылку, то мы не можем дать ее тому же пользователю
    //В базе хранить не будем
    ArrayList<UserLinkPair> uniqueLinkList = new ArrayList<>();
    private final String botName;
    StompSession session;

    public MyStompSessionHandler(String botName) {
        this.botName = botName;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        this.session = session;
        System.out.println("New session established : " + session.getSessionId());
        session.subscribe("/topic/messages", this);
        System.out.println("Subscribed to /topic/messages");
        //session.send("/chat",getSampleMessage());
        session.send("/updusr",new BotMessage());

    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.out.println("Got an exception" + exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return BotMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        BotMessage msg = (BotMessage) payload;
        System.out.println("Received : " + msg.getTextMessage() + " from : " + msg.getAuthor());
        if (msg.getTextMessage().startsWith("@" + botName + ":")){
            System.out.println("генерим ссылку");
            BotMessage linkMessage = getSampleLink(msg.getAuthor());
            session.send("/chat", linkMessage);
        }
    }

    private BotMessage getSampleLink(String author) {

        BotMessage msg = new BotMessage();
        msg.setAuthor(botName);
        //Уникальные ссылки!
        //1-82000

        AtomicBoolean unique = new AtomicBoolean(false);
        int issueNumber;

        do {
            unique.set(true);
            issueNumber = 1000 + (int) (Math.random() * 82000);

            int finalIssueNumber = issueNumber;
            uniqueLinkList.forEach(u ->{
                System.out.println(u.getUsername() + " " + u.getLink());
                if(u.getUsername().equals(author) && u.getLink()== finalIssueNumber){
                    unique.set(false);
                }
            });
        }while (!unique.get());

        UserLinkPair userLinkPair = new UserLinkPair();
        userLinkPair.setUsername(author);
        userLinkPair.setLink(issueNumber);
        uniqueLinkList.add(userLinkPair);

        String link = "https://news.drom.ru/" + issueNumber + ".html";
        msg.setTextMessage(link);

        return msg;
    }
}