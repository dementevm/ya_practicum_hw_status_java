package com.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

public class Bot {
    String url = "https://api.telegram.org/";
    private static final String token = "bot" + Config.get("BOT_TOKEN");
    private static final String chatId = Config.get("CHAT_ID");

    void sendMessage(String message) {
        String sendMessageURL = url + token + "/sendMessage";
        String postData = "{\"chat_id\": \"" + chatId + "\", \"text\": \"" + message + "\"}";
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(sendMessageURL))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(postData))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();
    }

}