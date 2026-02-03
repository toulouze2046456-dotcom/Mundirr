package com.health.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

/**
 * Service for integrating with Open Library API.
 * Provides access to book information, author data, and cover images.
 * 
 * API Documentation: https://openlibrary.org/developers/api
 * Note: Open Library API is free and does not require an API key.
 */
@Service
public class OpenLibraryService {
    
    @Value("${openlibrary.api.base-url:https://openlibrary.org}")
    private String baseUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Book category mappings for common genres
    private static final Map<String, List<String>> CATEGORY_SUBJECTS = Map.ofEntries(
            Map.entry("Fiction", List.of("fiction", "novel", "literary fiction")),
            Map.entry("Non-Fiction", List.of("non-fiction", "nonfiction", "reference")),
            Map.entry("Science", List.of("science", "physics", "biology", "chemistry", "astronomy")),
            Map.entry("Technology", List.of("technology", "programming", "computer science", "software")),
            Map.entry("Philosophy", List.of("philosophy", "ethics", "metaphysics", "logic")),
            Map.entry("Psychology", List.of("psychology", "mental health", "cognitive science")),
            Map.entry("Self-Help", List.of("self-help", "personal development", "self-improvement")),
            Map.entry("History", List.of("history", "historical", "world history")),
            Map.entry("Biography", List.of("biography", "autobiography", "memoir")),
            Map.entry("Health", List.of("health", "wellness", "fitness", "nutrition", "medicine")),
            Map.entry("Business", List.of("business", "economics", "finance", "entrepreneurship")),
            Map.entry("Art", List.of("art", "design", "photography", "architecture"))
    );
    
