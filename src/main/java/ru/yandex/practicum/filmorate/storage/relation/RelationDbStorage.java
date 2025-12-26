package ru.yandex.practicum.filmorate.storage.relation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Relation;

import java.util.Collection;
import java.util.List;

@Component
@Qualifier("relationDbStorage")
public class RelationDbStorage implements RelationStorage {
    @Override
    public void addRelation(Relation relation) {

    }

    @Override
    public void updateRelation(Relation relation) {

    }

    @Override
    public void removeRelation(Relation relation) {

    }

    @Override
    public Collection<Relation> getAllByUserId(Long userId) {
        return List.of();
    }

    @Override
    public Relation getByUserId(Long userId) {
        return null;
    }
}
