insert into authors (first_name, last_name)
VALUES ('Kacper', 'Stysinski');
insert into genres (name)
values ('history');
insert into books (description, title, year_of_first_release, genre_id)
VALUES ('nice book', 'Fantastic life of Kacper S.', 2021, 1);
insert into books_authors (book_id, author_id)
values (1, 1);
insert into book_instances
(alternative_title, language_code, pages_count, publisher_name, year_of_release, book_id)
VALUES (null, 'pl-PL', 1000, 'Znak', 2021, 1);