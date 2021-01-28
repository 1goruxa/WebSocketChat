package main.Controllers;

import main.Services.CheckService;
import main.Services.RegisterService;
import main.api.Request.RegisterRequest;
import main.api.Response.CheckResponse;
import main.api.Response.RegisterResponse;
import main.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class UserController {
    @Autowired
    RegisterService registerService;

    @Autowired
    CheckService checkService;

    @Autowired
    UserRepository userRepository;


    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest data){
        return registerService.register(data);
    }

    @GetMapping("/check")
    public CheckResponse check(@RequestParam String name, @RequestParam String password){
        return checkService.check(name, password);
    }

    @GetMapping("/updateUser")
    public ArrayList<String> updateOnlineUser(@RequestParam String name, @RequestParam boolean updateUser){
       ArrayList<String> users = checkService.updateUserOnlineStatus(name, updateUser);

        return users;
    }


    @GetMapping("/updateUserStomp")
    public ArrayList<String> updateAllOnlineUser(){

        ArrayList<String> users = userRepository.findAllOnline();


        return users;
    }

}
