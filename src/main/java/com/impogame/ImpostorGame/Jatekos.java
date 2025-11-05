package com.impogame.ImpostorGame;

public class Jatekos implements Comparable<Jatekos> {
    private String nev;
    private int role; // 1=impostor 0=player
    private boolean isAlive;

    private boolean hasVoted;
    private int votes;

    // Stats:
    private int allTimeVote = 0;
    private int goodVote = 0;

    private int allTimeGame = 0;
    private int winAsImpostor = 0;
    private int winAsPlayer = 0;

    public Jatekos(String nev) {
        this.nev = nev;
        this.role = 0;
        this.isAlive = true;
    }


    // Sorting types
    //byall -> azaz a 3 statisztika összeadva és kinek magasabb pl: winrate as player60% asimpostor 20% goodguessvote: 30% -> 110%
    //byimpostor -> imposztor beli győzelmei alapján %
    //byplayer -> játékosbeli győzelmei alapján %
    //byvotes -> csak a szavazatok aránya alapján %

    @Override
    public int compareTo(Jatekos e) {
        String type = ImpostorGameApplication.Jatek.getSortType();

        // ha nincs adat, 0-át adunk vissza
        double thisWinrateAsPlayer = allTimeGame > 0 ? (double) winAsPlayer / allTimeGame * 100.0 : 0.0;
        double thisWinrateAsImpostor = allTimeGame > 0 ? (double) winAsImpostor / allTimeGame * 100.0 : 0.0;
        double thisGoodVoteRate = allTimeVote > 0 ? (double) goodVote / allTimeVote * 100.0 : 0.0;

        double otherWinrateAsPlayer = e.allTimeGame > 0 ? (double) e.winAsPlayer / e.allTimeGame * 100.0 : 0.0;
        double otherWinrateAsImpostor = e.allTimeGame > 0 ? (double) e.winAsImpostor / e.allTimeGame * 100.0 : 0.0;
        double otherGoodVoteRate = e.allTimeVote > 0 ? (double) e.goodVote / e.allTimeVote * 100.0 : 0.0;

        switch (type) {
            case "byall":
                double total = thisWinrateAsPlayer + thisWinrateAsImpostor + thisGoodVoteRate;
                double otherTotal = otherWinrateAsPlayer + otherWinrateAsImpostor + otherGoodVoteRate;
                return Double.compare(otherTotal, total);

            case "byimpostor":
                return Double.compare(otherWinrateAsImpostor, thisWinrateAsImpostor);

            case "byplayer":
                return Double.compare(otherWinrateAsPlayer, thisWinrateAsPlayer);

            case "byvotes":
                return Double.compare(otherGoodVoteRate, thisGoodVoteRate);

            default:
                return this.nev.compareToIgnoreCase(e.nev);
        }
    }

    // INCREMENTATIONS:

    public void incAllTimeVote() {
        allTimeVote++;
    }

    public void incGoodVote() {
        goodVote++;
    }

    public void incAllTimeGame() {
        allTimeGame++;
    }

    public void incWinAsImpostor() {
        winAsImpostor++;
    }

    public void incWinAsPlayer() {
        winAsPlayer++;
    }

    // END INCS

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getAllTimeVote() {
        return allTimeVote;
    }

    public void setAllTimeVote(int allTimeVote) {
        this.allTimeVote = allTimeVote;
    }

    public int getGoodVote() {
        return goodVote;
    }

    public void setGoodVote(int goodVote) {
        this.goodVote = goodVote;
    }

    public int getAllTimeGame() {
        return allTimeGame;
    }

    public void setAllTimeGame(int allTimeGame) {
        this.allTimeGame = allTimeGame;
    }

    public int getWinAsImpostor() {
        return winAsImpostor;
    }

    public void setWinAsImpostor(int winAsImpostor) {
        this.winAsImpostor = winAsImpostor;
    }

    public int getWinAsPlayer() {
        return winAsPlayer;
    }

    public void setWinAsPlayer(int winAsPlayer) {
        this.winAsPlayer = winAsPlayer;
    }

    public void incVotes() {
        this.votes += 1;
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
