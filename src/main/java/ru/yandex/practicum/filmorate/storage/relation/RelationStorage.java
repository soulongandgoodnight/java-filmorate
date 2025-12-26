package ru.yandex.practicum.filmorate.storage.relation;

import ru.yandex.practicum.filmorate.model.Relation;

import java.util.Collection;

public interface RelationStorage {
    void addRelation(Relation relation);

    void updateRelation(Relation relation);

    void removeRelation(Relation relation);

    Collection<Relation> getAllByUserId(Long userId);

    Relation getByUserId(Long userId);
}
