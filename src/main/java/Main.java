public class Main {
    public static void main(String[] args) throws Exception {

        //List<String> list = engine.wordByPages.keySet().stream().sorted().collect(Collectors.toList());
        //int i = 0;
        //engine.search("devops").forEach(out::println);
        SearchServer searchServer = new SearchServer(8989);
        searchServer.start();

        // здесь создайте сервер, который отвечал бы на нужные запросы
        // слушать он должен порт 8989