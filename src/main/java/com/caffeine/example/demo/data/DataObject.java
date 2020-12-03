package com.caffeine.example.demo.data;

// Dados que serão armazenados em cache;
public class DataObject {
    private final String data;

    private static int objectCounter = 0;

    // Construtores padrões e getter
    public DataObject(String data) {
        this.data = data;
    }

    public static DataObject get(String data) {
        objectCounter++;
        return new DataObject(data);
    }

    public String getData() {
        return data;
    }
}