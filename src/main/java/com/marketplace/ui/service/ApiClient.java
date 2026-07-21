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

@Component
public class ApiClient {

    public static final String BASE_URL = "http://localhost:8080";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final UserSession session;

    public ApiClient(UserSession session) {
        this.session = session;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public String health() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/health"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public AuthResponse register(RegisterRequest request) throws IOException, InterruptedException {
        return postJson("/api/auth/register", request, AuthResponse.class, false);
    }

    public AuthResponse login(LoginRequest request) throws IOException, InterruptedException {
        return postJson("/api/auth/login", request, AuthResponse.class, false);
    }

    public List<ListingDto> getActiveListings() throws IOException, InterruptedException {
        return getJson("/api/listings/active", new TypeReference<>() {}, false);
    }

    public List<ListingDto> searchListings(ListingSearchRequest request) throws IOException, InterruptedException {
        return postJson("/api/listings/search", request, new TypeReference<>() {}, false);
    }

    public List<ListingDto> getMyListings() throws IOException, InterruptedException {
        return getJson("/api/listings/mine", new TypeReference<>() {}, true);
    }

    public ListingDto getListing(Long id) throws IOException, InterruptedException {
        return getJson("/api/listings/" + id, ListingDto.class, session.isLoggedIn());
    }

    public ListingDto createListing(ListingDto listing, List<Path> imagePaths) throws IOException, InterruptedException {
        return sendListingMultipart("POST", "/api/listings", listing, imagePaths);
    }

    public ListingDto updateListing(Long id, ListingDto listing, List<Path> imagePaths) throws IOException, InterruptedException {
        return sendListingMultipart("PUT", "/api/listings/" + id, listing, imagePaths);
    }

    public void deleteListing(Long id) throws IOException, InterruptedException {
        delete("/api/listings/" + id);
    }

    public ListingDto markSold(Long id) throws IOException, InterruptedException {
        return putJson("/api/listings/" + id + "/sold", null, ListingDto.class);
    }

    public List<CategoryDto> getCategories() throws IOException, InterruptedException {
        return getJson("/api/categories", new TypeReference<>() {}, false);
    }

    public List<CityDto> getCities() throws IOException, InterruptedException {
        return getJson("/api/cities", new TypeReference<>() {}, false);
    }

    public List<ListingDto> getFavorites() throws IOException, InterruptedException {
        return getJson("/api/favorites", new TypeReference<>() {}, true);
    }

    public void addFavorite(Long listingId) throws IOException, InterruptedException {
        postJson("/api/favorites/" + listingId, Map.of(), Object.class, true);
    }

    public void removeFavorite(Long listingId) throws IOException, InterruptedException {
        delete("/api/favorites/" + listingId);
    }

    public List<ConversationDto> getConversations() throws IOException, InterruptedException {
        return getJson("/api/conversations", new TypeReference<>() {}, true);
    }

    public ConversationDto getConversation(Long id) throws IOException, InterruptedException {
        return getJson("/api/conversations/" + id, ConversationDto.class, true);
    }

    public ConversationDto startConversation(Long listingId) throws IOException, InterruptedException {
        return postJson("/api/conversations/start/" + listingId, Map.of(), ConversationDto.class, true);
    }

    public MessageDto sendMessage(Long conversationId, String content) throws IOException, InterruptedException {
        return postJson("/api/conversations/" + conversationId + "/messages",
                Map.of("content", content), MessageDto.class, true);
    }

    public SellerRatingDto rateSeller(RatingRequest request) throws IOException, InterruptedException {
        return postJson("/api/ratings", request, SellerRatingDto.class, true);
    }

    public List<SellerRatingDto> getSellerRatings(Long sellerId) throws IOException, InterruptedException {
        return getJson("/api/ratings/seller/" + sellerId, new TypeReference<>() {}, false);
    }

    public List<ListingDto> getPendingListings() throws IOException, InterruptedException {
        return getJson("/api/admin/listings/pending", new TypeReference<>() {}, true);
    }

    public ListingDto reviewListing(Long id, AdminReviewRequest request) throws IOException, InterruptedException {
        return postJson("/api/admin/listings/" + id + "/review", request, ListingDto.class, true);
    }

    public List<UserDto> getUsers() throws IOException, InterruptedException {
        return getJson("/api/admin/users", new TypeReference<>() {}, true);
    }

    public UserDto blockUser(Long id, boolean blocked) throws IOException, InterruptedException {
        String encoded = URLEncoder.encode(String.valueOf(blocked), StandardCharsets.UTF_8);
        return putJson("/api/admin/users/" + id + "/block?blocked=" + encoded, null, UserDto.class);
    }

    public CategoryDto createCategory(String name, String description) throws IOException, InterruptedException {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("description", description);
        return postJson("/api/categories", body, CategoryDto.class, true);
    }

    public void deleteCategory(Long id) throws IOException, InterruptedException {
        delete("/api/categories/" + id);
    }

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

    private void applyAuth(HttpRequest.Builder builder) {
        if (!session.isLoggedIn()) {
            throw new IllegalStateException("Authentication required");
        }
        builder.header("Authorization", "Bearer " + session.getToken());
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> type) throws IOException {
        if (response.statusCode() >= 400) {
            throw new IOException(response.statusCode() + ": " + response.body());
        }
        if (type == Object.class || response.body() == null || response.body().isBlank()) {
            return null;
        }
        return objectMapper.readValue(response.body(), type);
    }

    private <T> T handleResponse(HttpResponse<String> response, TypeReference<T> type) throws IOException {
        if (response.statusCode() >= 400) {
            throw new IOException(response.statusCode() + ": " + response.body());
        }
        return objectMapper.readValue(response.body(), type);
    }
}
