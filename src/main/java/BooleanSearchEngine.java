import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine implements SearchEngine {
    Map<String, List<PageEntry>> wordByPages = new HashMap<>();
    public BooleanSearchEngine(File pdfsDir) throws IOException {
        File[] pdfFiles = pdfsDir.listFiles();
        for (File pdfFile : pdfFiles) {
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
        List<PageEntry> list = wordByPages.get(word.toLowerCase());
        try {
            Collections.sort(list);
        } catch (NullPointerException e){
            return list;
        }
        return list;

    }
}