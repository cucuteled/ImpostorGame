package com.impogame.ImpostorGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordCollection {

    private String listName;
    private List<String> words;
    private List<String> OriginalWords;

    private final Random rnd = new Random();

    public WordCollection(String listName, List<String> words) {
        this.listName = listName;
        this.words = words;
        this.OriginalWords = words;
    }
    
    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    private void removeWord(String word) {
        words.remove(word);
        if (words == null || words.isEmpty()) words = new ArrayList<>(OriginalWords);
    }

    public String getRandomWord() {
        String returnText = words.get(rnd.nextInt(words.size()));
        removeWord(returnText);
        return returnText;
    }

    @Override
    public String toString() {
        return listName;
    }
}
