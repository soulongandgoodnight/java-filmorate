# java-filmorate
Template repository for Filmorate project.

## Database information

Database diagram: [link](dbdiagram.png)

Queries examples:

### Add user

```sql
INSERT INTO users (email, name, birthday)
VALUES ('user@example.com', 'Иван', '2000-05-10');
```

### Add film

```sql
INSERT INTO films (name, description, release_date, duration)
VALUES ('Inception', 'Sci-fi триллер', '2010-07-16', 148);
```

### Add genre and bound it with film 

```sql
INSERT INTO genres (name)
VALUES ('Sci-Fi');

INSERT INTO film_genres (film_id, genre_id)
VALUES (1, 1);
```

### Add like to a film

```sql
INSERT INTO likes (user_id, film_id)
VALUES (1, 1);
```

### Get all films liked by user

```sql
SELECT f.id, f.name, f.release_date
FROM films f
JOIN likes l ON l.film_id = f.id
WHERE l.user_id = 1;
```

### Get likes count for every film

```sql
SELECT f.name, COUNT(l.user_id) AS likes_count
FROM films f
LEFT JOIN likes l ON l.film_id = f.id
GROUP BY f.id, f.name;
```

### Get films by genre

```sql
SELECT f.name
FROM films f
JOIN film_genres fg ON fg.film_id = f.id
JOIN genres g ON g.id = fg.genre_id
WHERE g.name = 'Sci-Fi';
```

### Send friendship request

```sql
INSERT INTO friendships (following_user_id, followed_user_id, is_friendship_confirmed)
VALUES (1, 2, false);
```

### Accept friendship

```sql
UPDATE friendships
SET is_friendship_confirmed = true
WHERE following_user_id = 1
  AND followed_user_id = 2;
```

### Get friends list

```sql
SELECT u.id, u.name
FROM users u
JOIN friendships f ON f.followed_user_id = u.id
WHERE f.following_user_id = 1
  AND f.is_friendship_confirmed = true;
```