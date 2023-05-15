private final int page;
private final int count;

public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
        }

@Override
public int compareTo(PageEntry o) {
        return 0;
        }

@Override
public String toString() {
        return "Файл: " + pdfName + ", страница: " + page + ", Кол: " + count;
        }
        }