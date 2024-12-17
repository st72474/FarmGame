package com.nesnidal.farm.game;

import com.nesnidal.farm.ConsoleCanvas;
import com.nesnidal.farm.ConsoleCommand;
import com.nesnidal.farm.animals.*;
import com.nesnidal.farm.crops.*;
import com.nesnidal.farm.disasters.Disaster;

import java.util.Locale;
import java.util.Random;

public class FarmGame {
    private final Random random;
    private final ConsoleCanvas canvas;
    private final ConsoleCommand command;
    private final Disaster disaster;

    private final FarmManager farmManager;
    private final MarketManager marketManager;

    private GameState gameState = GameState.STATE_FARM;
    private int round = 1;
    private double balance = GameSettings.STARTING_BALANCE;
    private boolean endFlag = false;

    public FarmGame() {
        this.random = new Random();
        this.canvas = new ConsoleCanvas(150, 25);
        this.command = new ConsoleCommand();
        this.disaster = new Disaster(random, GameSettings.DISASTER_PROBABILITY);

        this.farmManager = new FarmManager(this);
        this.marketManager = new MarketManager(this);

        farmManager.getFields().add(new CropField(100));
        marketManager.updateMarket(true);
    }

    public void gameLoop() {
        System.out.println("Maximalizuj své okno");
        System.out.println("Poté stiskni ENTER pro začátek hry");
        command.nextCommand();

        command.drawHelp(canvas);

        while (!endFlag) {
            command.nextCommand();
            switch (command.getName()) {
                case ConsoleCommand.CMD_HELP -> {
                    if (!command.getParam1().isEmpty()) System.out.println("Neplatný příkaz!");
                    else command.drawHelp(canvas);
                }
                case ConsoleCommand.CMD_FARM -> {
                    if (!command.getParam1().isEmpty()) System.out.println("Neplatný příkaz!");
                    else {
                        gameState = GameState.STATE_FARM;
                        farmManager.setSelectedField(null);
                        marketManager.setPage(MarketManager.DEFAULT_PAGE);

                        farmManager.drawFarm();
                    }
                }
                case ConsoleCommand.CMD_FARM_FIELD -> {
                    if (command.getParam1().isEmpty() || !command.getParam2().isEmpty()) System.out.println("Neplatný příkaz!");
                    else if (gameState != GameState.STATE_FARM && gameState != GameState.STATE_FIELD) System.out.println("Nenacházíš se na své farmě!");
                    else farmManager.selectField(command.getParam1());
                }
                case ConsoleCommand.CMD_FARM_FIELD_PLANT -> {
                    if (command.getParam1().isEmpty() || !command.getParam2().isEmpty()) System.out.println("Neplatný příkaz!");
                    else if (gameState != GameState.STATE_FIELD) System.out.println("Nenacházíš se na poli!");
                    else farmManager.plantField(command.getParam1());
                }
                case ConsoleCommand.CMD_MARKET -> {
                    if (!command.getParam1().isEmpty()) System.out.println("Neplatný příkaz!");
                    else {
                        gameState = GameState.STATE_MARKET;
                        farmManager.setSelectedField(null);
                        marketManager.nextPage();

                        marketManager.drawMarket();
                    }
                }
                case ConsoleCommand.CMD_SELL -> {
                    if (command.getParam2().isEmpty()) System.out.println("Neplatný příkaz!");
                    else if (gameState != GameState.STATE_MARKET) System.out.println("Nenacházíš se na trhu!");
                    else marketManager.sell(command.getParam1(), command.getParam2());
                }
                case ConsoleCommand.CMD_BUY -> {
                    if (command.getParam2().isEmpty()) System.out.println("Neplatný příkaz!");
                    else if (gameState != GameState.STATE_MARKET) System.out.println("Nenacházíš se na trhu!");
                    else marketManager.buy(command.getParam1(), command.getParam2());
                }
                case ConsoleCommand.CMD_SLEEP -> {
                    if (!command.getParam1().isEmpty()) System.out.println("Neplatný příkaz!");
                    else {
                        gameState = GameState.STATE_FARM;
                        farmManager.setSelectedField(null);
                        marketManager.setPage(MarketManager.DEFAULT_PAGE);
                        round++;

                        marketManager.updateMarket(round % GameSettings.MARKET_RESTART_ROUNDS == 0);

                        boolean isPlanted = false;
                        for (CropField cf : farmManager.getFields()) {
                            isPlanted |= cf.getCropCount() > 0;
                        }
                        if (isPlanted && round > GameSettings.DISASTER_START_ROUNDS) disaster.generateRandom();
                        else disaster.setNone();
                        farmManager.applyDisaster(disaster);

                        farmManager.feedAnimals();
                        drawStats();
                        farmManager.resetGeneratedStats();
                    }
                }
                case ConsoleCommand.CMD_END -> {
                    if (!command.getParam1().isEmpty()) System.out.println("Neplatný příkaz!");
                    else {
                        endFlag = true;
                        drawStats();
                    }
                }
                case ConsoleCommand.CMD_RST -> {
                    if (!command.getParam1().isEmpty()) System.out.println("Neplatný příkaz!");
                    else {
                        round = 1;
                        balance = GameSettings.STARTING_BALANCE;

                        gameState = GameState.STATE_FARM;
                        farmManager.getCropGroups().clear();
                        farmManager.setSelectedField(null);
                        farmManager.getFields().clear();
                        farmManager.getFields().add(new CropField(100));

                        marketManager.setPage(MarketManager.DEFAULT_PAGE);
                        marketManager.updateMarket(true);

                        command.drawHelp(canvas);
                    }
                }
                default -> System.out.println("Neplatný příkaz!");
            }
        }
    }

