public class Main {

    public static void main(String[] args) throws Exception {
        SearchServer searchServer = new SearchServer(8989);
        searchServer.start();
    }
}