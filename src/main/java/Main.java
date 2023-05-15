import java.io.File;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        //List<String> list = engine.wordByPages.keySet().stream().sorted().collect(Collectors.toList());
        //int i = 0;
        engine.search("devops").forEach(out::println);

        // здесь создайте сервер, который отвечал бы на нужные запросы
        // слушать он должен порт 8989