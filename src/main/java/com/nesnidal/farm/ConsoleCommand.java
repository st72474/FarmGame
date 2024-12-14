package com.nesnidal.farm;

import java.util.Scanner;

public class ConsoleCommand {
    public static final String CMD_HELP = "pomoc";
    public static final String CMD_FARM = "farma";
    public static final String CMD_FARM_FIELD = "pole";
    public static final String CMD_FARM_FIELD_PLANT = "zaset";
    public static final String CMD_MARKET = "trh";
    public static final String CMD_SELL = "prodat";
    public static final String CMD_BUY = "koupit";
    public static final String CMD_SLEEP = "spat";
    public static final String CMD_END = "konec";
    public static final String CMD_RST = "reset";

    private final Scanner scanner;
    private String[] lastCommand;

    public ConsoleCommand() {
        this.scanner = new Scanner(System.in);
    }

    public void nextCommand() {
        System.out.print("> ");
        lastCommand = scanner.nextLine().split(" ");
    }

    public String getName() {
        return lastCommand != null && lastCommand.length > 0 ? lastCommand[0] : "";
    }
    public String getParam1() {
        return lastCommand != null && lastCommand.length > 1 ? lastCommand[1] : "";
    }
    public String getParam2() {
        return lastCommand != null && lastCommand.length > 2 ? lastCommand[2] : "";
    }

    public void drawHelp(ConsoleCanvas canvas) {
        canvas.clearCanvas();

        canvas.drawString("Nápověda", (canvas.getWidth() - 8) / 2, 0, true);
        canvas.drawHorizontalLine('■', 1, true);
        canvas.drawHorizontalLine('■', canvas.getLastY(), true);
        canvas.drawVerticalLine('█', 0, 2, canvas.getLastY(), false);
        canvas.drawVerticalLine('█', canvas.getLastX(), 2, canvas.getLastY(), false);

        canvas.drawString(String.format("> %s", CMD_HELP), 2, 2, true);
        canvas.drawString("Zobrazí nápovědu", 50, 2, true);

        canvas.drawString(String.format("> %s", CMD_FARM), 2, 3, true);
        canvas.drawString("Přesune hráče na farmu", 50, 3, true);

        canvas.drawString(String.format("> %s <číslo>", CMD_FARM_FIELD), 2, 4, true);
        canvas.drawString("Přesune hráče na zvolené pole", 50, 4, true);

        canvas.drawString(String.format("> %s <plodina>", CMD_FARM_FIELD_PLANT), 2, 5, true);
        canvas.drawString("Zaseje zvolenou plodinu na pole, kde se hráč nachází", 50, 5, true);

        canvas.drawString(String.format("> %s", CMD_MARKET), 2, 6, true);
        canvas.drawString("Přesune hráče na trh nebo mezi jednotlivými částmi trhu.", 50, 6, true);

        canvas.drawString(String.format("> %s <položka> <počet>/vse", CMD_SELL), 2, 7, true);
        canvas.drawString("Prodá zvolený počet (nebo vše) položek", 50, 7, true);

        canvas.drawString(String.format("> %s <položka> <počet>", CMD_BUY), 2, 8, true);
        canvas.drawString("Koupí zvolený počet položek", 50, 8, true);

        canvas.drawString(String.format("> %s pole <číslo>", CMD_BUY), 2, 9, true);
        canvas.drawString("Koupí zvolené pole", 50, 9, true);

        canvas.drawString(String.format("> %s", CMD_SLEEP), 2, 10, true);
        canvas.drawString("Ukončí kolo", 50, 10, true);

        canvas.drawString(String.format("> %s", CMD_END), 2, 11, true);
        canvas.drawString("Ukončí a zavře hru", 50, 11, true);

        canvas.drawString(String.format("> %s", CMD_RST), 2, 12, true);
        canvas.drawString("Restartuje celou hru. Vše bude vráceno do výchozího nastavení.", 50, 12, true);

        canvas.printCanvas();
    }
}
