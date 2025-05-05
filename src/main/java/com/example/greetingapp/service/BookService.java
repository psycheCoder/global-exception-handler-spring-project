package com.example.greetingapp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.greetingapp.exception.BadRequestException;
import com.example.greetingapp.exception.ConflictException;
import com.example.greetingapp.model.Book;
import com.example.greetingapp.repository.BookRepository;

@Service
public class BookService {
	@Autowired
	private BookRepository bookRepository;
	private static final Logger LOGGER = LoggerFactory.getLogger(BookService.class);

	public Book createBook(Book book) {
		if (book.getAuthor() == null || book.getAuthor().isBlank() || book.getTitle() == null
				|| book.getTitle().isBlank()) {
			StringBuilder missingFields = new StringBuilder("Missing fields: ");
			boolean first = true;

			if (book.getAuthor() == null || book.getAuthor().isBlank()) {
				if (!first) {
					missingFields.append(", ");
				}
				missingFields.append("author");
				first = false;
			}

			if (book.getTitle() == null || book.getTitle().isBlank()) {
				if (!first) {
					missingFields.append(", ");
				}
				missingFields.append("title");
				first = false;
			}

			String message = missingFields.toString();
			LOGGER.debug(message);
			throw new BadRequestException(message);
		} else if (book.getAuthor() != null && !book.getAuthor().isBlank() && book.getTitle() != null
				&& !book.getTitle().isBlank()) {
			Book existingBook = bookRepository.findByAuthorAndTitle(book.getAuthor(), book.getTitle());
			Book existingBookByISBN = bookRepository.findByIsbn(book.getIsbn());
			if (null != existingBook) {
				String message = "A book with the same author and title already exists.";
				LOGGER.debug(message);
				throw new ConflictException(message);
			} else if (null != existingBookByISBN && existingBook == null) {

				String message = "Found same Book with ISBN ->" + existingBookByISBN.getIsbn();
				LOGGER.debug(message);
				throw new ConflictException(message);

			} else {
				return bookRepository.save(book);
			}
		}

		return null;

	}

	public List<Book> getAllBooks() {
		return bookRepository.findAll();
	}
}