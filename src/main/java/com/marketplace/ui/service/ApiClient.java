package com.marketplace.ui.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.marketplace.backend.dto.AdminReviewRequest;
import com.marketplace.backend.dto.ApiResponse;
import com.marketplace.backend.dto.AuthResponse;
import com.marketplace.backend.dto.CategoryDto;
import com.marketplace.backend.dto.CityDto;
import com.marketplace.backend.dto.ConversationDto;
import com.marketplace.backend.dto.ListingDto;
import com.marketplace.backend.dto.ListingSearchRequest;
import com.marketplace.backend.dto.LoginRequest;
import com.marketplace.backend.dto.MessageDto;
import com.marketplace.backend.dto.RatingRequest;
import com.marketplace.backend.dto.RegisterRequest;
import com.marketplace.backend.dto.SellerRatingDto;
import com.marketplace.backend.dto.UserDto;
import com.marketplace.ui.session.UserSession;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST API client for the marketplace backend.
 * Handles all HTTP communication, JSON serialization/deserialization,
 * authentication header injection, and multipart file uploads.
 */
@Component
public class ApiClient {

    /** The base URL for all backend API endpoints. */
    public static final String BASE_URL = "http://localhost:8080";

    /** The HTTP client instance configured with connection timeouts. */
    private final HttpClient httpClient;

    /** Jackson object mapper with JSR-310 (Java Time) module support. */
    private final ObjectMapper objectMapper;

    /** The current user session used to retrieve authentication tokens. */
    private final UserSession session;

