CREATE TABLE IF NOT EXISTS "users" (
  "id" bigserial PRIMARY KEY,
  "email" varchar,
  "login" varchar,
  "name" varchar,
  "birthday" date
);

CREATE TABLE IF NOT EXISTS "friendships" (
  "following_user_id" bigint,
  "followed_user_id" bigint,
  "is_friendship_confirmed" bool,
  PRIMARY KEY ("following_user_id", "followed_user_id")
);

CREATE TABLE IF NOT EXISTS "films" (
  "id" bigserial PRIMARY KEY,
  "name" varchar,
  "description" varchar,
  "release_date" date,
  "duration" int,
  "rating_id" int
);

CREATE TABLE IF NOT EXISTS "likes" (
  "film_id" bigint,
  "user_id" bigint,
  PRIMARY KEY ("film_id", "user_id")
);

CREATE TABLE IF NOT EXISTS "genres" (
  "id" bigserial PRIMARY KEY,
  "name" varchar
);

CREATE TABLE IF NOT EXISTS "film_genres" (
  "film_id" bigint,
  "genre_id" bigint,
  PRIMARY KEY ("film_id", "genre_id")
);

CREATE TABLE IF NOT EXISTS "ratings" (
  "id" bigserial PRIMARY KEY,
  "name" varchar NOT NULL UNIQUE
);

ALTER TABLE "friendships" ADD FOREIGN KEY ("following_user_id") REFERENCES "users" ("id");

ALTER TABLE "friendships" ADD FOREIGN KEY ("followed_user_id") REFERENCES "users" ("id");

ALTER TABLE "likes" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "likes" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("id");

ALTER TABLE "film_genres" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("id");

ALTER TABLE "film_genres" ADD FOREIGN KEY ("genre_id") REFERENCES "genres" ("id");

ALTER TABLE "films" ADD FOREIGN KEY ("rating_id") REFERENCES "ratings" ("id");
