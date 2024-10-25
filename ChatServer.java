import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static int clientCount = 0; // Переменная для отслеживания количества клиентов

    public static void main(String[] args) {
        System.out.println("Сервер запущен...");
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            // Запускаем поток для чтения сообщений с консоли
            new Thread(() -> {
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                String serverMessage;
                try {
                    while ((serverMessage = consoleReader.readLine()) != null) {
                        sendToAllClients("Сервер: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                OutputStream outputStream = socket.getOutputStream();
                out = new PrintWriter(outputStream, true);

                synchronized (clientWriters) {
                    clientWriters.add(out);
                    clientCount++; // Увеличиваем количество подключенных клиентов
                    System.out.println("Пользователь подключился. Всего подключено: " + clientCount);
                }

                // Ожидание, когда клиент отключится
                while (!socket.isClosed()) {
                    // Здесь можно оставить пустое тело, поскольку мы не ожидаем ввода от клиента
                    Thread.sleep(100); // добавляем задержку чтобы не перегружать процессор
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                    clientCount--; // Уменьшаем количество подключенных клиентов
                    System.out.println("Пользователь отключился. Всего подключено: " + clientCount);
                }
            }
        }
    }

    private static void sendToAllClients(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}
