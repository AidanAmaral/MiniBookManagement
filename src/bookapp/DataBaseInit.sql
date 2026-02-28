CREATE DATABASE IF NOT EXISTS book_inventory;
USE book_inventory;

CREATE TABLE IF NOT EXISTS books (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255) NOT NULL,
  borrowed_by VARCHAR(255)
);

INSERT INTO books (title, author, borrowed_by) VALUES
('The Hobbit', 'J.R.R. Tolkien', NULL),
('To Kill a Mockingbird', 'Harper Lee', NULL),
('1984', 'George Orwell', NULL),
('HTML5 Black Book', 'Kogent', 'Salim'),
('Pride and Prejudice', 'Jane Austen', NULL),
('Programming with Java', 'Balagurusamy', 'Vignesh'),
('The Catcher in the Rye', 'J.D. Salinger', NULL),
('The Great Gatsby', 'F. Scott Fitzgerald', 'Mohit'),
('The Lord of the Rings', 'J.R.R. Tolkien', 'Ashwin'),
('Fahrenheit 451', 'Ray Bradbury', NULL),
('The Alchemist', 'Paulo Coelho', 'Aidan'),
('The Da Vinci Code', 'Dan Brown', NULL);


