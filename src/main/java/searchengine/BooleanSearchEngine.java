package searchengine;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import searchengine.PageEntry;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine implements SearchEngine {
    Map<String, List<PageEntry>> wordByPages = new HashMap<>();
    List<String> stopWords;

    public BooleanSearchEngine(File pdfsDir, File stopWordsFile) throws IOException {
        File[] pdfFiles = pdfsDir.listFiles();
        for (File pdfFile : pdfFiles != null ? pdfFiles : new File[0]) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfFile));
            int countPages = pdfDoc.getNumberOfPages();
            for (int i = 1; i <= countPages; i++) {
                PdfPage pdfPage = pdfDoc.getPage(i);
                String textFromPage = PdfTextExtractor.getTextFromPage(pdfPage);
                String[] words = textFromPage.split("\\P{IsAlphabetic}+");
                Map<String, Integer> freqByWord = new HashMap<>();
                for (String word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqByWord.put(word, freqByWord.getOrDefault(word, 0) + 1);
                }
                for (String word : freqByWord.keySet()) {
                    PageEntry pageEntry = new PageEntry(pdfDoc.getDocumentInfo().getTitle(), i, freqByWord.get(word));
                    if (wordByPages.containsKey(word)) {
                        wordByPages.get(word).add(pageEntry);
                    } else {
                        List<PageEntry> newList = new ArrayList<>();
                        newList.add(pageEntry);
                        wordByPages.put(word, newList);
                    }
                }
            }
        }
        stopWords = addStopWordsFromFile(stopWordsFile);
    }

    private List<String> addStopWordsFromFile(File file) {
        List<String> fileInfo = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            fileInfo = reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileInfo;
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> resultList = wordByPages.get(word.toLowerCase().trim());
        try {
            Collections.sort(resultList);
        } catch (NullPointerException e) {
            return resultList;
        }
        return resultList;

    }

    public List<PageEntry> searchWords(Set<String> words) {
        Map<FilePage, Integer> filePageByCount = new HashMap<>();
        List<PageEntry> resultList = new ArrayList<>();
        for (String word : words) {
            if (words.size() > 1 && stopWords.contains(word)) {
                continue;
            }
            List<PageEntry> listPageEntry = search(word);
            if (listPageEntry == null) {
                break;
            }
            for (PageEntry pageEntry : listPageEntry) {
                FilePage filePage = new FilePage(pageEntry);
                filePageByCount.put(filePage, filePageByCount.getOrDefault(filePage, 0) + pageEntry.getCount());
            }
        }
        for (FilePage filePage : filePageByCount.keySet()) {
            PageEntry pageEntry = new PageEntry(filePage.name, filePage.page, filePageByCount.get(filePage));
            resultList.add(pageEntry);
        }
        try {
            Collections.sort(resultList);
        } catch (NullPointerException e) {
            return resultList;
        }
        return resultList;
    }

    private static class FilePage {
        private final String name;
        private final int page;

        public FilePage(PageEntry pageEntry) {
            this.name = pageEntry.getPdfName();
            this.page = pageEntry.getPage();
        }

        @Override
        public int hashCode() {
            return name.hashCode() + Integer.hashCode(page);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass()) return false;
            FilePage o = (FilePage) obj;
            return name.equals(o.name) && page == o.page;
        }
    }


}