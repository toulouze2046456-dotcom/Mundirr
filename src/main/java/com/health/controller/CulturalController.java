package com.health.controller;

import com.health.entity.CulturalBook;
import com.health.entity.User;
import com.health.repository.CulturalBookRepository;
import com.health.service.OpenLibraryService;
import com.health.service.WikidataMovieService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for Cultural/Cognitive health tracking.
 * Integrated with Open Library API for book search and metadata.
 */
@RestController
@RequestMapping("/api/cultural")
public class CulturalController extends BaseController {
    
    @Autowired
    private CulturalBookRepository bookRepo;
    
    @Autowired
    private OpenLibraryService openLibraryService;
    
    // ============ BOOK TRACKING ============
    
    /**
     * Get all books for the authenticated user.
     */
    @GetMapping("/books")
    public List<CulturalBook> getBooks(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        return bookRepo.findByUserId(user.getId());
    }
    
    /**
     * Add a book to the user's library.
     */
    @PostMapping("/books")
    public CulturalBook addBook(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        User user = getAuthenticatedUser(request);
        
        String title = (String) body.getOrDefault("title", "Untitled");
        String author = (String) body.getOrDefault("author", "Unknown");
        int progress = body.get("progress") instanceof Number ? ((Number) body.get("progress")).intValue() : 0;
        String category = (String) body.getOrDefault("category", "General");
        String status = (String) body.getOrDefault("status", "Reading");
        
        CulturalBook book = new CulturalBook(user.getId(), title, author, progress, category);
        book.setStatus(status);
        
        return bookRepo.save(book);
    }
    
    /**
     * Update book progress.
     */
    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(HttpServletRequest request, @PathVariable Long id, 
                                         @RequestBody Map<String, Object> body) {
        User user = getAuthenticatedUser(request);
        
        return bookRepo.findById(id)
                .filter(book -> book.getUserId().equals(user.getId()))
                .map(book -> {
                    if (body.containsKey("progress")) {
                        book.setProgress(((Number) body.get("progress")).intValue());
                    }
                    if (body.containsKey("status")) {
                        book.setStatus((String) body.get("status"));
                    }
                    if (body.containsKey("category")) {
                        book.setCategory((String) body.get("category"));
                    }
                    return ResponseEntity.ok(bookRepo.save(book));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete a book.
     */
    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(HttpServletRequest request, @PathVariable Long id) {
        User user = getAuthenticatedUser(request);
        
        return bookRepo.findById(id)
                .filter(book -> book.getUserId().equals(user.getId()))
                .map(book -> {
                    bookRepo.delete(book);
                    return ResponseEntity.ok(Map.of("success", true));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // ============ OPEN LIBRARY SEARCH ============
    
    /**
     * Search books using Open Library API.
     */
    @GetMapping("/search")
    public List<Map<String, Object>> searchBooks(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int limit) {
        return openLibraryService.searchBooks(query, limit);
    }
    
    /**
     * Search books by author name.
     */
    @GetMapping("/search/author")
    public List<Map<String, Object>> searchByAuthor(
            @RequestParam String name,
            @RequestParam(defaultValue = "20") int limit) {
        return openLibraryService.searchByAuthor(name, limit);
    }
    
    /**
     * Search books by subject/category.
     */
    @GetMapping("/search/subject")
    public List<Map<String, Object>> searchBySubject(
            @RequestParam String subject,
            @RequestParam(defaultValue = "20") int limit) {
        return openLibraryService.searchBySubject(subject, limit);
    }
    
    /**
     * Get book details by Open Library work key.
     */
    @GetMapping("/details/{workKey}")
    public ResponseEntity<Map<String, Object>> getBookDetails(@PathVariable String workKey) {
        Map<String, Object> details = openLibraryService.getBookDetails(workKey);
        if (details.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }
    
    /**
     * Get book by ISBN.
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<Map<String, Object>> getBookByIsbn(@PathVariable String isbn) {
        Map<String, Object> book = openLibraryService.getBookByIsbn(isbn);
        if (book.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }
    
    /**
     * Get author details.
     */
    @GetMapping("/author/{authorKey}")
    public ResponseEntity<Map<String, Object>> getAuthorDetails(@PathVariable String authorKey) {
        Map<String, Object> author = openLibraryService.getAuthorDetails(authorKey);
        if (author.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(author);
    }
    
    /**
     * Get available book categories.
     */
    @GetMapping("/categories")
    public List<String> getCategories() {
        return openLibraryService.getCategories();
    }
    
    // ============ MOVIE SEARCH (Wikidata) ============
    
    @Autowired
    private WikidataMovieService wikidataMovieService;
    
    /**
     * Search movies using Wikidata API.
     */
    @GetMapping("/movies/search")
    public List<Map<String, Object>> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "15") int limit) {
        return wikidataMovieService.searchMovies(query, limit);
    }
    
    /**
     * Get movie details by Wikidata entity ID.
     */
    @GetMapping("/movies/{entityId}")
    public ResponseEntity<Map<String, Object>> getMovieDetails(@PathVariable String entityId) {
        Map<String, Object> details = wikidataMovieService.getMovieDetails(entityId);
        if (details.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }
}
