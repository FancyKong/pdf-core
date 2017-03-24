package com.cafa.pdf.core.demo;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;
import org.apache.tika.Tika;

import java.io.*;
import java.util.*;

public class PDFTest {
    static PdfReader reader;
    public static void main(String[] args) throws Exception {
        /*Tika tika = new Tika();
        System.out.println(tika.parseToString(new File("D:s.doc")));*/
        List<Chapter> pageNumber = new ArrayList<>();
        reader = new PdfReader("D:b.pdf");
        List list = SimpleBookmark.getBookmark(reader);
        for (Object aList : list) {
            getPageNumbers((Map) aList, pageNumber);
        }
        System.out.println(pageNumber);
        for (int i = 0; i < pageNumber.size() - 1; i++) {
            String store = "D:b" + pageNumber.get(i).title.trim() + ".pdf";
            String trim = pageNumber.get(i).title.trim();
            System.out.println(trim);
            System.out.println(pageNumber.get(i).title.length());
            System.out.println(trim.length());
            split(pageNumber.get(i).beginPage, pageNumber.get(i + 1).beginPage, new FileOutputStream(store), new FileInputStream("D:b.pdf"));
        }
        int end = reader.getNumberOfPages();
        split(pageNumber.get(pageNumber.size() - 1).beginPage, end + 1, new FileOutputStream("D:b_last.pdf"), new FileInputStream("D:b.pdf"));
    }


    private static void split(int begin, int end, OutputStream os, InputStream is) throws DocumentException, IOException {
        Document document = new Document(reader.getPageSize(1));
        PdfCopy copy = new PdfCopy(document, os);
        document.open();
        for (int j = begin; j < end; j++) {
            document.newPage();
            PdfImportedPage page = copy.getImportedPage(reader, j);
            copy.addPage(page);
        }
        document.close();
    }

    private static void showBookmark(Map bookmark) throws IOException {
        System.out.println(bookmark.get("Title") + "-" + bookmark.get("Page"));
        ArrayList kids = (ArrayList) bookmark.get("Kids");

        if (kids == null)
            return;
        for (Object kid : kids) {
            showBookmark((Map) kid);
        }

    }

    public static void getPageNumbers(Map<String, Object> bookmark, List<Chapter> pageNumber) throws IOException {
        if (bookmark == null)
            throw new NullPointerException("书签为空");
        int pageNum = 0;
        String string = "";
        if ("GoTo".equals(bookmark.get("Action"))) {
            String page = (String) bookmark.get("Page");
            if (page != null) {
                page = page.trim();
                int idx = page.indexOf(' ');
                string = (String) bookmark.get("Title");
                if (idx < 0) {
                    pageNum = Integer.parseInt(page);
                } else {
                    pageNum = Integer.parseInt(page.substring(0, idx));
                }
            }
            if (pageNum < 1) throw new IllegalArgumentException("参数错误");
            pageNumber.add(new Chapter(pageNum, string));
        }
    }

    static class Chapter {
        int beginPage;
        String title;

        public Chapter(int beginPage, String title) {
            this.beginPage = beginPage;
            this.title = title;
        }

        @Override
        public String toString() {
            return "Chapter{" +
                    "beginPage=" + beginPage +
                    ", title='" + title + '\'' +
                    '}';
        }
    }
}