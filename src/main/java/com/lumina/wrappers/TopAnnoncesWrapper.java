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

public class TopAnnoncesWrapper implements SiteWrapper {

    private static final String BASE_URL = "https://api.topannonces.fr/liste";

    @Override
    public List<Annonce> fetchAnnonces(String motsCles) {
        List<Annonce> annonces = new ArrayList<>();

        // Format the search keywords for the query
        String formattedMotsCles = motsCles.trim().replaceAll(" ", "+");

        // Construct the full URL
        String url = BASE_URL + "?q=" + formattedMotsCles + "&nbParPage=12&init=1";

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

            // Parse the JSON response
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode annoncesNode = rootNode.at("/resultats/annonces");

            // Iterate over the listings
            if (annoncesNode.isArray()) {
                for (JsonNode annonceNode : annoncesNode) {
                    if (annonceNode.has("idAnnonce")) { // Check if the ad has a valid ID
                        Annonce annonce = new Annonce();

                        annonce.setId(annonceNode.get("idAnnonce").asInt());

                        // Safely retrieve optional fields
                        annonce.setTitre(getSafeText(annonceNode, "titre"));
                        annonce.setLien("https://www.topannonces.fr" + getSafeText(annonceNode, "url"));
                        annonce.setImage(getSafeText(annonceNode, "urlMiniature"));
                        annonce.setSite("TopAnnonces");
                        annonce.setDateRecuperation("N/A"); // Placeholder for the date

                        annonces.add(annonce);
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return annonces;
    }

    /**
     * Helper method to safely get a text value from a JsonNode.
     * If the field is null or missing, it returns an empty string.
     */
    private String getSafeText(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return (fieldNode != null && !fieldNode.isNull()) ? fieldNode.asText() : "";
    }
}
