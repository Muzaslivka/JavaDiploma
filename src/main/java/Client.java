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
        System.out.println("Поиск по словам в PDF - файлах");
        while (true) {
            try (Socket clientSocket = new Socket(HOST, PORT);
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                System.out.println("Введи слова для поиска или end для завершения работы:");
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
                System.out.println("Не могу подключиться к серверу");
                System.out.println(e.getMessage());
                break;
            }
        }
    }
}