insert into authors (first_name, last_name)
VALUES ('Kacper', 'Stysinski'),
       ('Jedrzej', 'Racibor'),
       ('Jan', 'Kochanowski');
insert into genres (name)
values ('History'),
       ('S-F');
insert into books (description, title, year_of_first_release, genre_id)
VALUES ('Nice book.', 'Fantastic life of Kacper S.', 2021, 1),
       ('Nice book.', 'Minecraft', 2021, 2);
insert into books_authors (book_id, author_id)
values (1, 1),
       (2, 2),
       (2, 3);
insert into book_copies
(alternative_title, language_code, pages_count, publisher_name, year_of_release, book_id)
VALUES (null, 'pl-PL', 1000, 'Znak', 2021, 1),
       (null, 'pl-PL', 1000, 'Znak', 2021, 1),
       ('Is cool', 'pl-PL', 1000, 'Znak', 2021, 2);
insert into book_history (due_date, book_copy_id, returned_date)
values (current_timestamp, 1, null);