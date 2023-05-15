import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class SearchServer {

    private final int SERVER_PORT;

    public SearchServer(int SERVER_PORT) {
        this.SERVER_PORT = SERVER_PORT;
    }

    public void start() throws IOException {
        Gson gson = new Gson();
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Сервер стартовал на порту " + SERVER_PORT + "...");
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    String request = in.readLine();
                    List<PageEntry> listPageEntry = engine.search(request);
                    if (listPageEntry == null) {
                        out.println("Слово " + request + " не найдено");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (PageEntry linePageEntry : listPageEntry) {
                            sb.append(gson.toJson(linePageEntry));
                            sb.append("#");
                        }
                        String answer = sb.toString();
                        out.println(answer);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер :(");
            e.printStackTrace();
        }
    }
}