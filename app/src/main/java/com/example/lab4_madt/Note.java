package com.example.lab4_madt;

public class Note {
    private final String name;
    private final String content;

    public Note(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
