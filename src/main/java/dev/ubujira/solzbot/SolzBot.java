package dev.ubujira.solzbot;

import lombok.Getter;

public class SolzBot {

    @Getter
    private static SolzBot instance;

    public SolzBot() {

    }

    public static void main(String[] args) {
        new SolzBot();
    }

}
