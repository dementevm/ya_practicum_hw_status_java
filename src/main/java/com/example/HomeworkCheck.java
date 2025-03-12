package com.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.google.gson.Gson;

public class HomeworkCheck {
    private static String lastStatus = "";
    private static final String STATUS_REVIEWING = "reviewing";
    private static final String STATUS_APPROVED = "approved";
    private static final String STATUS_REJECTED = "rejected";
    private static final String API_URL = "https://practicum.yandex.ru/api/user_api/homework_statuses/";
    private static final String PRACTICUM_TOKEN = Config.get("PRACTICUM_TOKEN");

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Bot bot = new Bot();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        bot.sendMessage("Бот начал работу.");
        mainLoop();
    }

    private static void mainLoop() {
        long currentTimestamp = System.currentTimeMillis() / 1000;
        while (true) {
            System.out.println(currentTimestamp);
            try {
                HomeworkResponse response = getHomeworkStatuses(currentTimestamp);
                if (response != null && response.homeworks != null && !response.homeworks.isEmpty()) {
                    String message = parseHomeworkStatus(response.homeworks.get(0));
                    if (message != null) {
                        bot.sendMessage(message);
                    }
                }
                currentTimestamp = response != null ? response.currentDate : currentTimestamp;
                Thread.sleep(300_000);
            } catch (Exception e) {
                System.out.println("Бот упал с ошибкой: " + e.getMessage());
                try {
                    Thread.sleep(5_000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }

    private static String parseHomeworkStatus(Homework hw) {
        if (hw.status == null) {
            try {
                Thread.sleep(300_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        switch (hw.status) {
            case STATUS_REVIEWING:
                if (lastStatus.equals(STATUS_REVIEWING)) {
                    try {
                        Thread.sleep(300_000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                } else {
                    lastStatus = STATUS_REVIEWING;
                    return "Ревьюер начал проверять работу: " + hw.homeworkName;
                }
            case STATUS_REJECTED:
                lastStatus = "";
                return "У вас проверили работу:" + hw.homeworkName + "!\nК сожалению, в работе нашлись ошибки.\nКомментарий: " + hw.reviewerComment;
            case STATUS_APPROVED:
                lastStatus = "";
                return "У вас проверили работу:" + hw.homeworkName + "!\nРевьюеру всё понравилось, работа принята.\nКомментарий: " + hw.reviewerComment;
            default:
                lastStatus = "";
                return "Получен статус " + hw.status + "для работы: " + hw.homeworkName;
        }
    }

    private static HomeworkResponse getHomeworkStatuses(long currentTimestamp) {
        try {
            String urlWithParams = API_URL + "?from_date=" + currentTimestamp;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlWithParams))
                    .timeout(Duration.ofSeconds(10))
                    .header("Authorization", "OAuth " + PRACTICUM_TOKEN)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(response.body(), HomeworkResponse.class);
        } catch (Exception e) {
            bot.sendMessage("Проблемы с запросом. Ошибка: " + e.getMessage());
            try {
                Thread.sleep(300_000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            return null;
        }
    }
}
