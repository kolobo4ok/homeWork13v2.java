package task2;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class CommentsFetcher {
    public CommentsFetcher() {
    }

    public void starting() {
        // Задаємо id користувача
        int userId = 1;

        try {
            // Отримуємо останній пост користувача
            Post lastPost = fetchLastPost(userId);

            if (lastPost != null) {
                // Отримуємо коментарі до останнього поста
                Comment[] comments = fetchCommentsForPost(lastPost.getId());

                // Записуємо коментарі у файл
                writeCommentsToFile(userId, lastPost.getId(), comments);
            } else {
                System.out.println("Користувач не має постів.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Post fetchLastPost(int userId) throws Exception {
        // Отримуємо всі пости користувача
        Post[] posts = fetchUserPosts(userId);

        // Знаходимо останній пост за id
        if (posts.length > 0) {
            return posts[posts.length - 1];
        } else {
            return null;
        }
    }

    private static Post[] fetchUserPosts(int userId) throws Exception {
        String apiUrl = "https://jsonplaceholder.typicode.com/users/" + userId + "/posts";
        HttpURLConnection connection = createConnection(apiUrl);

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String response = readResponse(connection);
            Gson gson = new Gson();
            return gson.fromJson(response, Post[].class);
        } else {
            throw new RuntimeException("Не вдалося отримати пости користувача. Статус код: " + responseCode);
        }
    }

    private static Comment[] fetchCommentsForPost(int postId) throws Exception {
        String apiUrl = "https://jsonplaceholder.typicode.com/posts/" + postId + "/comments";
        HttpURLConnection connection = createConnection(apiUrl);

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String response = readResponse(connection);
            Gson gson = new Gson();
            return gson.fromJson(response, Comment[].class);
        } else {
            throw new RuntimeException("Не вдалося отримати коментарі. Статус код: " + responseCode);
        }
    }

    private static void writeCommentsToFile(int userId, int postId, Comment[] comments) throws IOException {
        String fileName = "src/task2/user-" + userId + "-post-" + postId + "-comments.json";
        Path filePath = Path.of(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            Gson gson = new Gson();

            JsonArray jsonArray = new JsonArray();

            for (Comment comment : comments) {
                jsonArray.add(gson.toJsonTree(comment));
                writer.newLine();
            }

            writer.write(jsonArray.toString());
        }

        System.out.println("Коментарі записані у файл: " + fileName);
    }

    private static HttpURLConnection createConnection(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    private static String readResponse(HttpURLConnection connection) throws Exception {
        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }
}