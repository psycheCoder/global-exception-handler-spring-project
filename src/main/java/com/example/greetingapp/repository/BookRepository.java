package com.example.greetingapp.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.example.greetingapp.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
	Book findByAuthorAndTitle(String author, String title);
	Book findByIsbn(String isbn);
}