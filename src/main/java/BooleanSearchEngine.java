import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    Map<String, List<PageEntry>> wordByPages = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        File[] pdfFiles = pdfsDir.listFiles();
        for (File pdfFile : pdfFiles != null ? pdfFiles : new File[0]) {
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfFile));
            int countPages = pdfDoc.getNumberOfPages();
            for (int i = 1; i <= countPages; i++) {
                PdfPage pdfPage = pdfDoc.getPage(i);
                String text = PdfTextExtractor.getTextFromPage(pdfPage);
                String[] words = text.split("\\P{IsAlphabetic}+");
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
                        List<PageEntry> list = new ArrayList<>();
                        list.add(pageEntry);
                        wordByPages.put(word, list);
                    }
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> list = wordByPages.get(word.toLowerCase().trim());
        try {
            Collections.sort(list);
        } catch (NullPointerException e) {
            return list;
        }
        return list;

    }

    public List<PageEntry> searchWords(String[] words) {
        Map<FilePage, Integer> filePageByCount = new HashMap<>();
        List<PageEntry> resultList = new ArrayList<>();
        for (String word : words) {
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