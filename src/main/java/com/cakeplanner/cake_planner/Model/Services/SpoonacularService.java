package com.cakeplanner.cake_planner.Model.Services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.List;

@Service
public class SpoonacularService {

    @Value("${spoonacular.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String parseIngredients(List<String> ingredientList) {
        String url = "https://api.spoonacular.com/recipes/parseIngredients";

        String joined = String.join("\n", ingredientList);  // Body content

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // required by this endpoint

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("ingredientList", joined);
        body.add("servings", "1");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("apiKey", apiKey); // just the key here

        ResponseEntity<String> response = restTemplate.postForEntity(
                builder.toUriString(),
                request,
                String.class
        );

        return response.getBody();
    }

    public double getConversionRate(String ingredientName, String sourceUnit, String targetUnit) {
        String url = "https://api.spoonacular.com/recipes/convert";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("ingredientName", ingredientName)
                .queryParam("sourceAmount", 1)  // always 1 to get the rate
                .queryParam("sourceUnit", sourceUnit)
                .queryParam("targetUnit", targetUnit)
                .queryParam("apiKey", apiKey);

        ResponseEntity<String> response = restTemplate.getForEntity(
                builder.toUriString(),
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JSONObject result = new JSONObject(response.getBody());
            return result.optDouble("targetAmount", -1);  // -1 fallback
        } else {
            throw new RuntimeException("Spoonacular API error: " + response.getStatusCode());
        }
    }
}