    /**
     * Search for books by title, author, or general query.
     * 
     * @param query Search query (title or author name)
     * @param limit Maximum results to return
     * @return List of matching books with metadata
     */
    public List<Map<String, Object>> searchBooks(String query, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/search.json")
                    .queryParam("q", query)
                    .queryParam("limit", Math.min(limit, 100))
                    .queryParam("fields", "key,title,author_name,author_key,first_publish_year," +
                            "number_of_pages_median,cover_i,subject,isbn,language,publisher")
                    .build()
                    .toUriString();
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode docs = root.get("docs");
            
            if (docs != null && docs.isArray()) {
                for (JsonNode book : docs) {
                    Map<String, Object> bookData = extractBookData(book);
                    results.add(bookData);
                }
            }
        } catch (Exception e) {
            System.err.println("Open Library API error: " + e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Search books by author name.
     */
    public List<Map<String, Object>> searchByAuthor(String authorName, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/search.json")
                    .queryParam("author", authorName)
                    .queryParam("limit", Math.min(limit, 100))
                    .queryParam("fields", "key,title,author_name,author_key,first_publish_year," +
                            "number_of_pages_median,cover_i,subject,isbn")
                    .build()
                    .toUriString();
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode docs = root.get("docs");
            
            if (docs != null && docs.isArray()) {
                for (JsonNode book : docs) {
                    results.add(extractBookData(book));
                }
            }
        } catch (Exception e) {
            System.err.println("Open Library API error: " + e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Search books by subject/category.
     */
    public List<Map<String, Object>> searchBySubject(String subject, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/subjects/" + 
                    subject.toLowerCase().replace(" ", "_") + ".json")
                    .queryParam("limit", Math.min(limit, 100))
                    .build()
                    .toUriString();
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode works = root.get("works");
            
            if (works != null && works.isArray()) {
                for (JsonNode work : works) {
                    results.add(extractWorkData(work));
                }
            }
        } catch (Exception e) {
            System.err.println("Open Library API error: " + e.getMessage());
        }
        
        return results;
    }
    
    /**
     * Get detailed book information by Open Library key.
     * Key format: /works/OL12345W
     */
    public Map<String, Object> getBookDetails(String workKey) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Normalize key format
            if (!workKey.startsWith("/")) {
                workKey = "/works/" + workKey;
            }
            
            String url = baseUrl + workKey + ".json";
            String response = restTemplate.getForObject(url, String.class);
            JsonNode book = objectMapper.readTree(response);
            result = extractDetailedBookData(book);
        } catch (Exception e) {
            System.err.println("Open Library API error: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Get book details by ISBN.
     */
    public Map<String, Object> getBookByIsbn(String isbn) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String url = baseUrl + "/isbn/" + isbn + ".json";
            String response = restTemplate.getForObject(url, String.class);
            JsonNode book = objectMapper.readTree(response);
            result = extractEditionData(book);
        } catch (Exception e) {
            System.err.println("Open Library API error (ISBN): " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Get author information.
     */
    public Map<String, Object> getAuthorDetails(String authorKey) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Normalize key format
            if (!authorKey.startsWith("/")) {
                authorKey = "/authors/" + authorKey;
            }
            
            String url = baseUrl + authorKey + ".json";
            String response = restTemplate.getForObject(url, String.class);
            JsonNode author = objectMapper.readTree(response);
            
            result.put("key", getTextValue(author, "key"));
            result.put("name", getTextValue(author, "name"));
            result.put("birthDate", getTextValue(author, "birth_date"));
            result.put("bio", extractBio(author));
            
            // Get author photo
            if (author.has("photos") && author.get("photos").isArray() && 
                author.get("photos").size() > 0) {
                int photoId = author.get("photos").get(0).asInt();
                result.put("photoUrl", "https://covers.openlibrary.org/a/id/" + photoId + "-M.jpg");
            }
        } catch (Exception e) {
            System.err.println("Open Library API error: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Get cover image URL for a book.
     * 
     * @param coverId The cover ID from search results
     * @param size Size: S (small), M (medium), L (large)
     * @return Cover image URL
     */
    public String getCoverUrl(int coverId, String size) {
        if (coverId <= 0) return null;
        return "https://covers.openlibrary.org/b/id/" + coverId + "-" + size + ".jpg";
    }
    
    /**
     * Get available categories.
     */
    public List<String> getCategories() {
        return new ArrayList<>(CATEGORY_SUBJECTS.keySet());
    }
    
    /**
     * Extract book data from search result.
     */
    private Map<String, Object> extractBookData(JsonNode book) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("key", getTextValue(book, "key"));
        data.put("title", getTextValue(book, "title"));
        data.put("source", "OpenLibrary");
        
        // Extract first author
        if (book.has("author_name") && book.get("author_name").isArray() &&
            book.get("author_name").size() > 0) {
            data.put("author", book.get("author_name").get(0).asText());
            
            // Get all authors
            List<String> authors = new ArrayList<>();
            for (JsonNode author : book.get("author_name")) {
                authors.add(author.asText());
            }
            data.put("authors", authors);
        }
        
        // Author keys for getting more info
        if (book.has("author_key") && book.get("author_key").isArray() &&
            book.get("author_key").size() > 0) {
            data.put("authorKey", book.get("author_key").get(0).asText());
        }
        
        // Publication year
        if (book.has("first_publish_year")) {
            data.put("publishYear", book.get("first_publish_year").asInt());
        }
        
        // Page count (median across editions)
        if (book.has("number_of_pages_median")) {
            data.put("pageCount", book.get("number_of_pages_median").asInt());
        }
        
        // Cover image
        if (book.has("cover_i")) {
            int coverId = book.get("cover_i").asInt();
            data.put("coverId", coverId);
            data.put("coverUrl", getCoverUrl(coverId, "M"));
            data.put("coverUrlSmall", getCoverUrl(coverId, "S"));
            data.put("coverUrlLarge", getCoverUrl(coverId, "L"));
        }
        
        // ISBN
        if (book.has("isbn") && book.get("isbn").isArray() &&
            book.get("isbn").size() > 0) {
            data.put("isbn", book.get("isbn").get(0).asText());
        }
        
        // Subjects/Categories
        if (book.has("subject") && book.get("subject").isArray()) {
            List<String> subjects = new ArrayList<>();
            for (JsonNode subject : book.get("subject")) {
                subjects.add(subject.asText());
                if (subjects.size() >= 5) break; // Limit to 5
            }
            data.put("subjects", subjects);
            data.put("category", categorizeBook(subjects));
        }
        
        // Languages
        if (book.has("language") && book.get("language").isArray()) {
            List<String> languages = new ArrayList<>();
            for (JsonNode lang : book.get("language")) {
                languages.add(lang.asText());
            }
            data.put("languages", languages);
        }
        
        return data;
    }
    
    /**
     * Extract data from subject/works endpoint.
     */
    private Map<String, Object> extractWorkData(JsonNode work) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("key", getTextValue(work, "key"));
        data.put("title", getTextValue(work, "title"));
        data.put("source", "OpenLibrary");
        
        // Authors
        if (work.has("authors") && work.get("authors").isArray()) {
            List<String> authors = new ArrayList<>();
            for (JsonNode author : work.get("authors")) {
                if (author.has("name")) {
                    authors.add(author.get("name").asText());
                }
            }
            if (!authors.isEmpty()) {
                data.put("author", authors.get(0));
                data.put("authors", authors);
            }
        }
        
        // Cover
        if (work.has("cover_id")) {
            int coverId = work.get("cover_id").asInt();
            data.put("coverId", coverId);
            data.put("coverUrl", getCoverUrl(coverId, "M"));
        }
        
        if (work.has("first_publish_year")) {
            data.put("publishYear", work.get("first_publish_year").asInt());
        }
        
        return data;
    }
    
    /**
     * Extract detailed book data.
     */
    private Map<String, Object> extractDetailedBookData(JsonNode book) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("key", getTextValue(book, "key"));
        data.put("title", getTextValue(book, "title"));
        data.put("source", "OpenLibrary");
        
        // Description
        if (book.has("description")) {
            JsonNode desc = book.get("description");
            if (desc.isTextual()) {
                data.put("description", desc.asText());
            } else if (desc.has("value")) {
                data.put("description", desc.get("value").asText());
            }
        }
        
        // Subjects
        if (book.has("subjects") && book.get("subjects").isArray()) {
            List<String> subjects = new ArrayList<>();
            for (JsonNode subject : book.get("subjects")) {
                subjects.add(subject.asText());
            }
            data.put("subjects", subjects);
            data.put("category", categorizeBook(subjects));
        }
        
        // Covers
        if (book.has("covers") && book.get("covers").isArray() &&
            book.get("covers").size() > 0) {
            int coverId = book.get("covers").get(0).asInt();
            data.put("coverId", coverId);
            data.put("coverUrl", getCoverUrl(coverId, "M"));
        }
        
        // Created date
        if (book.has("created") && book.get("created").has("value")) {
            data.put("createdDate", book.get("created").get("value").asText());
        }
        
        return data;
    }
    
    /**
     * Extract edition-specific data (from ISBN lookup).
     */
    private Map<String, Object> extractEditionData(JsonNode book) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("title", getTextValue(book, "title"));
        data.put("source", "OpenLibrary");
        
        if (book.has("number_of_pages")) {
            data.put("pageCount", book.get("number_of_pages").asInt());
        }
        
        if (book.has("publish_date")) {
            data.put("publishDate", book.get("publish_date").asText());
        }
        
        if (book.has("publishers") && book.get("publishers").isArray() &&
            book.get("publishers").size() > 0) {
            data.put("publisher", book.get("publishers").get(0).asText());
        }
        
        // Get work key for more details
        if (book.has("works") && book.get("works").isArray() &&
            book.get("works").size() > 0) {
            data.put("workKey", book.get("works").get(0).get("key").asText());
        }
        
        // Cover
        if (book.has("covers") && book.get("covers").isArray() &&
            book.get("covers").size() > 0) {
            int coverId = book.get("covers").get(0).asInt();
            data.put("coverId", coverId);
            data.put("coverUrl", getCoverUrl(coverId, "M"));
        }
        
        return data;
    }
    
    /**
     * Extract author biography.
     */
    private String extractBio(JsonNode author) {
        if (!author.has("bio")) return "";
        
        JsonNode bio = author.get("bio");
        if (bio.isTextual()) {
            return bio.asText();
        } else if (bio.has("value")) {
            return bio.get("value").asText();
        }
        return "";
    }
    
    /**
     * Categorize book based on subjects.
     */
    private String categorizeBook(List<String> subjects) {
        if (subjects == null || subjects.isEmpty()) return "General";
        
        for (Map.Entry<String, List<String>> entry : CATEGORY_SUBJECTS.entrySet()) {
            for (String subject : subjects) {
                String lowerSubject = subject.toLowerCase();
                for (String keyword : entry.getValue()) {
                    if (lowerSubject.contains(keyword)) {
                        return entry.getKey();
                    }
                }
            }
        }
        
        return "General";
    }
    
    private String getTextValue(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? 
                node.get(field).asText() : "";
    }
}
