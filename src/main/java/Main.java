import searchengine.BooleanSearchEngine;
import server.SearchServer;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        final File pdfDir = new File("pdfs");
        final File stopWordsFile = new File("stop-ru.txt");
        final int serverPort = 8989;

        BooleanSearchEngine searchEngine = new BooleanSearchEngine(pdfDir, stopWordsFile);
        SearchServer searchServer = new SearchServer(serverPort, searchEngine);
        searchServer.start();
    }
}