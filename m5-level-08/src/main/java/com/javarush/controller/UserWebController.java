package com.javarush.controller;

import com.javarush.dto.CreateUserDto;
import com.javarush.entity.User;
import com.javarush.exception.ResourceNotFoundException;
import com.javarush.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users-web")
@RequiredArgsConstructor
public class UserWebController {

    private final UserRepository userRepository;

    // Отображение формы создания
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("userDto", new CreateUserDto());
        return "create-user";
    }

    // Обработка отправки формы
    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("userDto") CreateUserDto userDto,
                             BindingResult bindingResult,
                             Model model) {
        // Если есть ошибки валидации
        if (bindingResult.hasErrors()) {
            return "create-user"; // остаемся на форме, ошибки уже в модели
        }

        // Нет ошибок - сохраняем пользователя
        User user = new User(userDto.getName(), userDto.getEmail());
        userRepository.save(user);
        return "redirect:/users-web/list";
    }

    // Список пользователей
    @GetMapping("/list")
    public String listUsers(@RequestParam(required = false) String name, Model model) {
        model.addAttribute("searchName", name);
        if (name != null && !name.isBlank()) {
            model.addAttribute("users", userRepository.findAll());
        }
        return "user-list";
    }

    @GetMapping("/{id}")
    public String getUserDetails(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с ID " + id + " не найден"));
        model.addAttribute("user", user);
        return "user-details";
    }

}
