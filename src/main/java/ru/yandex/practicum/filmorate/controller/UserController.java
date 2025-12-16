package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Marker;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Validated(Marker.OnCreate.class) @RequestBody User user) {
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {} (id={})", user.getLogin(), user.getId());
        return user;
    }

    @PutMapping
    public User update(@Validated(Marker.OnUpdate.class) @RequestBody User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            String message = "Пользователь с ID " + (user.getId() != null ? user.getId() : "[не указан]") + " не найден";
            log.error("Ошибка: {}", message);
            throw new ValidationException(message);
        }
        validateUser(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь: {} (id={})", user.getLogin(),user.getId());
        return user;
    }

    private void validateUser(User user) {
        if (user.getBirthday() == null) {
            String message = "Дата рождения не может быть null";
            log.error("Ошибка валидации пользователя: {}",message);
            throw new ValidationException(message);
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
