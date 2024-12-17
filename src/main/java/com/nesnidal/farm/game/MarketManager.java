package com.nesnidal.farm.game;

import com.nesnidal.farm.ConsoleCanvas;
import com.nesnidal.farm.animals.*;
import com.nesnidal.farm.crops.*;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MarketManager {
    public static final int DEFAULT_PAGE = 2;
    private final FarmGame game;

    private ArrayList<CropGroup> marketCropGroups = new ArrayList<>();
    private ArrayList<AnimalGroup> marketAnimalGroups = new ArrayList<>();
    private ArrayList<CropField> marketFields = new ArrayList<>();
    private int marketPage = DEFAULT_PAGE;

    public MarketManager(FarmGame game) {
        this.game = game;
    }

    public void sell(String param1, String param2) {
        switch (marketPage) {
            case 0 -> {
                try {
                    String sellId = param1;
                    CropGroup sellCropGroup = null;
                    for (CropGroup cg : game.getFarmManager().getCropGroups()) {
                        if (Objects.equals(cg.getId(), sellId)) sellCropGroup = cg;
                    }

                    if (sellCropGroup == null) System.out.println("Zvolený produkt není platný!");
                    else {
                        int sellCount;
                        if (Objects.equals(param2, "vse")) sellCount = sellCropGroup.getCount();
                        else sellCount = Integer.parseInt(param2);

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
                                game.setBalance(game.getBalance() + sellPrice);

                                System.out.printf(Locale.getDefault(), "Prodej: %d %s\t\tCelkový výdělek: %.2f$\n", sellCountReal, marketCropGroup.getName(), sellPrice);

                                drawMarket();
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Zvolený počet není platný!");
                }
            }
            case 1 -> {
                try {
                    String sellId = param1;
                    ProductGroup sellProductGroup = null;
                    for (AnimalGroup ag : game.getFarmManager().getAnimalGroups()) {
                        if (Objects.equals(ag.getProduct().getId(), sellId)) sellProductGroup = ag.getProduct();
                    }

                    if (sellProductGroup == null) System.out.println("Zvolený produkt není platný!");
                    else {
                        int sellCount;
                        if (Objects.equals(param2, "vse")) sellCount = sellProductGroup.getCount();
                        else sellCount = Integer.parseInt(param2);

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
                                game.setBalance(game.getBalance() + sellPrice);

                                System.out.printf(Locale.getDefault(), "Prodej: %d %s\t\tCelkový výdělek: %.2f$\n", sellCountReal, sellProductGroup.getName(), sellPrice);
                                drawMarket();
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

    public void buy(String param1, String param2) {
        switch (marketPage) {
            case 0 -> {
                try {
                    int buyCount = Integer.parseInt(param2);
                    String buyId = param1;

                    CropGroup marketCropGroup = null;
                    for (CropGroup cg : marketCropGroups) {
                        if (Objects.equals(cg.getId(), buyId)) marketCropGroup = cg;
                    }

                    if (marketCropGroup == null) System.out.println("Zvolený produkt není platný!");
                    else {
                        CropGroup buyCropGroup = null;
                        for (CropGroup cg : game.getFarmManager().getCropGroups()) {
                            if (Objects.equals(cg.getId(), buyId)) buyCropGroup = cg;
                        }

                        int buyCountReal = marketCropGroup.removeCount(buyCount);
                        double buyPrice = buyCountReal * marketCropGroup.getPrice();
                        if (buyCountReal == 0) System.out.println("Trh nemá dostek této plodiny!");
                        else if (buyPrice > game.getBalance()) {
                            System.out.println("Nemáš dostatek mincí na tento nákup!");
                            marketCropGroup.addCount(buyCountReal, false);
                        }
                        else {
                            if (buyCountReal < buyCount) System.out.printf("Trh nemá dostek této plodiny! Koupeno pouze: %d\n", buyCountReal);

                            if (buyCropGroup == null) {
                                buyCropGroup = marketCropGroup.getClone();
                                game.getFarmManager().getCropGroups().add(buyCropGroup);
                            }
                            buyCropGroup.addCount(buyCountReal, false);
                            game.setBalance(game.getBalance() - buyPrice);

                            System.out.printf(Locale.getDefault(), "Nákup: %d %s\t\tCelková cena: %.2f$\n", buyCountReal, buyCropGroup.getName(), buyPrice);
                            drawMarket();
                        }
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Zvolený počet není platný!");
                }
            }
            case 1 ->{
                try {
                    int buyCount = Integer.parseInt(param2);
                    String buyId = param1;

                    AnimalGroup marketAnimalGroup = null;
                    for (AnimalGroup ag : marketAnimalGroups) {
                        if (Objects.equals(ag.getId(), buyId)) marketAnimalGroup = ag;
                    }

                    if (marketAnimalGroup == null) System.out.println("Zvolený produkt není platný!");
                    else {
                        AnimalGroup buyAnimalGroup = null;
                        for (AnimalGroup ag : game.getFarmManager().getAnimalGroups()) {
                            if (Objects.equals(ag.getId(), buyId)) buyAnimalGroup = ag;
                        }

                        int buyCountReal = marketAnimalGroup.removeCount(buyCount);
                        double buyPrice = buyCountReal * marketAnimalGroup.getPrice();
                        if (buyCountReal == 0) System.out.println("Trh nemá dostek těchto zvířat!");
                        else if (buyPrice > game.getBalance()) {
                            System.out.println("Nemáš dostatek mincí na tento nákup!");
                            marketAnimalGroup.addCount(buyCountReal);
                        }
                        else {
                            if (buyCountReal < buyCount) System.out.printf("Trh nemá dostek těchto zvířat! Koupeno pouze: %d\n", buyCountReal);

                            if (buyAnimalGroup == null) {
                                buyAnimalGroup = marketAnimalGroup.getClone();
                                buyAnimalGroup.getProduct().setCount(0);
                                game.getFarmManager().getAnimalGroups().add(buyAnimalGroup);
                            }
                            buyAnimalGroup.addCount(buyCountReal);
                            game.setBalance(game.getBalance() - buyPrice);

                            System.out.printf(Locale.getDefault(), "Nákup: %d %s\t\tCelková cena: %.2f$\n", buyCountReal, buyAnimalGroup.getName(), buyPrice);
                            drawMarket();
                        }
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Zvolený počet není platný!");
                }
            }
            case 2 -> {
                if (!Objects.equals(param1, "pole")) System.out.println("Tady můžeš koupit pouze pole!");
                try {
                    int fieldNum = Integer.parseInt(param2) - 1;
                    CropField buyField = marketFields.get(fieldNum);
                    if (buyField == null) System.out.println("Zvolené pole není platné!");
                    else if (GameSettings.FIELD_PRICE_MULTIPLIER * buyField.getCapacity() > game.getBalance()) System.out.println("Nemáš dostatek mincí na tento nákup!");
                    else {
                        double buyPrice = GameSettings.FIELD_PRICE_MULTIPLIER * buyField.getCapacity();
                        game.getFarmManager().getFields().add(new CropField(buyField.getCapacity()));
                        marketFields.remove(fieldNum);
                        game.setBalance(game.getBalance() - buyPrice);

                        System.out.printf(Locale.getDefault(), "Nákup pole\t\tCelková cena: %.2f$\n", buyPrice);
                        drawMarket();
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Zvolené pole není platné!");
                }
            }
        }
    }

    public void updateMarket(boolean restartMarket) {
        Random random = game.getRandom();

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

                if (cg.getCount() > 0) cg.setPrice(Math.round(100.0 * (((GameSettings.CROP_PRICE_MULTIPLIER * cg.getPriceMultiplier() * 100.0) / cg.getCount()) + random.nextDouble(0.1))) / 100.0);
                else cg.setPrice(0);
            }

            marketFields.clear();
            marketFields.add(new CropField(100));
            while (marketFields.size() < GameSettings.MARKET_FIELDS_COUNT) {
                marketFields.add(new CropField(100 + (50 * random.nextInt(19))));
            }

            marketAnimalGroups.clear();
            marketAnimalGroups.add(new CowAnimalGroup(0));
            marketAnimalGroups.add(new SheepAnimalGroup(0));
            marketAnimalGroups.add(new ChickenAnimalGroup(0));

            for (AnimalGroup ag : marketAnimalGroups) {
                ag.setCount(5 + random.nextInt(20));

                if (ag.getCount() > 0) ag.setPrice(Math.round(100.0 * ((GameSettings.ANIMAL_PRICE_MULTIPLIER * ag.getPriceMultiplier() * 10.0) / ag.getCount()) + random.nextDouble(1.0)) / 100.0);
                else ag.setPrice(0);

                ProductGroup pg = ag.getProduct();
                pg.setCount(15 + random.nextInt(100));
                pg.setPrice(Math.round(100.0 * ((GameSettings.ANIMAL_PRODUCT_PRICE_MULTIPLIER * pg.getPriceMultiplier() * 10.0) / pg.getCount()) + random.nextDouble(1.0)) / 100.0);
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

    public void drawMarket() {
        ConsoleCanvas canvas = game.getCanvas();
        canvas.clearCanvas();

        canvas.drawString(String.format("Kolo %d", game.getRound()), (canvas.getWidth() / 4) + 10, 2, true);
        canvas.drawStringCentre(String.format("Trh %d/3", marketPage + 1), 0, true);
        canvas.drawHorizontalLine('■', 1, true);
        canvas.drawHorizontalLine('■', canvas.getLastY(), true);
        canvas.drawVerticalLine('█', 0, 2, canvas.getLastY(), false);
        canvas.drawVerticalLine('█', canvas.getLastX(), 2, canvas.getLastY(), false);

        canvas.drawString(String.format(Locale.getDefault(), "Kapitál: %.2f$", game.getBalance()), 2, 2, true);
        canvas.drawString(String.format("Počet polí: %d", game.getFarmManager().getFields().size()), 2, 3, true);
        canvas.drawString("Sklad", 2, 5, true);
        canvas.drawHorizontalLine('■', 8, (canvas.getWidth() / 2) - 2, 5, false);
        for (int i = 0; i <  game.getFarmManager().getCropGroups().size(); i++) {
            CropGroup cg = game.getFarmManager().getCropGroups().get(i);
            canvas.drawString(String.format("%s: %d", cg.getName(), cg.getCount()), 2, 6 + i, false);
        }
        for (int i = 0; i < game.getFarmManager().getAnimalGroups().size(); i++) {
            AnimalGroup ag = game.getFarmManager().getAnimalGroups().get(i);
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
                    canvas.drawString(String.format(Locale.getDefault(), "Cena: %.2f$", GameSettings.FIELD_PRICE_MULTIPLIER * cg.getCapacity()), (canvas.getWidth() / 2) + 5, 3 + 3 * i, false);
                    canvas.drawString(String.format("Kapacita: %d", cg.getCapacity()), (canvas.getWidth() / 2) + 5, 4 + 3 * i, false);
                }
            }
        }

        canvas.printCanvas();
    }

    public ArrayList<CropGroup> getCropGroups() {
        return marketCropGroups;
    }
    public ArrayList<AnimalGroup> getAnimalGroups() {
        return marketAnimalGroups;
    }
    public ArrayList<CropField> getFields() {
        return marketFields;
    }

    public int getPage() {
        return marketPage;
    }
    public void setPage(int marketPage) {
        this.marketPage = marketPage;
    }
    public void nextPage() {
        if (marketPage < DEFAULT_PAGE) marketPage++;
        else marketPage = 0;
    }
}
