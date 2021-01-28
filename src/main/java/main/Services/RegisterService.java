package main.Services;

import main.api.Request.RegisterRequest;
import main.api.Response.RegisterResponse;
import main.model.User;
import main.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegisterService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    UserRepository userRepository;


    public RegisterResponse register(RegisterRequest data){
        System.out.println("Пришло с фронта" + data.getName());
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setResult(false);
        registerResponse.setError("");

        if (data.getName().equals("") || data.getPassword().equals("") ){
            registerResponse.setError("Не все поля заполнены");
        }else{

            if (data.getName().length() < 3 || data.getPassword().length() < 3){
                registerResponse.setError("Длина имени и пароля должна быть больше 3 символов");

            }
        }
        if (registerResponse.getError().equals("")) {
            Optional<User> existingUser = userRepository.findOneByName(data.getName());
            if (existingUser.isPresent()){
                registerResponse.setError("Имя пользователя занято");
            }else {
                registerResponse.setResult(true);
                User newUser = new User();
                newUser.setName(data.getName());
                newUser.setPassword(passwordEncoder.encode(data.getPassword()));
                userRepository.save(newUser);
            }

        }

        return registerResponse;
    }

}
