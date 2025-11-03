package com.impogame.ImpostorGame.Controllers;

import com.impogame.ImpostorGame.ImpostorGameApplication;
import com.impogame.ImpostorGame.Jatekos;

import java.util.Arrays;
import java.util.List;

// DATA POJO
public class infoData {
    private int time;
    private List<Jatekos> players;
    private boolean isOnGoing;
    private String szo;
    private List<String> wordCollection;
    private boolean isVote;
    private String message = "";

    public infoData(int time, List<Jatekos> players, boolean isOnGoing, String szo) {
        this.time = time;
        this.players = players;
        this.isOnGoing = isOnGoing;
        this.szo = szo;
        this.wordCollection = ImpostorGameApplication.Jatek.getWordCollection();
        this.isVote = ImpostorGameApplication.Jatek.isVote();

        if (ImpostorGameApplication.Jatek.isMessage()) {
            this.message = ImpostorGameApplication.Jatek.getMessage();
        }

    }

    public String getMessage() {
        return message;
    }

    public boolean isVote() {
        return isVote;
    }

    public List<String> getWordCollection() {
        return wordCollection;
    }

    public String getSzo() {
        return szo;
    }

    public int getTime() {
        return time;
    }

    public List<Jatekos> getPlayers() {
        return players;
    }

    public boolean isOnGoing() {
        return isOnGoing;
    }
}
