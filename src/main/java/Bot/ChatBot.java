package Bot;

import com.fasterxml.jackson.databind.ObjectMapper;

import main.api.Response.CheckResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;


public class ChatBot {

    private String userName;
    private String password;
    private String mainLinkSource;


    public static void main(String[] args) throws IOException {
        System.out.println("Авторизуем бота");

        ChatBot chatBot = new ChatBot();
        chatBot.setUserName("AskMeAboutCars");
        chatBot.setPassword("123456");
        chatBot.setMainLinkSource("http://www.drom.ru");
        chatBot.connect(chatBot.getUserName(), chatBot.getPassword());

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMainLinkSource() {
        return mainLinkSource;
    }

    public void setMainLinkSource(String mainLinkSource) {
        this.mainLinkSource = mainLinkSource;
    }

    public boolean connect(String name, String password) throws IOException {
        //Логонимся либо регистрируем и логонимся

        //Сначала проверим существует ли бот уже
        String checkName = "http://localhost:8080/check?name="+name+"&password="+password;
        final URL urlCheck = new URL(checkName);

        final HttpURLConnection conCheck = (HttpURLConnection) urlCheck.openConnection();
        conCheck.setRequestMethod("GET");
        conCheck.setRequestProperty("Content-Type", "application/json");
        StringBuilder content = new StringBuilder();
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(conCheck.getInputStream()))) {
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

        } catch (final Exception ex) {
            ex.printStackTrace();
            System.out.println("----");;
        }

        //Здесь парсинг ответа и принятие решения.
        ObjectMapper mapper = new ObjectMapper();
        CheckResponse checkResponse = mapper.readValue(content.toString(), CheckResponse.class);

        if (checkResponse.isResult()){
            System.out.println("Нашли запись о боте в БД, логонимся");

            String updateUserUrl = "http://localhost:8080/updateUser?name="+name+"&updateUser=true";
            final URL urlUpdateUser = new URL(updateUserUrl);
            final HttpURLConnection conUpdate = (HttpURLConnection) urlUpdateUser.openConnection();
            conUpdate.setRequestMethod("GET");
            conUpdate.setRequestProperty("Content-Type", "application/json");
            content = new StringBuilder();
            try (final BufferedReader in = new BufferedReader(new InputStreamReader(conUpdate.getInputStream()))) {
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

            } catch (final Exception ex) {
                ex.printStackTrace();
                System.out.println("----");;
            }
            System.out.println("Залогонились" + content.toString());
            setupWebsocketClient(name);

        }
        else{
            System.out.println(checkResponse.getError());
            if(checkResponse.getError().equals("Пользователь не найден")){
                System.out.println("Пробуем создать...");
                //Регистрация пользователя
                final CloseableHttpClient httpclient = HttpClients.createDefault();

                String JSON_STRING = "{\"name\" : \"" + name + "\", \"password\" : \"" + password + "\"}";

                StringEntity requestEntity = new StringEntity(
                        JSON_STRING,
                        ContentType.APPLICATION_JSON);

                HttpPost postMethod = new HttpPost("http://localhost:8080/register");
                postMethod.setEntity(requestEntity);

                HttpResponse rawResponse = httpclient.execute(postMethod);
                httpclient.close();
                connect(name,password);
            }
        }

        return true;
    }

    //Дальше подписываемся на сокет /chat /topic/messages
    private void setupWebsocketClient(String botName){
        String URL = "ws://localhost:8080/chat";
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSessionHandler sessionHandler = new MyStompSessionHandler(botName);
        stompClient.connect(URL, sessionHandler);


        new Scanner(System.in).nextLine();
    }





}
