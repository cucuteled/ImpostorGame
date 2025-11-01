package com.impogame.ImpostorGame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class ImpostorGameApplication {

    public static GameClass Jatek = new GameClass();

	public static void main(String[] args) throws URISyntaxException {
        SpringApplication.run(ImpostorGameApplication.class, args);
    }

        @EventListener(ApplicationReadyEvent.class)
        public void openBrowserWhenReady() {
            try {
                new ProcessBuilder("cmd", "/c", "start http://localhost:80/start").start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}

