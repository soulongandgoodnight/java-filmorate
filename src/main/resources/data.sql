MERGE INTO RATINGS
USING ( VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17')) s(ID, NAME)
on RATINGS.ID = s.ID
when not matched then insert values (s.ID, s.NAME);

MERGE INTO GENRES
USING ( VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик')) s(ID, NAME)
on GENRES.ID = s.ID
when not matched then insert values (s.ID, s.NAME);