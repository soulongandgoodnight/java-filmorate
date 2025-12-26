MERGE INTO "ratings"
USING ( VALUES (1, '0+'), (2, '6+'), (3, '12+'), (4, '16+'), (5, '18+')) s(id, name)
on "ratings"."id" = s.id
when not matched then insert values (s.id, s.name);
