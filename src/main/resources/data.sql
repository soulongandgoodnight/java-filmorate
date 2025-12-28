MERGE INTO RATINGS
USING ( VALUES (1, '0+'), (2, '6+'), (3, '12+'), (4, '16+'), (5, '18+')) s(ID, NAME)
on RATINGS.ID = s.ID
when not matched then insert values (s.ID, s.NAME);

MERGE INTO GENRES
USING ( VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик')) s(ID, NAME)
on GENRES.ID = s.ID
when not matched then insert values (s.ID, s.NAME);