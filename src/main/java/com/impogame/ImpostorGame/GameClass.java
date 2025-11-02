package com.impogame.ImpostorGame;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameClass {

    private int time = 0;
    private List<Jatekos> jatekosok;
    private boolean isOnGoing;
    private String szo;

    private String message = "";

    private boolean isVote;

    public static List<WordCollection> wordLista = new ArrayList<>();

    public GameClass() {
        this.jatekosok = new ArrayList<>();
        this.szo = "";
        this.isOnGoing = false;
        try { ReadFiles(); } catch (Exception e) {}
        this.isVote = false;
    }


    private void ReadFiles() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            // minden TXT fájl a static/words mappában
            Resource[] resources = resolver.getResources("classpath:static/words/*.txt");

            for (Resource res : resources) {
                String filename = res.getFilename(); // pl. Sport.txt
                List<String> words = new ArrayList<>();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(res.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        words.add(line);
                    }
                }

                wordLista.add(new WordCollection(filename.replace(".txt", ""), words));
            }

            System.out.println("wordLists loaded: " + wordLista.stream().map(WordCollection::getListName).toList());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void StartGame(StartData data)
    {
        szo = "";
        isOnGoing = true;
        isVote = false;
        String[] idostring = data.getTime().split(":");
        time = 300;
        if (idostring.length == 2) {
            time = Integer.parseInt(idostring[0]) * 60 + Integer.parseInt(idostring[1]);
        } else { time = 300; }
        System.out.println("Játék idő: " + time );
        String selectedList = data.getSelectedWordList();
        for (WordCollection w : wordLista) {
            if (w.getListName().equals(selectedList)) szo = w.getRandomWord(); // select random next word
        }
        if (szo.equals("")) {
            System.out.println("Hiba!");
            System.exit(1);
        }
        System.out.println("\nA szó: " + szo);
        // Játékosok handlingje
        for (Jatekos j : jatekosok) {
            j.setAlive(true);
            j.setRole(0);
        }
        Random rnd = new Random();
        Jatekos j = jatekosok.get(rnd.nextInt(jatekosok.size()));
        j.setRole(1); // imposztor kiválasztva
        // MÁSODIK IMPOSZTOR 5 JÁTÉK VAGY A FELETT
        if (playerCount() >= 5 && data.isMultipleImpostor()) {
            Jatekos j2;
            do {
                j2 = jatekosok.get(rnd.nextInt(jatekosok.size()));
            } while (j2 == j || j2.getRole() == 1);
            j2.setRole(1);
        }
        //
        startCountdown();
    }

    public void startCountdown() {
        Thread countdownThread = new Thread(() -> {
            while (time > 0 && isOnGoing && playersAlive() > 2) {
                if (!isVote) time--;
                //System.out.println("Hátralévő idő: " + time);
                if (!isImpostorsAlive()) {
                    endGame();
                    break;
                }
                try {
                    Thread.sleep(1000); // 1 másodperc várakozás
                } catch (InterruptedException e) {
                    System.out.println("Szál megszakítva");
                    break;
                }
            }
            System.out.println("Idő lejárt!");
            endGame();
        });

        countdownThread.start();
    }

    public void endGame() {
        while (isImpostorsAlive() && playersAlive() >= 3) {
            initateVote();
        }
        System.out.println("A játék véget ért!" + (isImpostorsAlive() ? "Imposztor nyert" : "Elkapták az imposztort"));

        String imposztorok = "";

        for (Jatekos j : jatekosok) {
            if (j.getRole() == 1) imposztorok += j.getNev() + "\n";
        }

        setMessage("<h2>" + (isImpostorsAlive() ? "<p style='color:#d32f2f;'>Imposztor nyert!</p>" : "<p style='color:#28a745;'>Elkapták az imposztort!</p>") + "</h2><br>" + "Imposztorok:<i><br>" + imposztorok + "</i>");
        isOnGoing = false;
    }

    public void  initateVote() {
        isVote = true;
    }

    public boolean isVote() {
        return isVote;
    }

    public void setVote(boolean vote) {
        isVote = vote;
    }

    public String getSzo() {
        return szo;
    }

    public void setSzo(String szo) {
        this.szo = szo;
    }

    public boolean isOnGoing() {
        return isOnGoing;
    }

    public void setOnGoing(boolean onGoing) {
        isOnGoing = onGoing;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public List<Jatekos> getJatekosok() {
        return jatekosok;
    }

    public void addJatekos(Jatekos jatekos) {
        this.jatekosok.add(jatekos);
    }

    public void removeJatekos(String jatekos) {
        for (Jatekos j : jatekosok) {
            if (j.getNev().equals(jatekos)) rJatekos(j);
        }
    }

    public void setHasVoted(String jatekos, boolean vote) {
        for (Jatekos j : jatekosok) {
            if (j.getNev().equals(jatekos)) j.setHasVoted(vote);
        }
    }

    public void setAllVoteZero() {
        for (Jatekos j: jatekosok) {
            j.setHasVoted(false);
        }
    }

    private void rJatekos(Jatekos j) {
        jatekosok.remove(j);
    }

    public boolean findPlayerByName(String nev) {
        for (Jatekos j : jatekosok) {
            if (j.getNev().equals(nev)) return true;
        }
        return false;
    }

    public boolean findPlayerByNameAndIsAlive(String nev) {
        for (Jatekos j : jatekosok) {
            if (j.getNev().equals(nev) && j.isAlive()) return true;
        }
        return false;
    }

    public void killPlayer(String nev) {
        for (Jatekos j : jatekosok) {
            if (j.getNev().equals(nev)) j.setAlive(false);
        }
    }

    public int playerCount() {
        return jatekosok.size();
    }

    public int playersAlive() {
        return (int) jatekosok.stream() .filter(j -> j.isAlive()) .count();
    }

    public boolean isImpostorsAlive() {
        return jatekosok.stream()
                .anyMatch(j -> j.getRole() == 1 && j.isAlive());
    }

    public List<String> getWordCollection()
    {
        List<String> wordCollection = new ArrayList<>();
        for (WordCollection w : wordLista) {
            wordCollection.add(w.getListName());
        }
        return wordCollection;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMessage() {
        return message != null && !message.isEmpty();
    }
}

