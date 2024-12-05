package com.lumina.wrappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina.data.Annonce;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class FakeSiteWrapper implements SiteWrapper {

    private static final String BASE_URL = "http://localhost:8080/fakesite/search";

    @Override
    public List<Annonce> fetchAnnonces(String keyword) throws IOException {

        HttpClient client = HttpClient.newHttpClient();


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)))
                .GET()
                .build();

        try {

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


            ObjectMapper mapper = new ObjectMapper();
            return Arrays.asList(mapper.readValue(response.body(), Annonce[].class));
        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            throw new RuntimeException("Request interrupted", e);
        }
    }
}
