package com.lumina.wrappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina.data.Annonce;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class LeboncoinWrapper implements SiteWrapper {

    private static final String BASE_URL = "https://www.leboncoin.fr/_next/data/k9Cm4O3CD4jOB8ucP60CW/recherche.json";

    @Override
    public List<Annonce> fetchAnnonces(String motsCles) {
        List<Annonce> annonces = new ArrayList<>();

        // Construct the full URL
        String url = BASE_URL + "?text=" + motsCles.replaceAll(" " , "+");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper objectMapper = new ObjectMapper();

            // Parse le JSON
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode adsNode = rootNode.at("/pageProps/searchData/ads");

            // Parcourt les annonces
            if (adsNode.isArray()) {
                for (JsonNode ad : adsNode) {
                    Annonce annonce = new Annonce(rs.getInt("id"), rs.getString("titre"), rs.getString("lien"), rs.getString("image"), rs.getString("site"), rs.getString("date_recuperation"));
                    annonce.setId(ad.get("list_id").asInt());
                    annonce.setTitre(ad.get("subject").asText());
                    annonce.setLien(ad.get("url").asText());
                    annonce.setImage(ad.at("/images/thumb_url").asText());
                    annonce.setSite("Leboncoin");
                    annonce.setDateRecuperation(ad.get("first_publication_date").asText());

                    annonces.add(annonce);
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return annonces;
    }
}