    public void drawStats() {
        canvas.clearCanvas();

        canvas.drawStringCentre(endFlag ? "Konec hry" : String.format("Konec kola %d", round - 1), 0, true);
        canvas.drawHorizontalLine('■', 1, true);
        canvas.drawHorizontalLine('■', canvas.getLastY(), true);
        canvas.drawVerticalLine('█', 0, 2, canvas.getLastY(), false);
        canvas.drawVerticalLine('█', canvas.getLastX(), 2, canvas.getLastY(), false);

        canvas.drawStringCentre(String.format(Locale.getDefault(), "Kapitál: %.2f$", balance), 3, true);

        if (!endFlag) {
            canvas.drawStringCentre(disaster.getDescription(), 4, true);

            canvas.drawString("Vyprodukováno:", (canvas.getWidth() / 2) - 40, 5, true);
            canvas.drawString("Ztraceno zvířat:", (canvas.getWidth() / 2) - 40, 15, true);
            for (int i = 0; i < farmManager.getAnimalGroups().size(); i++) {
                AnimalGroup ag = farmManager.getAnimalGroups().get(i);
                ProductGroup pg = ag.getProduct();
                canvas.drawString(String.format("%s: %d (celkem: %d)", pg.getName(), pg.getGeneratedCount(), pg.getCount()), (canvas.getWidth() / 2) - 36, 6 + i, false);
                canvas.drawString(String.format("%s: %d (celkem: %d)", ag.getName(), ag.getLostCount(), ag.getCount()), (canvas.getWidth() / 2) - 36, 16 + i, false);
            }

            canvas.drawString("Sklizeno:", (canvas.getWidth() / 2) + 30, 5, true);
            for (int i = 0; i < farmManager.getCropGroups().size(); i++) {
                CropGroup cg = farmManager.getCropGroups().get(i);
                canvas.drawString(String.format("%s: %d (celkem: %d)", cg.getName(), cg.getGeneratedCount(), cg.getCount()), (canvas.getWidth() / 2) + 34, 6 + i, false);
            }
        } else {
            canvas.drawStringCentre(String.format("Odehráno kol: %d", round - 1), 4, true);
        }

        canvas.printCanvas();
    }

    public Random getRandom() {
        return random;
    }
    public ConsoleCanvas getCanvas() {
        return canvas;
    }
    public int getRound() {
        return round;
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public GameState getGameState() {
        return gameState;
    }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public FarmManager getFarmManager() {
        return farmManager;
    }
    public MarketManager getMarketManager() {
        return marketManager;
    }
}
