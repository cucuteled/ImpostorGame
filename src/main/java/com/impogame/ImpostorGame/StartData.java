package com.impogame.ImpostorGame;

public class StartData {

    private String selectedWordList;
    private String time;

    public StartData(String selectedWordList, String time) {
        this.selectedWordList = selectedWordList;
        this.time = time;
    }

    public String getSelectedWordList() {
        return selectedWordList;
    }

    public String getTime() {
        return time;
    }
}
