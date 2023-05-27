package server;

import com.google.gson.Gson;
import searchengine.BooleanSearchEngine;
import searchengine.PageEntry;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchServer {

    protected int SERVER_PORT;
    protected BooleanSearchEngine booleanSearchEngine;

    public SearchServer(int SERVER_PORT, BooleanSearchEngine booleanSearchEngine) {
        this.SERVER_PORT = SERVER_PORT;
        this.booleanSearchEngine = booleanSearchEngine;
    }

    public void start() {
        Gson gson = new Gson();
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Сервер стартовал на порту " + SERVER_PORT + "...");
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    String requestClient = in.readLine();
                    String[] wordsFromRequest = requestClient.split("\\P{IsAlphabetic}+");
                    Set<String> uniqWordsFromRequest = new HashSet<>(Arrays.asList(wordsFromRequest));
                    List<PageEntry> resultList = booleanSearchEngine.searchWords(uniqWordsFromRequest);
                    if (resultList == null || resultList.size() == 0) {
                        out.println("Слова \"" + requestClient + "\" не найдены");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (PageEntry pageEntry : resultList) {
                            sb.append(pageEntry.getPdfName()).append("\n");
                        }
                        out.println(gson.toJson(resultList));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер :(");
            e.printStackTrace();
        }
    }
}