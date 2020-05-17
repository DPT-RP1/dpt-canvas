package com.sony.dpt.media;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CbzImagePack implements ImagePack {

    private String path;
    private Map<Integer, byte[]> prefetched;

    private Map<Integer, String> pageToFilename;

    private ZipFile cbzFile;
    private int currentPage = 0;
    private int pageCount;

    private CbzImagePack(String path) throws IOException {
        this.path = path;
        this.prefetched = new HashMap<Integer, byte[]>();
        this.cbzFile = new ZipFile(new File(path));
        this.pageToFilename = new HashMap<Integer, String>();
        scanForPages();
    }

    public static ImagePack open(String path) throws IOException {
        return new CbzImagePack(path);
    }

    private void scanForPages() {
        List<String> fileNames = new ArrayList<String>();
        Enumeration<? extends ZipEntry> entries = cbzFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry current = entries.nextElement();
            if (!current.isDirectory()) fileNames.add(current.getName());
        }

        Collections.sort(fileNames, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2); // TODO: revisit if needed
            }
        });

        this.pageCount = 0;
        for (String fileName : fileNames) {
            pageToFilename.put(pageCount, fileName);
            pageCount += 1;
        }
        this.pageCount += 1;
    }

    private void prefetch() {

    }

    public byte[] fetch(int i) throws IOException {
        ZipEntry zipEntry = cbzFile.getEntry(pageToFilename.get(i));
        InputStream zipInputStream = cbzFile.getInputStream(zipEntry);

        byte[] buffer = new byte[(int) zipEntry.getSize()];
        new DataInputStream(zipInputStream).readFully(buffer);

        prefetched.put(i, buffer);
        return buffer;
    }

    @Override
    public byte[] page(int i) throws IOException {
        currentPage = i;
        return fetch(i);
    }


    @Override
    public int pageCount() {
        return pageCount;
    }

    @Override
    public int currentPage() {
        return currentPage;
    }

    @Override
    public void close() throws IOException {
        cbzFile.close();
        prefetched.clear();
        prefetched = null;
    }
}
