package com.impogame.ImpostorGame;

import java.util.List;

public class langControl {

    // Common
    public static String error = "Hiba!";

    // GameClass
    public static String time_text = "Játék idő:";
    public static String println_theword = "A szó:";
    public static String time_up = "Az idő lejárt!";
    public static String game_end = "A játék véget ért!";
    public static String impostor_win = "Imposztor nyert";
    public static String impostor_lose = "Elkapták az imposztort";
    public static String msg_impostor_win = "Az imposztor győzött!";
    public static String msg_impostor_lose = "A játékosok nyertek";
    public static String msg_impostors = "Imposztorok:";

    // RequestController
    public static String game_starts = "Játék indul! Szólista:";
    public static String time = ", idő:";
    public static String msg_tie_vote = "[!]Döntetlen, a szavazás érvénytelen és megismétlődik";
    public static String msg_player_voted_out = "[!]Kiesett:";
    public static String game_start_info_name_equal_error = "Már van ilyen nevű játékos.";
    public static String game_start_info_maximum_8_player = "Maximum 8 játékos!";
    public static String game_start_info_minumum_3_player = "Minimum 3 játékos!";
    public static String game_start_info_already_started = "Már elindult!";

    public static void initLang(WordCollection Lang) {
        List<String> l = Lang.getWords();
        int i = 0;
        // --- Common ---
        error = l.get(i++);

        // --- GameClass ---
        time_text = l.get(i++);
        println_theword = l.get(i++);
        time_up = l.get(i++);
        game_end = l.get(i++);
        impostor_win = l.get(i++);
        impostor_lose = l.get(i++);
        msg_impostor_win = l.get(i++);
        msg_impostor_lose = l.get(i++);
        msg_impostors = l.get(i++);

        // --- RequestController ---
        game_starts = l.get(i++);
        time = l.get(i++);
        msg_tie_vote = l.get(i++);
        msg_player_voted_out = l.get(i++);
        game_start_info_name_equal_error = l.get(i++);
        game_start_info_maximum_8_player = l.get(i++);
        game_start_info_minumum_3_player = l.get(i++);
        game_start_info_already_started = l.get(i++);
    }
}
