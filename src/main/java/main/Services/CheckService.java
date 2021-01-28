package main.Services;

import main.api.Response.CheckResponse;
import main.model.User;
import main.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CheckService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    UserRepository userRepository;

    public CheckResponse check(String name, String password){
        CheckResponse checkResponse = new CheckResponse();
        checkResponse.setResult(false);
        checkResponse.setError("");

        if (name.equals("") || password.equals("") ){
            checkResponse.setError("Не все поля заполнены");
        }else{
            Optional<User> optionalUser = userRepository.findOneByName(name);
            if (!optionalUser.isPresent()){
                checkResponse.setError("Пользователь не найден");
            } else{
                User user = optionalUser.get();

                if (passwordEncoder.matches(password, user.getPassword()) && user.getOnline() == 0) {

                    checkResponse.setResult(true);
                }
                else{
                    checkResponse.setError("Пароль указан неверно либо пользователь уже авторизован");
                }
            }

        }
        return checkResponse;
    }

    public void dropAllUsers(){
       userRepository.dropAllUsers();
    }

    public ArrayList<String> updateUserOnlineStatus(String name, boolean status){

        Optional<User> optionalUser = userRepository.findOneByName(name);
        User user = optionalUser.get();
        if (status){
            user.setOnline(1);
        } else{
            user.setOnline(0);
        }
        userRepository.save(user);

        ArrayList<String> onlineUsers = userRepository.findAllOnline();
        return onlineUsers;
    }
}
