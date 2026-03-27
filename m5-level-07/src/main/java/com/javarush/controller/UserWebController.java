package com.javarush.controller;

import com.javarush.entity.User;
import com.javarush.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/users-web")
@RequiredArgsConstructor
public class UserWebController {

    private final UserRepository userRepository;

    @GetMapping("/list") // localhost:8080/users-web/list
    public String listUsers(@RequestParam(value = "name", required = false) String name, Model model) {

        List<User> userList;
        if (name != null && !name.isEmpty()) {
            userList = userRepository.findByName(name);
            model.addAttribute("searchName", name);
        } else {
            userList = userRepository.findAll();
        }
        model.addAttribute("users", userList);
        return "user-list"; // user-list.html
    }

//    @GetMapping("/new") // localhost:8080/users-web/new
//    public String showCreateForm(Model model) {
//        model.addAttribute("user", new User());
//        return "user-form"; // user-form.html
//    }

    @GetMapping("/view/{id}") // localhost:8080/users-web/view/1
    public String viewUserDetails(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Пользователь с таким id не найден: " + id));
        model.addAttribute("user", user);
        return "user-details"; // user-details.html
    }

}
