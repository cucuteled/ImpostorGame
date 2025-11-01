package com.impogame.ImpostorGame;

public class Jatekos {
    private String nev;
    private int role; // 1=impostor 0=player
    private boolean isAlive;

    private boolean hasVoted;

    public Jatekos(String nev) {
        this.nev = nev;
        this.role = 0;
        this.isAlive = true;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }
}
