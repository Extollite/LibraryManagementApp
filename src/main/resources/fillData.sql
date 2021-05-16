-- insert into authors (first_name, last_name)
-- VALUES ('Kacper', 'Stysinski'),
--        ('Jedrzej', 'Racibor'),
--        ('Jan', 'Kochanowski');
-- 
-- 
-- insert into genres (name)
-- values ('History'),
--        ('S-F');
-- insert into books (description, title, year_of_first_release, genre_id)
-- VALUES ('Nice book.', 'Fantastic life of Kacper S.', 2021, 1),
--        ('Nice book.', 'Minecraft', 2021, 2);
-- insert into books_authors (book_id, author_id)
-- values (1, 1),
--        (2, 2),
--        (2, 3);
-- insert into book_copies
-- (alternative_title, language_code, pages_count, publisher_name, year_of_release, book_id)
-- VALUES (null, 'pl-PL', 1000, 'Znak', 2021, 1),
--        (null, 'pl-PL', 1000, 'Znak', 2021, 1),
--        ('Is cool', 'pl-PL', 1000, 'Znak', 2021, 2);
-- insert into book_history (due_date, book_copy_id, returned_date)
-- values (current_timestamp, 1, null);

insert into genres (id, name)
values (0, 'Antiques & Collectibles'),
       (1, 'Architecture'),
       (2, 'Art'),
       (3, 'Bibles'),
       (4, 'Biography & Autobiography'),
       (5, 'Body, Mind & Spirit'),
       (6, 'Business & Economics'),
       (7, 'Comics & Graphic Novels'),
       (8, 'Computers'),
       (9, 'Cooking'),
       (10, 'Crafts & Hobbies'),
       (11, 'Design'),
       (12, 'Drama'),
       (13, 'Education'),
       (14, 'Family & Relationships'),
       (15, 'Fiction'),
       (16, 'Foreign Language Study'),
       (17, 'Games & Activities'),
       (18, 'Gardening'),
       (19, 'Health & Fitness'),
       (20, 'History'),
       (21, 'House & Home'),
       (22, 'Humor'),
       (23, 'Juvenile Fiction'),
       (24, 'Juvenile Nonfiction'),
       (25, 'Language Arts & Disciplines'),
       (26, 'Law'),
       (27, 'Literary Collections'),
       (28, 'Literary Criticism'),
       (29, 'Mathematics'),
       (30, 'Medical'),
       (31, 'Music'),
       (32, 'Nature'),
       (33, 'Performing Arts'),
       (34, 'Pets'),
       (35, 'Philosophy'),
       (36, 'Photography'),
       (37, 'Poetry'),
       (38, 'Political Science'),
       (39, 'Psychology'),
       (40, 'Reference'),
       (41, 'Religion'),
       (42, 'Science'),
       (43, 'Self-help'),
       (44, 'Social Science'),
       (45, 'Sports & Recreation'),
       (46, 'Study Aids'),
       (47, 'Technology & Engineering'),
       (48, 'Transportation'),
       (49, 'Travel'),
       (50, 'True Crime'),
       (51, 'Young Adult Fiction'),
       (52, 'Young Adult Nonfiction');
 