package com.example.demo.storage;


import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class PdfStorage {
    private final ConcurrentHashMap<String, byte[]> inMemoryStorage =  new ConcurrentHashMap<>();
    public void saveFile(String fileName, byte[] content, String checksum) {
            inMemoryStorage.putIfAbsent(fileName + "-" + checksum, content);
        System.out.println("File saved: " + fileName + " " + checksum);
    }

    public byte[] getPdfByNameAndChecksum(String name, String checksum) {
        return inMemoryStorage.get(name + "-" + checksum);
    }

}