    /**
     * Constructs a new ApiClient and initializes the HTTP client and object mapper.
     *
     * @param session the user session providing authentication credentials
     */
    public ApiClient(UserSession session) {
        this.session = session;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Performs a health check on the backend service.
     *
     * @return the health status response body
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public String health() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/health"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /**
     * Registers a new user.
     *
     * @param request the registration request payload
     * @return the authentication response containing user details and token
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public AuthResponse register(RegisterRequest request) throws IOException, InterruptedException {
        return postJson("/api/auth/register", request, AuthResponse.class, false);
    }

    /**
     * Authenticates a user and obtains an access token.
     *
     * @param request the login request payload
     * @return the authentication response containing user details and token
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public AuthResponse login(LoginRequest request) throws IOException, InterruptedException {
        return postJson("/api/auth/login", request, AuthResponse.class, false);
    }

    /**
     * Retrieves all active (approved and not sold) listings.
     *
     * @return a list of active listing DTOs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<ListingDto> getActiveListings() throws IOException, InterruptedException {
        return getJson("/api/listings/active", new TypeReference<>() {}, false);
    }

    /**
     * Searches listings based on provided criteria.
     *
     * @param request the search request containing filters and pagination
     * @return a list of listing DTOs matching the search criteria
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<ListingDto> searchListings(ListingSearchRequest request) throws IOException, InterruptedException {
        return postJson("/api/listings/search", request, new TypeReference<>() {}, false);
    }

    /**
     * Retrieves all listings owned by the currently authenticated user.
     *
     * @return a list of the current user's listing DTOs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<ListingDto> getMyListings() throws IOException, InterruptedException {
        return getJson("/api/listings/mine", new TypeReference<>() {}, true);
    }

    /**
     * Retrieves a specific listing by its identifier.
     *
     * @param id the listing ID
     * @return the listing DTO
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public ListingDto getListing(Long id) throws IOException, InterruptedException {
        return getJson("/api/listings/" + id, ListingDto.class, session.isLoggedIn());
    }

    /**
     * Creates a new listing with optional images.
     *
     * @param listing the listing data
     * @param imagePaths a list of image file paths to upload
     * @return the created listing DTO
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public ListingDto createListing(ListingDto listing, List<Path> imagePaths) throws IOException, InterruptedException {
        return sendListingMultipart("POST", "/api/listings", listing, imagePaths);
    }

    /**
     * Updates an existing listing with optional new images.
     *
     * @param id the ID of the listing to update
     * @param listing the updated listing data
     * @param imagePaths a list of new image file paths to attach
     * @return the updated listing DTO
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public ListingDto updateListing(Long id, ListingDto listing, List<Path> imagePaths) throws IOException, InterruptedException {
        return sendListingMultipart("PUT", "/api/listings/" + id, listing, imagePaths);
    }

    /**
     * Deletes a listing by its identifier.
     *
     * @param id the ID of the listing to delete
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public void deleteListing(Long id) throws IOException, InterruptedException {
        delete("/api/listings/" + id);
    }

    /**
     * Marks a specific listing as sold.
     *
     * @param id the ID of the listing to mark as sold
     * @return the updated listing DTO
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public ListingDto markSold(Long id) throws IOException, InterruptedException {
        return putJson("/api/listings/" + id + "/sold", null, ListingDto.class);
    }

    /**
     * Retrieves all available product categories.
     *
     * @return a list of category DTOs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<CategoryDto> getCategories() throws IOException, InterruptedException {
        return getJson("/api/categories", new TypeReference<>() {}, false);
    }

    /**
     * Retrieves all available cities.
     *
     * @return a list of city DTOs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<CityDto> getCities() throws IOException, InterruptedException {
        return getJson("/api/cities", new TypeReference<>() {}, false);
    }

    /**
     * Retrieves all favorite listings of the current user.
     *
     * @return a list of favorite listing DTOs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<ListingDto> getFavorites() throws IOException, InterruptedException {
        return getJson("/api/favorites", new TypeReference<>() {}, true);
    }

    /**
     * Adds a listing to the current user's favorites.
     *
     * @param listingId the ID of the listing to favorite
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public void addFavorite(Long listingId) throws IOException, InterruptedException {
        postJson("/api/favorites/" + listingId, Map.of(), Object.class, true);
    }

    /**
     * Removes a listing from the current user's favorites.
     *
     * @param listingId the ID of the listing to unfavorite
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public void removeFavorite(Long listingId) throws IOException, InterruptedException {
        delete("/api/favorites/" + listingId);
    }

    /**
     * Retrieves all conversations for the current user.
     *
     * @return a list of conversation DTOs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<ConversationDto> getConversations() throws IOException, InterruptedException {
        return getJson("/api/conversations", new TypeReference<>() {}, true);
    }

    /**
     * Retrieves a specific conversation by its identifier.
     *
     * @param id the conversation ID
     * @return the conversation DTO
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public ConversationDto getConversation(Long id) throws IOException, InterruptedException {
        return getJson("/api/conversations/" + id, ConversationDto.class, true);
    }

    /**
     * Starts a new conversation for a specific listing.
     *
     * @param listingId the ID of the listing to start the conversation about
     * @return the created conversation DTO
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public ConversationDto startConversation(Long listingId) throws IOException, InterruptedException {
        return postJson("/api/conversations/start/" + listingId, Map.of(), ConversationDto.class, true);
    }

    /**
     * Sends a message within a specific conversation.
     *
     * @param conversationId the ID of the conversation
     * @param content the message content
     * @return the created message DTO
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public MessageDto sendMessage(Long conversationId, String content) throws IOException, InterruptedException {
        return postJson("/api/conversations/" + conversationId + "/messages",
                Map.of("content", content), MessageDto.class, true);
    }

    /**
     * Submits a rating for a seller.
     *
     * @param request the rating request payload
     * @return the created seller rating DTO
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public SellerRatingDto rateSeller(RatingRequest request) throws IOException, InterruptedException {
        return postJson("/api/ratings", request, SellerRatingDto.class, true);
    }

    /**
     * Retrieves all ratings for a specific seller.
     *
     * @param sellerId the ID of the seller
     * @return a list of seller rating DTOs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<SellerRatingDto> getSellerRatings(Long sellerId) throws IOException, InterruptedException {
        return getJson("/api/ratings/seller/" + sellerId, new TypeReference<>() {}, false);
    }

    /**
     * Retrieves all listings pending administrative review.
     *
     * @return a list of pending listing DTOs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<ListingDto> getPendingListings() throws IOException, InterruptedException {
        return getJson("/api/admin/listings/pending", new TypeReference<>() {}, true);
    }

    /**
     * Reviews a listing as an administrator (approve or reject).
     *
     * @param id the listing ID
     * @param request the admin review request containing the decision and remarks
     * @return the updated listing DTO
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public ListingDto reviewListing(Long id, AdminReviewRequest request) throws IOException, InterruptedException {
        return postJson("/api/admin/listings/" + id + "/review", request, ListingDto.class, true);
    }

    /**
     * Retrieves all registered users (admin only).
     *
     * @return a list of user DTOs
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public List<UserDto> getUsers() throws IOException, InterruptedException {
        return getJson("/api/admin/users", new TypeReference<>() {}, true);
    }

    /**
     * Blocks or unblocks a user (admin only).
     *
     * @param id the user ID
     * @param blocked true to block, false to unblock
     * @return the updated user DTO
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public UserDto blockUser(Long id, boolean blocked) throws IOException, InterruptedException {
        String encoded = URLEncoder.encode(String.valueOf(blocked), StandardCharsets.UTF_8);
        return putJson("/api/admin/users/" + id + "/block?blocked=" + encoded, null, UserDto.class);
    }

    /**
     * Creates a new category (admin only).
     *
     * @param name the category name
     * @param description the category description
     * @return the created category DTO
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public CategoryDto createCategory(String name, String description) throws IOException, InterruptedException {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("description", description);
        return postJson("/api/categories", body, CategoryDto.class, true);
    }

    /**
     * Deletes a category by its identifier (admin only).
     *
     * @param id the category ID
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public void deleteCategory(Long id) throws IOException, InterruptedException {
        delete("/api/categories/" + id);
    }

    /**
     * Extracts a user-friendly error message from an exception.
     * Attempts to parse the response body as an ApiResponse to retrieve the message.
     *
     * @param ex the caught exception
     * @return a clean error message
     */
    public String extractErrorMessage(Exception ex) {
        if (ex.getMessage() != null && ex.getMessage().contains(":")) {
            try {
                String jsonPart = ex.getMessage().substring(ex.getMessage().indexOf('{'));
                ApiResponse response = objectMapper.readValue(jsonPart, ApiResponse.class);
                return response.getMessage();
            } catch (Exception ignored) {
            }
        }
        return ex.getMessage() == null ? "Unexpected error" : ex.getMessage();
    }

    /**
     * Sends a multipart/form-data request for creating or updating a listing with images.
     *
     * @param method the HTTP method (POST or PUT)
     * @param path the request path
     * @param listing the listing DTO
     * @param imagePaths the image files to upload
     * @return the parsed listing DTO response
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    private ListingDto sendListingMultipart(String method, String path, ListingDto listing, List<Path> imagePaths)
            throws IOException, InterruptedException {
        String boundary = "Boundary-" + UUID.randomUUID();
        var byteArrays = new java.io.ByteArrayOutputStream();

        byteArrays.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        byteArrays.write(("Content-Disposition: form-data; name=\"listing\"; filename=\"listing.json\"\r\n").getBytes(StandardCharsets.UTF_8));
        byteArrays.write(("Content-Type: application/json\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        byteArrays.write(objectMapper.writeValueAsBytes(listing));
        byteArrays.write("\r\n".getBytes(StandardCharsets.UTF_8));

        if (imagePaths != null) {
            for (Path imagePath : imagePaths) {
                if (imagePath != null && Files.exists(imagePath)) {
                    byteArrays.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
                    byteArrays.write(("Content-Disposition: form-data; name=\"images\"; filename=\"" +
                            imagePath.getFileName() + "\"\r\n").getBytes(StandardCharsets.UTF_8));
                    byteArrays.write(("Content-Type: application/octet-stream\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                    byteArrays.write(Files.readAllBytes(imagePath));
                    byteArrays.write("\r\n".getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        byteArrays.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(60));
        applyAuth(builder);
        HttpRequest request = builder.method(method, HttpRequest.BodyPublishers.ofByteArray(byteArrays.toByteArray())).build();
        return handleResponse(httpClient.send(request, HttpResponse.BodyHandlers.ofString()), ListingDto.class);
    }

    /**
     * Sends a GET request and deserializes the response to the specified class type.
     *
     * @param path the request path
     * @param type the target response class
     * @param authRequired whether authentication is required
     * @param <T> the response type
     * @return the deserialized response object
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    private <T> T getJson(String path, Class<T> type, boolean authRequired) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .timeout(Duration.ofSeconds(30));
        if (authRequired) {
            applyAuth(builder);
        }
        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, type);
    }

    /**
     * Sends a GET request and deserializes the response to a generic type reference.
     *
     * @param path the request path
     * @param type the target type reference (e.g., for lists)
     * @param authRequired whether authentication is required
     * @param <T> the response type
     * @return the deserialized response object
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    private <T> T getJson(String path, TypeReference<T> type, boolean authRequired) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET()
                .timeout(Duration.ofSeconds(30));
        if (authRequired) {
            applyAuth(builder);
        }
        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, type);
    }

    /**
     * Sends a POST request with a JSON body and deserializes the response to the specified class.
     *
     * @param path the request path
     * @param body the request body object (will be serialized to JSON)
     * @param type the target response class
     * @param authRequired whether authentication is required
     * @param <T> the response type
     * @return the deserialized response object
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    private <T> T postJson(String path, Object body, Class<T> type, boolean authRequired) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body == null ? "{}" : objectMapper.writeValueAsString(body)));
        if (authRequired) {
            applyAuth(builder);
        }
        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, type);
    }

    /**
     * Sends a POST request with a JSON body and deserializes the response to a generic type reference.
     *
     * @param path the request path
     * @param body the request body object (will be serialized to JSON)
     * @param type the target type reference
     * @param authRequired whether authentication is required
     * @param <T> the response type
     * @return the deserialized response object
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    private <T> T postJson(String path, Object body, TypeReference<T> type, boolean authRequired) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body == null ? "{}" : objectMapper.writeValueAsString(body)));
        if (authRequired) {
            applyAuth(builder);
        }
        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, type);
    }

    /**
     * Sends a PUT request with a JSON body and deserializes the response.
     *
     * @param path the request path
     * @param body the request body object (will be serialized to JSON)
     * @param type the target response class
     * @param <T> the response type
     * @return the deserialized response object
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    private <T> T putJson(String path, Object body, Class<T> type) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .PUT(HttpRequest.BodyPublishers.ofString(body == null ? "{}" : objectMapper.writeValueAsString(body)));
        applyAuth(builder);
        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        return handleResponse(response, type);
    }

    /**
     * Sends a DELETE request.
     *
     * @param path the request path
     * @throws IOException if an I/O error occurs or the response status is 4xx/5xx
     * @throws InterruptedException if the operation is interrupted
     */
    private void delete(String path) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(30))
                .DELETE();
        applyAuth(builder);
        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IOException(response.statusCode() + ": " + response.body());
        }
    }

    /**
     * Applies the Bearer token authentication header to the given request builder.
     *
     * @param builder the HTTP request builder
     * @throws IllegalStateException if the user is not logged in
     */
    private void applyAuth(HttpRequest.Builder builder) {
        if (!session.isLoggedIn()) {
            throw new IllegalStateException("Authentication required");
        }
        builder.header("Authorization", "Bearer " + session.getToken());
    }

    /**
     * Handles an HTTP response and deserializes it to the specified class type.
     *
     * @param response the HTTP response
     * @param type the target response class
     * @param <T> the response type
     * @return the deserialized response object, or null for empty responses
     * @throws IOException if the response status is 4xx/5xx or JSON parsing fails
     */
    private <T> T handleResponse(HttpResponse<String> response, Class<T> type) throws IOException {
        if (response.statusCode() >= 400) {
            throw new IOException(response.statusCode() + ": " + response.body());
        }
        if (type == Object.class || response.body() == null || response.body().isBlank()) {
            return null;
        }
        return objectMapper.readValue(response.body(), type);
    }

    /**
     * Handles an HTTP response and deserializes it to a generic type reference.
     *
     * @param response the HTTP response
     * @param type the target type reference
     * @param <T> the response type
     * @return the deserialized response object
     * @throws IOException if the response status is 4xx/5xx or JSON parsing fails
     */
    private <T> T handleResponse(HttpResponse<String> response, TypeReference<T> type) throws IOException {
        if (response.statusCode() >= 400) {
            throw new IOException(response.statusCode() + ": " + response.body());
        }
        return objectMapper.readValue(response.body(), type);
    }
}