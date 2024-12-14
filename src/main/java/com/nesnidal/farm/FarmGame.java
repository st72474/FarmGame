package com.nesnidal.farm;

import com.nesnidal.farm.animals.*;
import com.nesnidal.farm.crops.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class FarmGame {
    //Game parameters
    private final double STARTING_BALANCE = 100.0;                  //Nastaví počáteční kapitál
    private final double CROP_PRICE_MULTIPLIER = 5.0;               //Nastaví globální násobitel ceny plodin na trhu
    private final double ANIMAL_PRICE_MULTIPLIER = 5.0;             //Nastaví globální násobitel ceny zvířat na trhu
    private final double ANIMAL_PRODUCT_PRICE_MULTIPLIER = 5.0;     //Nastaví globální násobitel ceny produktů zvířat na trhu
    private final double FIELD_PRICE_MULTIPLIER = 8.0;              //Nastaví globální násobitel ceny pole na trhu
    private final int MARKET_RESTART_ROUNDS = 20;                   //Nastavení po kolika kolech se obnovuje trh (doplnění zboží a úprava cen)
    private final int MARKET_FIELDS_COUNT = 5;                      //Nastavení maximální počet dostupných polí na trhu
    private final int DISASTER_START_ROUNDS = 10;                   //Nastavení po kolika kolech se mohou začít vyskytovat pohromy
    private final double DISASTER_PROBABILITY = 0.15;               //Nastaví pravděpodobnost pohromy v každém kole (hodnoty 0 až 1)

    private final Random random;
    private final ConsoleCanvas canvas;
    private final ConsoleCommand command;
    private final Disaster disaster;

    private GameState gameState = GameState.STATE_FARM;
    private int round = 1;
    private double balance = STARTING_BALANCE;
    private boolean endFlag = false;

    private ArrayList<CropField> fields = new ArrayList<>();
    private CropField selectedField = null;
    private ArrayList<CropGroup> cropGroups = new ArrayList<>();
    private ArrayList<AnimalGroup> animalGroups = new ArrayList<>();

    private ArrayList<CropGroup> marketCropGroups = new ArrayList<>();
    private ArrayList<AnimalGroup> marketAnimalGroups = new ArrayList<>();
    private ArrayList<CropField> marketFields = new ArrayList<>();
    private int marketPage = 2;

    public FarmGame() {
        this.random = new Random();
        this.canvas = new ConsoleCanvas(150, 25);
        this.command = new ConsoleCommand();
        this.disaster = new Disaster(random, DISASTER_PROBABILITY);

        this.fields.add(new CropField(100));
        updateMarket(true);
    }

    public enum GameState {
        STATE_FARM,
        STATE_MARKET,
        STATE_FIELD,
    }

    public void startLoop() {
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
                        selectedField = null;
                        marketPage = 2;

                        drawFarm(canvas);
                    }
                }
                case ConsoleCommand.CMD_FARM_FIELD -> {
                    if (command.getParam1().isEmpty() || !command.getParam2().isEmpty()) System.out.println("Neplatný příkaz!");
                    else if (gameState != GameState.STATE_FARM && gameState != GameState.STATE_FIELD) System.out.println("Nenacházíš se na své farmě!");
                    else {
                        try {
                            int fieldNum = Integer.parseInt(command.getParam1()) - 1;

                            if (fieldNum < 0 || fieldNum + 1 > fields.size()) System.out.println("Zvolené pole neexistuje!");
                            else {
                                gameState = GameState.STATE_FIELD;
                                selectedField = fields.get(fieldNum);
                                drawField(canvas);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Zvolené pole neexistuje!");
                        }
                    }
                }
                case ConsoleCommand.CMD_FARM_FIELD_PLANT -> {
                    if (command.getParam1().isEmpty() || !command.getParam2().isEmpty()) System.out.println("Neplatný příkaz!");
                    else if (gameState != GameState.STATE_FIELD) System.out.println("Nenacházíš se na poli!");
                    else {
                        CropGroup selectedCropGroup = null;
                        for (CropGroup cg : cropGroups) {
                            if (Objects.equals(cg.getId(), command.getParam1())) {
                                selectedCropGroup = cg;
                                break;
                            }
                        }
                        if (selectedCropGroup == null) System.out.println("Nevlastníš zvolenou plodinu!");
                        else {
                            if (selectedCropGroup.getCount() <= 0) System.out.println("Nemáš dostatek zvolené plodiny!");
                            else if (selectedField.getCropCount() == selectedField.getCapacity()) System.out.println("Pole je již plné!");
                            else if (selectedField.getCropGroup() != null && selectedField.getCropGroup() != selectedCropGroup) System.out.println("Na poli již roste jiná plodina!");
                            else {
                                selectedField.setCropGroup(selectedCropGroup);
                                selectedField.addCropCount(selectedCropGroup.removeCount(selectedField.getCapacity() - selectedField.getCropCount()));
                                drawField(canvas);
                            }
                        }
                    }
                }
                case ConsoleCommand.CMD_MARKET -> {
                    if (!command.getParam1().isEmpty()) System.out.println("Neplatný příkaz!");
                    else {
                        gameState = GameState.STATE_MARKET;
                        selectedField = null;

                        if (marketPage < 2) marketPage++;
                        else marketPage = 0;

                        drawMarket(canvas);
                    }
                }
                case ConsoleCommand.CMD_SELL -> {
                    if (command.getParam2().isEmpty()) System.out.println("Neplatný příkaz!");
                    else if (gameState != GameState.STATE_MARKET) System.out.println("Nenacházíš se na trhu!");
                    else {
                        switch (marketPage) {
                            case 0 -> {
                                try {
                                    String sellId = command.getParam1();
                                    CropGroup sellCropGroup = null;
                                    for (CropGroup cg : cropGroups) {
                                        if (Objects.equals(cg.getId(), sellId)) sellCropGroup = cg;
                                    }

                                    if (sellCropGroup == null) System.out.println("Zvolený produkt není platný!");
                                    else {
                                        int sellCount;
                                        if (Objects.equals(command.getParam2(), "vse")) sellCount = sellCropGroup.getCount();
                                        else sellCount = Integer.parseInt(command.getParam2());

                                        CropGroup marketCropGroup = null;
                                        for (CropGroup cg : marketCropGroups) {
                                            if (Objects.equals(cg.getId(), sellId)) marketCropGroup = cg;
                                        }
                                        if (marketCropGroup != null) {
                                            int sellCountReal = sellCropGroup.removeCount(sellCount);
                                            if (sellCountReal == 0) System.out.println("Nemáš dostek této plodiny!");
                                            else {
                                                if (sellCountReal < sellCount)
                                                    System.out.printf("Nemáš dostek této plodiny! Prodáno pouze: %d\n", sellCountReal);
                                                marketCropGroup.addCount(sellCountReal, false);
                                                double sellPrice = sellCountReal * marketCropGroup.getPrice();
                                                balance += sellPrice;

                                                System.out.printf(Locale.getDefault(), "Prodej: %d %s\t\tCelkový výdělek: %.2f$\n", sellCountReal, marketCropGroup.getName(), sellPrice);

                                                drawMarket(canvas);
                                            }
                                        }
                                    }
                                } catch (NumberFormatException ex) {
                                    System.out.println("Zvolený počet není platný!");
                                }
                            }
                            case 1 -> {
                                try {
                                    String sellId = command.getParam1();
                                    ProductGroup sellProductGroup = null;
                                    for (AnimalGroup ag : animalGroups) {
                                        if (Objects.equals(ag.getProduct().getId(), sellId)) sellProductGroup = ag.getProduct();
                                    }

                                    if (sellProductGroup == null) System.out.println("Zvolený produkt není platný!");
                                    else {
                                        int sellCount;
                                        if (Objects.equals(command.getParam2(), "vse")) sellCount = sellProductGroup.getCount();
                                        else sellCount = Integer.parseInt(command.getParam2());

                                        ProductGroup marketProductGroup = null;
                                        for (AnimalGroup ag : marketAnimalGroups) {
                                            if (Objects.equals(ag.getProduct().getId(), sellId)) marketProductGroup = ag.getProduct();
                                        }
                                        if (marketProductGroup != null) {
                                            int sellCountReal = sellProductGroup.removeCount(sellCount);
                                            if (sellCountReal == 0) System.out.println("Nemáš dostek tohoto produktu!");
                                            else {
                                                if (sellCountReal < sellCount) System.out.printf("Nemáš dostek tohoto produktu! Prodáno pouze: %d\n", sellCountReal);
                                                marketProductGroup.addCount(sellCountReal);
                                                double sellPrice = sellCountReal * marketProductGroup.getPrice();
                                                balance += sellPrice;

                                                System.out.printf(Locale.getDefault(), "Prodej: %d %s\t\tCelkový výdělek: %.2f$\n", sellCountReal, sellProductGroup.getName(), sellPrice);
                                                drawMarket(canvas);
                                            }
                                        }
                                    }
                                } catch (NumberFormatException ex) {
                                    System.out.println("Zvolený počet není platný!");
                                }
                            }
                            case 2 -> {
                                System.out.println("Neplatný příkaz!");
                            }
                        }
                    }
                }
                case ConsoleCommand.CMD_BUY -> {
                    if (command.getParam2().isEmpty()) System.out.println("Neplatný příkaz!");
                    else if (gameState != GameState.STATE_MARKET) System.out.println("Nenacházíš se na trhu!");
                    else {
                        switch (marketPage) {
                            case 0 -> {
                                try {
                                    int buyCount = Integer.parseInt(command.getParam2());
                                    String buyId = command.getParam1();

                                    CropGroup marketCropGroup = null;
                                    for (CropGroup cg : marketCropGroups) {
                                        if (Objects.equals(cg.getId(), buyId)) marketCropGroup = cg;
                                    }

                                    if (marketCropGroup == null) System.out.println("Zvolený produkt není platný!");
                                    else {
                                        CropGroup buyCropGroup = null;
                                        for (CropGroup cg : cropGroups) {
                                            if (Objects.equals(cg.getId(), buyId)) buyCropGroup = cg;
                                        }

                                        int buyCountReal = marketCropGroup.removeCount(buyCount);
                                        double buyPrice = buyCountReal * marketCropGroup.getPrice();
                                        if (buyCountReal == 0) System.out.println("Trh nemá dostek této plodiny!");
                                        else if (buyPrice > balance) {
                                            System.out.println("Nemáš dostatek mincí na tento nákup!");
                                            marketCropGroup.addCount(buyCountReal, false);
                                        }
                                        else {
                                            if (buyCountReal < buyCount) System.out.printf("Trh nemá dostek této plodiny! Koupeno pouze: %d\n", buyCountReal);

                                            if (buyCropGroup == null) {
                                                buyCropGroup = marketCropGroup.getClone();
                                                cropGroups.add(buyCropGroup);
                                            }
                                            buyCropGroup.addCount(buyCountReal, false);
                                            balance -= buyPrice;

                                            System.out.printf(Locale.getDefault(), "Nákup: %d %s\t\tCelková cena: %.2f$\n", buyCountReal, buyCropGroup.getName(), buyPrice);
                                            drawMarket(canvas);
                                        }
                                    }
                                } catch (NumberFormatException ex) {
                                    System.out.println("Zvolený počet není platný!");
                                }
                            }
                            case 1 ->{
                                try {
                                    int buyCount = Integer.parseInt(command.getParam2());
                                    String buyId = command.getParam1();

                                    AnimalGroup marketAnimalGroup = null;
                                    for (AnimalGroup ag : marketAnimalGroups) {
                                        if (Objects.equals(ag.getId(), buyId)) marketAnimalGroup = ag;
                                    }

                                    if (marketAnimalGroup == null) System.out.println("Zvolený produkt není platný!");
                                    else {
                                        AnimalGroup buyAnimalGroup = null;
                                        for (AnimalGroup ag : animalGroups) {
                                            if (Objects.equals(ag.getId(), buyId)) buyAnimalGroup = ag;
                                        }

                                        int buyCountReal = marketAnimalGroup.removeCount(buyCount);
                                        double buyPrice = buyCountReal * marketAnimalGroup.getPrice();
                                        if (buyCountReal == 0) System.out.println("Trh nemá dostek těchto zvířat!");
                                        else if (buyPrice > balance) {
                                            System.out.println("Nemáš dostatek mincí na tento nákup!");
                                            marketAnimalGroup.addCount(buyCountReal);
                                        }
                                        else {
                                            if (buyCountReal < buyCount) System.out.printf("Trh nemá dostek těchto zvířat! Koupeno pouze: %d\n", buyCountReal);

                                            if (buyAnimalGroup == null) {
                                                buyAnimalGroup = marketAnimalGroup.getClone();
                                                buyAnimalGroup.getProduct().setCount(0);
                                                animalGroups.add(buyAnimalGroup);
                                            }
                                            buyAnimalGroup.addCount(buyCountReal);
                                            balance -= buyPrice;

                                            System.out.printf(Locale.getDefault(), "Nákup: %d %s\t\tCelková cena: %.2f$\n", buyCountReal, buyAnimalGroup.getName(), buyPrice);
                                            drawMarket(canvas);
                                        }
                                    }
                                } catch (NumberFormatException ex) {
                                    System.out.println("Zvolený počet není platný!");
                                }
                            }
                            case 2 -> {
                                if (!Objects.equals(command.getParam1(), "pole")) System.out.println("Tady můžeš koupit pouze pole!");
                                try {
                                    int fieldNum = Integer.parseInt(command.getParam2()) - 1;
                                    CropField buyField = marketFields.get(fieldNum);
                                    if (buyField == null) System.out.println("Zvolené pole není platné!");
                                    else if (FIELD_PRICE_MULTIPLIER * buyField.getCapacity() > balance) System.out.println("Nemáš dostatek mincí na tento nákup!");
                                    else {
                                        double buyPrice = FIELD_PRICE_MULTIPLIER * buyField.getCapacity();
                                        fields.add(new CropField(buyField.getCapacity()));
                                        marketFields.remove(fieldNum);
                                        balance -= buyPrice;

                                        System.out.printf(Locale.getDefault(), "Nákup pole\t\tCelková cena: %.2f$\n", buyPrice);
                                        drawMarket(canvas);
                                    }
                                } catch (NumberFormatException ex) {
                                    System.out.println("Zvolené pole není platné!");
                                }
                            }
                        }
                    }
                }
                case ConsoleCommand.CMD_SLEEP -> {
                    if (!command.getParam1().isEmpty()) System.out.println("Neplatný příkaz!");
                    else {
                        gameState = GameState.STATE_FARM;
                        selectedField = null;
                        marketPage = 2;
                        round++;

                        updateMarket(round % MARKET_RESTART_ROUNDS == 0);

                        boolean isPlanted = false;
                        for (CropField cf : fields) {
                            isPlanted |= cf.getCropCount() > 0;
                        }
                        if (isPlanted && round > DISASTER_START_ROUNDS) disaster.generateRandom();
                        else disaster.setNone();

                        for (CropField cf : fields) {
                            cf.setCropCount((int)(cf.getCropCount() * disaster.getMultiplier()));
                            if (cf.getCropCount() == 0) {
                                cf.setCropGroup(null);
                                cf.resetGrow();
                            } else {
                                if (cf.grow()) {
                                    cf.getCropGroup().addCount((int)(cf.getCropCount() * cf.getCropGroup().getProductivity()), true);
                                    cf.setCropGroup(null);
                                    cf.setCropCount(0);
                                }
                            }
                        }

                        for (AnimalGroup ag : animalGroups) {
                            CropGroup feedCropGroup = null;
                            for (CropGroup cg : cropGroups) {
                                if (Objects.equals(ag.getFoodName(), cg.getName())) feedCropGroup = cg;
                            }

                            int lostCount = 0;
                            if (feedCropGroup == null) lostCount = ag.getCount();
                            else {
                                for (int i = 0; i < ag.getCount(); i++) {
                                    if (ag.getFoodAmount() != feedCropGroup.removeCount(ag.getFoodAmount())) lostCount++;
                                }
                            }
                            ag.removeCount(lostCount);

                            if (ag.produce()) {
                                ag.getProduct().addCount((int)(ag.getCount() * ag.getProduct().getProductivity()));
                            }
                        }

                        drawStats(canvas);

                        for (CropGroup cg : cropGroups) {
                            cg.setGeneratedCount(0);
                        }
                        for (AnimalGroup ag : animalGroups) {
                            ag.setLostCount(0);
                            ag.getProduct().setGeneratedCount(0);
                        }
                    }
                }
                case ConsoleCommand.CMD_END -> {
                    if (!command.getParam1().isEmpty()) System.out.println("Neplatný příkaz!");
                    else {
                        endFlag = true;
                        drawStats(canvas);
                    }
                }
                case ConsoleCommand.CMD_RST -> {
                    if (!command.getParam1().isEmpty()) System.out.println("Neplatný příkaz!");
                    else {
                        round = 1;
                        balance = STARTING_BALANCE;

                        gameState = GameState.STATE_FARM;
                        cropGroups.clear();
                        selectedField = null;
                        fields.clear();
                        this.fields.add(new CropField(100));

                        marketPage = 2;
                        updateMarket(true);

                        command.drawHelp(canvas);
                    }
                }
                default -> System.out.println("Neplatný příkaz!");
            }
        }
    }

    public void drawStats(ConsoleCanvas canvas) {
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
            for (int i = 0; i < animalGroups.size(); i++) {
                AnimalGroup ag = animalGroups.get(i);
                ProductGroup pg = ag.getProduct();
                canvas.drawString(String.format("%s: %d (celkem: %d)", pg.getName(), pg.getGeneratedCount(), pg.getCount()), (canvas.getWidth() / 2) - 36, 6 + i, false);
                canvas.drawString(String.format("%s: %d (celkem: %d)", ag.getName(), ag.getLostCount(), ag.getCount()), (canvas.getWidth() / 2) - 36, 16 + i, false);
            }

            canvas.drawString("Sklizeno:", (canvas.getWidth() / 2) + 30, 5, true);
            for (int i = 0; i < cropGroups.size(); i++) {
                CropGroup cg = cropGroups.get(i);
                canvas.drawString(String.format("%s: %d (celkem: %d)", cg.getName(), cg.getGeneratedCount(), cg.getCount()), (canvas.getWidth() / 2) + 34, 6 + i, false);
            }
        } else {
            canvas.drawStringCentre(String.format("Odehráno kol: %d", round - 1), 4, true);
        }

        canvas.printCanvas();
    }

    public void drawFarm(ConsoleCanvas canvas) {
        canvas.clearCanvas();

        canvas.drawStringCentre(String.format("Kolo %d", round), 2, true);
        canvas.drawStringCentre("Farma", 0, true);
        canvas.drawHorizontalLine('■', 1, true);
        canvas.drawHorizontalLine('■', canvas.getLastY(), true);
        canvas.drawVerticalLine('█', 0, 2, canvas.getLastY(), false);
        canvas.drawVerticalLine('█', canvas.getLastX(), 2, canvas.getLastY(), false);

        canvas.drawString(String.format(Locale.getDefault(), "Kapitál: %.2f$", balance), 2, 2, true);
        canvas.drawString(String.format("Počet polí: %d", fields.size()), 2, 3, true);
        canvas.drawString("Sklad", 2, 5, true);
        canvas.drawHorizontalLine('■', 8, canvas.getLastX(), 5, false);

        for (int i = 0; i < cropGroups.size(); i++) {
            CropGroup cg = cropGroups.get(i);
            canvas.drawString(String.format("%s: %d", cg.getName(), cg.getCount()), 2, 6 + i, false);
        }
        for (int i = 0; i < animalGroups.size(); i++) {
            AnimalGroup ag = animalGroups.get(i);
            canvas.drawString(String.format("%s: %d", ag.getProduct().getName(), ag.getProduct().getCount()), canvas.getWidth() / 4, 6 + i, false);
        }

        canvas.drawVerticalLine('█', (canvas.getWidth() / 2) - 1, 6, canvas.getLastY(), false);
        canvas.drawString(" Zvířata ", canvas.getWidth() / 2, 5, true);

        for (int i = 0; i < animalGroups.size(); i++) {
            AnimalGroup ag = animalGroups.get(i);
            ProductGroup pg = ag.getProduct();
            canvas.drawString(String.format("%s: %d", ag.getName(), ag.getCount()), (canvas.getWidth() / 2) + 1, 6 + 3 * i, false);
            canvas.drawString(String.format("Krmení: %s (%d/kolo)", ag.getFoodName(), ag.getFoodAmount()), (canvas.getWidth() / 2) + 5, 7 + 3 * i, false);
            canvas.drawString(String.format(Locale.getDefault(), "Produkuje: %s (%.2f/%d kol)", pg.getName(), pg.getProductivity(), pg.getRoundsToProduce()), (canvas.getWidth() / 2) + 5, 8 + 3 * i, false);
        }

        canvas.printCanvas();
    }

    public void drawMarket(ConsoleCanvas canvas) {
        canvas.clearCanvas();

        canvas.drawString(String.format("Kolo %d", round), (canvas.getWidth() / 4) + 10, 2, true);
        canvas.drawStringCentre(String.format("Trh %d/3", marketPage + 1), 0, true);
        canvas.drawHorizontalLine('■', 1, true);
        canvas.drawHorizontalLine('■', canvas.getLastY(), true);
        canvas.drawVerticalLine('█', 0, 2, canvas.getLastY(), false);
        canvas.drawVerticalLine('█', canvas.getLastX(), 2, canvas.getLastY(), false);

        canvas.drawString(String.format(Locale.getDefault(), "Kapitál: %.2f$", balance), 2, 2, true);
        canvas.drawString(String.format("Počet polí: %d", fields.size()), 2, 3, true);
        canvas.drawString("Sklad", 2, 5, true);
        canvas.drawHorizontalLine('■', 8, (canvas.getWidth() / 2) - 2, 5, false);
        for (int i = 0; i < cropGroups.size(); i++) {
            CropGroup cg = cropGroups.get(i);
            canvas.drawString(String.format("%s: %d", cg.getName(), cg.getCount()), 2, 6 + i, false);
        }
        for (int i = 0; i < animalGroups.size(); i++) {
            AnimalGroup ag = animalGroups.get(i);
            ProductGroup pg = ag.getProduct();
            canvas.drawString(String.format("%s: %d", ag.getName(), ag.getCount()), (canvas.getWidth() / 4), 6 + 2 * i, false);
            canvas.drawString(String.format("%s: %d", pg.getName(), pg.getCount()), (canvas.getWidth() / 4) + 4, 7 + 2 * i, false);
        }

        canvas.drawVerticalLine('█', (canvas.getWidth() / 2) - 1, 1, canvas.getLastY(), false);
        switch (marketPage) {
            case 0 -> {
                for (int i = 0; i < marketCropGroups.size(); i++) {
                    CropGroup cg = marketCropGroups.get(i);
                    canvas.drawString(String.format("%s: %d", cg.getName(), cg.getCount()), (canvas.getWidth() / 2) + 1, 2 + 3 * i, false);
                    canvas.drawString(String.format(Locale.getDefault(), "Cena: %.2f$/kus", cg.getPrice()), (canvas.getWidth() / 2) + 5, 3 + 3 * i, false);
                    canvas.drawString(String.format("Doba růstu: %d kol, výnosnost: %d %%", cg.getRoundsToGrow(), (int)Math.round((cg.getProductivity() * 100.0))), (canvas.getWidth() / 2) + 5, 4 + 3 * i, false);
                }
            }
            case 1 -> {
                for (int i = 0; i < marketAnimalGroups.size(); i++) {
                    AnimalGroup ag = marketAnimalGroups.get(i);
                    ProductGroup pg = ag.getProduct();
                    canvas.drawString(String.format("%s: %d", ag.getName(), ag.getCount()), (canvas.getWidth() / 2) + 1, 2 + 4 * i, false);
                    canvas.drawString(String.format(Locale.getDefault(), "Cena: %.2f$/kus", ag.getPrice()), (canvas.getWidth() / 2) + 5, 3 + 4 * i, false);
                    canvas.drawString(String.format("Krmení: %s (%d/kolo)", ag.getFoodName(), ag.getFoodAmount()), (canvas.getWidth() / 2) + 5, 4 + 4 * i, false);
                    canvas.drawString(String.format(Locale.getDefault(), "Produkuje: %s (%.2f/%d kol)", pg.getName(), pg.getProductivity(), pg.getRoundsToProduce()), (canvas.getWidth() / 2) + 5, 5 + 4 * i, false);

                    canvas.drawString(String.format("%s: %d", pg.getName(), pg.getCount()), (3 * canvas.getWidth() / 4), 2 + 4 * i, false);
                    canvas.drawString(String.format(Locale.getDefault(), "Cena: %.2f$/kus", pg.getPrice()), (3 * canvas.getWidth() / 4) + 4, 3 + 4 * i, false);
                }
            }
            case 2 -> {
                for (int i = 0; i < marketFields.size(); i++) {
                    CropField cg = marketFields.get(i);
                    canvas.drawString(String.format("%d: %s pole", i + 1, cg.getCapacity() <= 350 ? "Malé" : cg.getCapacity() >= 750 ? "Velké" : "Střední"), (canvas.getWidth() / 2) + 1, 2 + 3 * i, false);
                    canvas.drawString(String.format(Locale.getDefault(), "Cena: %.2f$", FIELD_PRICE_MULTIPLIER * cg.getCapacity()), (canvas.getWidth() / 2) + 5, 3 + 3 * i, false);
                    canvas.drawString(String.format("Kapacita: %d", cg.getCapacity()), (canvas.getWidth() / 2) + 5, 4 + 3 * i, false);
                }
            }
        }

        canvas.printCanvas();
    }

    public void drawField(ConsoleCanvas canvas) {
        canvas.clearCanvas();

        canvas.drawStringCentre(String.format("Kolo %d", round), 2, true);
        canvas.drawStringCentre(String.format("Pole %d", fields.indexOf(selectedField) + 1), 0, true);
        canvas.drawHorizontalLine('■', 1, true);
        canvas.drawHorizontalLine('■', canvas.getLastY(), true);
        canvas.drawVerticalLine('█', 0, 2, canvas.getLastY(), false);
        canvas.drawVerticalLine('█', canvas.getLastX(), 2, canvas.getLastY(), false);
        canvas.drawHorizontalLine('■', 0, canvas.getLastX(), 5, false);

        CropGroup fieldCropGroup = selectedField.getCropGroup();
        if (fieldCropGroup == null) canvas.drawString(String.format("Zaseto: Nic (0/%d)", selectedField.getCapacity()), 2, 2, true);
        else {
            canvas.drawString(String.format("Zaseto: %s (%d/%d)", fieldCropGroup.getName(), selectedField.getCropCount(), selectedField.getCapacity()), 2, 2, true);
            canvas.drawString(String.format("Zbývá kol: %d", selectedField.getRemainingRounds()), 2, 3, true);
            canvas.drawString(String.format("Očekávaný výnos: %d", (int)(selectedField.getCropCount() * fieldCropGroup.getProductivity())), 2, 4, true);
        }

        for (int y = 6; y < canvas.getLastY(); y++) {
            canvas.drawHorizontalLine('~', 2, canvas.getLastX() - 2, y, false);
            if (fieldCropGroup != null) {
                for (int x = 2; x < canvas.getLastX() - 2; x++) {
                    if (random.nextInt(100) < 10) canvas.drawPixel('|', x, y, true);
                }
            }
        }

        canvas.printCanvas();
    }

    public void updateMarket(boolean restartMarket) {
        if (restartMarket) {
            marketCropGroups.clear();
            marketCropGroups.add(new BarleyCropGroup(0));
            marketCropGroups.add(new CarrotCropGroup(0));
            marketCropGroups.add(new CornCropGroup(0));
            marketCropGroups.add(new OatCropGroup(0));
            marketCropGroups.add(new PotatoCropGroup(0));
            marketCropGroups.add(new WheatCropGroup(0));

            for (CropGroup cg : marketCropGroups) {
                cg.setCount(200 + (100 * random.nextInt(10)));

                if (cg.getCount() > 0) cg.setPrice(Math.round(100.0 * (((CROP_PRICE_MULTIPLIER * cg.getPriceMultiplier() * 100.0) / cg.getCount()) + random.nextDouble(0.1))) / 100.0);
                else cg.setPrice(0);
            }

            marketFields.clear();
            marketFields.add(new CropField(100));
            while (marketFields.size() < MARKET_FIELDS_COUNT) {
                marketFields.add(new CropField(100 + (50 * random.nextInt(19))));
            }

            marketAnimalGroups.clear();
            marketAnimalGroups.add(new CowAnimalGroup(0));
            marketAnimalGroups.add(new SheepAnimalGroup(0));
            marketAnimalGroups.add(new ChickenAnimalGroup(0));

            for (AnimalGroup ag : marketAnimalGroups) {
                ag.setCount(5 + random.nextInt(20));

                if (ag.getCount() > 0) ag.setPrice(Math.round(100.0 * ((ANIMAL_PRICE_MULTIPLIER * ag.getPriceMultiplier() * 10.0) / ag.getCount()) + random.nextDouble(1.0)) / 100.0);
                else ag.setPrice(0);

                ProductGroup pg = ag.getProduct();
                pg.setCount(15 + random.nextInt(100));
                pg.setPrice(Math.round(100.0 * ((ANIMAL_PRODUCT_PRICE_MULTIPLIER * pg.getPriceMultiplier() * 10.0) / pg.getCount()) + random.nextDouble(1.0)) / 100.0);
            }
        }

        for (CropGroup cg : marketCropGroups) {
            int variation = 10 * (random.nextInt(10) - 5);
            if (variation > 0) cg.addCount(variation, false);
            else cg.removeCount(variation);
            cg.setPrice(cg.getPrice() + (100.0 * random.nextDouble(0.1) / 100.0));
        }
        for (AnimalGroup ag : marketAnimalGroups) {
            int variation = 2 * (random.nextInt(4) - 2);
            if (variation > 0) ag.addCount(variation);
            else ag.removeCount(variation);
            ag.setPrice(ag.getPrice() + (100.0 * random.nextDouble(1.0) / 100.0));

            ProductGroup pg = ag.getProduct();
            variation = 10 * (random.nextInt(10) - 5);
            if (variation > 0) pg.addCount(variation);
            else pg.removeCount(variation);
            pg.setPrice(pg.getPrice() + (100.0 * random.nextDouble(1.0) / 100.0));
        }
    }
}
