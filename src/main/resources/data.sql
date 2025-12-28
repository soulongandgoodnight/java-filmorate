MERGE INTO "ratings"
USING ( VALUES (1, '0+'), (2, '6+'), (3, '12+'), (4, '16+'), (5, '18+')) s(id, name)
on "ratings"."id" = s.id
when not matched then insert values (s.id, s.name);

MERGE INTO "genres"
USING ( VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик')) s(id, name)
on "genres"."id" = s.id
when not matched then insert values (s.id, s.name);