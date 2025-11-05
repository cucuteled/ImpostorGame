package com.impogame.ImpostorGame;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class GameClass {

    private int time = 0;
    private List<Jatekos> jatekosok;
    private boolean isOnGoing;
    private String szo;

    private String message = "";

    private boolean isVote;

    private List<WordCollection> wordLista = new ArrayList<>();
    private List<WordCollection> LangLista = new ArrayList<>();

    private String selectedLang = "HU";

    private String sortType = "byplayer";

    private final Random rnd = new Random();

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

            // a langokat:

            Resource[] langres = resolver.getResources("classpath:static/lang/*.txt");

            for (Resource res : langres) {
                String filename = res.getFilename(); // pl. Sport.txt
                List<String> words = new ArrayList<>();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(res.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("#") || line.isEmpty()) continue;
                        try {
                            words.add(line.split("_:")[1]);
                        } catch (Exception e) {}
                    }
                }

                LangLista.add(new WordCollection(filename.replace(".txt", ""), words));
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
        message = "";
        // new game round add to stat:
        for (Jatekos j : jatekosok) {
            j.incAllTimeGame();
        }
        //
        String[] idostring = data.getTime().split(":");
        time = 300;
        if (idostring.length == 2) {
            time = Integer.parseInt(idostring[0]) * 60 + Integer.parseInt(idostring[1]);
        } else { time = 300; }
        System.out.println(langControl.time_text + " " + time );
        szo = getWordCollectionObject(data.getSelectedWordList()).getRandomWord(); // select random next word

        if (szo.equals("")) {
            System.out.println(langControl.error);
            System.exit(1);
        }
        System.out.println("\n" + langControl.println_theword + " " + szo);
        // Játékosok handlingje
        for (Jatekos j : jatekosok) {
            j.setAlive(true);
            j.setRole(0);
        }
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
                    System.out.println(langControl.error + " :Thread");
                    break;
                }
            }
            System.out.println(langControl.time_up);
            endGame();
        });

        countdownThread.start();
    }

    public String getSelectedLang() {
        return selectedLang;
    }

    public void endGame() {
        while (isImpostorsAlive() && playersAlive() >= 3) {
            initateVote();
        }
        System.out.println(langControl.game_end + (isImpostorsAlive() ? langControl.impostor_win : langControl.impostor_lose));

        String imposztorok = "";

        for (Jatekos j : jatekosok) {
            if (j.getRole() == 1) { // ha imposztor
                imposztorok += j.getNev() + "\n";
                if (isImpostorsAlive()) j.incWinAsImpostor(); // ha az imposztorok nyertek
            } else { // ha nem imposztor és nem az imposztorok nyertek
                if (!isImpostorsAlive() && j.isAlive()) j.incWinAsPlayer();
            }
        }

        setMessage(
                "<h2>" +
                        "<p style='color:" + (isImpostorsAlive() ? "#d32f2f" : "#28a745") + "; font-weight:bold; text-align:center;'>"
                        + (isImpostorsAlive() ? langControl.msg_impostor_win : langControl.msg_impostor_lose) + "</p>" +
                        "<p style='text-align:center;'>" + langControl.msg_impostors + "<br><i>" + imposztorok + "</i></p>"
        );
        isOnGoing = false;
        isVote = false;
        sortPlayers();
    }

    public void sortPlayers() {
        Collections.sort(jatekosok);
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
        sortPlayers();
    }

    public List<String> getLangs() {
        List<String> langs = new ArrayList<>();
        langs.add(getSelectedLang());
        for (WordCollection w : LangLista) {
            langs.add(w.getListName());
        }
        return langs;
    }

    public void setLang(String lang) {
        for (WordCollection w : LangLista) {
            if (w.getListName().equalsIgnoreCase(lang)) {
                selectedLang = lang.toUpperCase();
                langControl.initLang(w);
                break;
            }
        }
    }

    public Jatekos getPlayer(String JatekosNev) {
        for (Jatekos j : jatekosok) {
            if (j.getNev().equals(JatekosNev)) return j;
        }
        return jatekosok.getFirst(); // EZ EGY HIBA!
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
            j.setVotes(0);
        }
    }

    public void incVoteForPlayer(String playername) {
        for (Jatekos j: jatekosok) {
            if (j.getNev().equals(playername)) j.incVotes();
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
            String listname = w.getListName();
            String[] ln = listname.split("_");
            if (ln[0].equalsIgnoreCase(selectedLang.toLowerCase())) wordCollection.add(ln[1]);
        }
        return wordCollection;
    }

    public WordCollection getWordCollectionObject(String SelectedList)
    {
        for (WordCollection w : wordLista) {
            String listname = w.getListName();
            String[] ln = listname.split("_");
            if (ln[0].equalsIgnoreCase(selectedLang) && ln[1].equalsIgnoreCase(SelectedList)) return w;
        }
        return wordLista.get(1);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        System.out.println(message);
    }

    public boolean isMessage() {
        return message != null && !message.isEmpty();
    }
}

