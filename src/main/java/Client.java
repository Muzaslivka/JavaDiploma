import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int PORT = 8989;
    private static final String HOST = "localHost";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Gson gson = new Gson();
        System.out.println("Поиск по слову в pdf - файлах");
        while (true) {
            try (Socket clientSocket = new Socket(HOST, PORT);
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                System.out.println("Введи слово для поиска:");
                String input = sc.nextLine();
                if (input.equals("end")) {
                    out.println("end");
                    System.out.println("Всего доброго");
                    break;
                } else {
                    out.println(input);
                    String answerServer = in.readLine();
                    System.out.println(answerServer.replace("#", "\n"));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}