package task3;

import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OpenTasksFetcher {
    private int userId;         // Задаємо ідентифікатор користувача X
    private String apiUrl;      // Формуємо URL для отримання задач користувача

    public OpenTasksFetcher(String apiUrl) { //приймає з якого apiUrl зчитувати
        this.apiUrl = apiUrl;
    }

    public OpenTasksFetcher() throws Exception {     //Дефолт конструктор
        userId = 1;
        apiUrl = "https://jsonplaceholder.typicode.com/users/" + userId + "/todos";
    }

    public void read() {
        try {
            // Викликаємо метод для виведення відкритих задач
            fetchAndPrintOpenTasks(apiUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fetchAndPrintOpenTasks(String apiUrl) throws Exception {
        // Створюємо об'єкт URL і встановлюємо з'єднання
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Встановлюємо метод запиту
        connection.setRequestMethod("GET");

        // Отримуємо відповідь від сервера
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Створюємо читач для зчитування відповіді
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            // Зчитуємо відповідь рядок за рядком
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // Обробляємо отриману відповідь у форматі JSON та виводимо відкриті задачі
            processJsonResponse(response.toString());
        } else {
            System.out.println("Error: Unable to fetch tasks. Status code: " + responseCode);
        }
        // Закриваємо з'єднання
        connection.disconnect();
    }

    private static void processJsonResponse(String jsonResponse) {
        // Обробка відповіді у форматі JSON та виведення відкритих задач. Використав бібліотеку Gson для обробки JSON
        Gson gson = new Gson();

        // Перетворюємо JSON-строку в масив об'єкта задач (Task)
        Task[] tasks = gson.fromJson(jsonResponse, Task[].class);

        // Виводимо відкриті задачі (completed = false)
        for (Task task : tasks) {
            if (!task.isCompleted()) {
                System.out.println("Task ID: " + task.getId() + ", Title: " + task.getTitle());
            }
        }
    }
}