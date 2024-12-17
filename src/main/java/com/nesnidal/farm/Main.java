package com.nesnidal.farm;

import com.nesnidal.farm.game.FarmGame;

public class Main {
    public static void main(String[] args) {
        FarmGame game = new FarmGame();
        game.gameLoop();
    }
}