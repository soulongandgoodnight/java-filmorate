package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public User getById(Long id) {
        return null;
    }

    @Override
    public Collection<User> getByIds(Collection<Long> ids) {
        return List.of();
    }

    @Override
    public Collection<User> findAll() {
        return List.of();
    }
}
