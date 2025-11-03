package com.impogame.ImpostorGame.Controllers;

import com.impogame.ImpostorGame.ImpostorGameApplication;
import com.impogame.ImpostorGame.Jatekos;
import com.impogame.ImpostorGame.StartData;
import com.sun.tools.javac.Main;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RequestController {

    // for the connecting users
    @GetMapping("/")
    public String home() {
        return "enter";
    }

    // game HTML
    @GetMapping("/game")
    public String redirectToGame() {
        return "game";
    }

    // for the host
    @GetMapping("/start")
    public String GameControlPanel() {
        return "GameControlPanel";
    }

    @GetMapping("/get-lang")
    @ResponseBody
    public List<String> getLang() {
        return ImpostorGameApplication.Jatek.getLangs();
    }

    @PostMapping("/set-lang")
    @ResponseBody
    public String setLang(@RequestParam String Lang) {
        ImpostorGameApplication.Jatek.setLang(Lang);
        System.out.println(Lang);
        return "ok";
    }

    @GetMapping("/get-ip")
    @ResponseBody
    public String getClientIp(HttpServletRequest request) {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ip = inetAddress.getHostAddress();
            return ip;
        } catch (Exception e) {

        }
        return "localhost";
    }

    // START THE GAME ---!
    @PostMapping("/startGame")
    @ResponseBody
    public String startGame(@RequestBody StartData startData)
    {
        if (ImpostorGameApplication.Jatek.isOnGoing()) return "Már elindult.";
        if (ImpostorGameApplication.Jatek.playerCount() < 3) return "Minimum 3 játékos!";

        System.out.println("Játék indul! Szólista: " + startData.getSelectedWordList() + ", idő: " + startData.getTime());
        ImpostorGameApplication.Jatek.StartGame(startData);

        return "ok"; // game started
    }

    // voting

    private static String wantToVote = "";
    private static int wantToVoteInt = 0;
    private static String votedPlayers = "";

    public static void initVote() {
        wantToVote = "";
        wantToVoteInt = 0;
        votedPlayers = "";

        ImpostorGameApplication.Jatek.setAllVoteZero();
    }

    @PostMapping("/vote")
    @ResponseBody
    public String vote(@RequestParam String voter,
                       String votedPlayer) {
        if (voter == null || voter.trim().isEmpty()) {
            return "Hibás név";
        }
        //System.out.println("voter: " + voter + "\nvoted: " + votedPlayer + "\n--");
        if (!ImpostorGameApplication.Jatek.findPlayerByName(voter)) return "Nincs ilyen nevű";
        // hozzáadjuk a szavazást kezdeményezi kivánót
        if (!wantToVote.contains(voter) && ImpostorGameApplication.Jatek.findPlayerByNameAndIsAlive(voter)) {

            // Számoljuk a szavazatokat ha megy a szavazás, olyanoktól akik még nincsenek a wantToVoteban, azaz még nem voksoltak
            if (ImpostorGameApplication.Jatek.isVote()) {
                if (ImpostorGameApplication.Jatek.findPlayerByName(votedPlayer) && !wantToVote.contains(voter)) {
                    votedPlayers += votedPlayer + ";";
                    ImpostorGameApplication.Jatek.incVoteForPlayer(votedPlayer);
                }
            }

            ImpostorGameApplication.Jatek.setHasVoted(voter, true);

            wantToVote += voter + ";";
            wantToVoteInt++;
        }

        //System.out.println("wantovote: " + wantToVote + "\n" + "wantotovoteint: " + wantToVoteInt + "\n votedplayers: " + votedPlayers + "\n---\n" + voter + ":" + votedPlayer);

        // init vote, HA: az élő játékosok több mint 50% szavazni akar
        if ((double)wantToVoteInt / (double)ImpostorGameApplication.Jatek.playersAlive() *100.0 > 50.0 && !ImpostorGameApplication.Jatek.isVote()) {
            ImpostorGameApplication.Jatek.initateVote();
            wantToVote = ""; // kiürítjük mert a szavazás alatt is kell számolni ki szavazott
            wantToVoteInt = 0;
            ImpostorGameApplication.Jatek.setAllVoteZero();
        }

        if (wantToVoteInt == ImpostorGameApplication.Jatek.playersAlive()) {
            // megszámoljuk a szavazatot és a legtöbbet kapott player meghal
            Map<String, Integer> counter = new HashMap<>();
            for (String s : votedPlayers.split(";")) {
                if (s.isEmpty()) continue;
                counter.put(s, counter.getOrDefault(s, 0) + 1);
            }

            String legtobb = null;
            int max = -1;
            int maxCount = 0;

            for (Map.Entry<String, Integer> e : counter.entrySet()) {
                if (e.getValue() > max) {
                    max = e.getValue();
                    legtobb = e.getKey();
                    maxCount = 1;
                } else if (e.getValue() == max) {
                    maxCount++;
                }
            }

            if (maxCount > 1) {
                // DÖNTETLEN SZAVAZÁS:
                ImpostorGameApplication.Jatek.setMessage("[!]Döntetlen, a szavazás érvénytelen és megismétlődik");
                // szavazás újra kezdése
                initVote();
                return "ok";
            } else {
                // KIESIK:
                ImpostorGameApplication.Jatek.setMessage("[!]Kiesett: " + legtobb);
            }

            if (legtobb != null) {
                ImpostorGameApplication.Jatek.killPlayer(legtobb);
            }
            // vote vége
            initVote();
            ImpostorGameApplication.Jatek.setVote(false);
        }

        return "ok";
    }

    // vote end

    @GetMapping("/registerMe")
    @ResponseBody
    public String registerMe(@RequestParam String nev)
    {
        if (nev == null || nev.trim().isEmpty()) {
            return "Hibás név";
        }
        if (ImpostorGameApplication.Jatek.findPlayerByName(nev)) return "Már van ilyen nevű játékos.";
        if (ImpostorGameApplication.Jatek.playerCount() >=8) return "Maximum 8 játékos!";
        if (ImpostorGameApplication.Jatek.isOnGoing()) return "Már elindult!";

        ImpostorGameApplication.Jatek.addJatekos(new Jatekos(nev));

        return "ok";
    }

    @PostMapping("/stop")
    @ResponseBody
    public String stop() {

        new Thread(() -> {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
            }
            System.exit(0);
        }).start();

        return "ok";
    }

    @GetMapping("/authMe")
    @ResponseBody
    public String authMe(@CookieValue(value = "playerName", required = false) String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return "noauth";
        }
        if (ImpostorGameApplication.Jatek.findPlayerByName(playerName)) {
            return "ok";
        } else {
            return "noauth";
        }
    }

    @GetMapping("/getinfo")
    @ResponseBody
    public infoData getInfo(String auth) {
        if (auth == null || auth.equals("")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Hiba");
        }
        if (ImpostorGameApplication.Jatek.findPlayerByName(auth) == false && auth.equals("admin") == false) {
            infoData hiba = new infoData(
                    -99,
                    ImpostorGameApplication.Jatek.getJatekosok(),
                    ImpostorGameApplication.Jatek.isOnGoing(),
                    ImpostorGameApplication.Jatek.getSzo()
            );
            return hiba;
        }
        infoData returnData = new infoData(
                ImpostorGameApplication.Jatek.getTime(),
                ImpostorGameApplication.Jatek.getJatekosok(),
                ImpostorGameApplication.Jatek.isOnGoing(),
                ImpostorGameApplication.Jatek.getSzo()
        );
        return returnData;
    }

    @PostMapping("/removePlayer")
    @ResponseBody
    public String removePlayer(@RequestParam String nev) {
        ImpostorGameApplication.Jatek.removeJatekos(nev);
        return "ok";
    }

}

