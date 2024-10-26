import static spark.Spark.*;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<String> messages = new ArrayList<>();
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        staticFiles.externalLocation("index.html"); // Укажите путь к вашим HTML файлам

        // Обработка отправки сообщения
        post("/send", (request, response) -> {
            String message = gson.fromJson(request.body(), Message.class).message;
            messages.add(message);
            return "Сообщение получено";
        });

        // Обработка получения сообщений
        get("/messages", (request, response) -> {
            response.type("application/json");
            return gson.toJson(messages);
        });
    }

    // Внутренний класс для десериализации JSON сообщения
    private static class Message {
        String message;
    }
}
