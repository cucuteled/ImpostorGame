package com.impogame.ImpostorGame;

public class StartData {

    private String selectedWordList;
    private String time;

    private boolean isMultipleImpostor;

    public StartData(String selectedWordList, String time, boolean isMultipleImpostor) {
        this.selectedWordList = selectedWordList;
        this.time = time;
        this.isMultipleImpostor = isMultipleImpostor;
    }

    public String getSelectedWordList() {
        return selectedWordList;
    }

    public String getTime() {
        return time;
    }

    public boolean isMultipleImpostor() {
        return isMultipleImpostor;
    }
}
