package com.nesnidal.farm.game;

public class GameSettings {
    public static final double STARTING_BALANCE = 100.0;                  //Nastaví počáteční kapitál
    public static final double CROP_PRICE_MULTIPLIER = 5.0;               //Nastaví globální násobitel ceny plodin na trhu
    public static final double ANIMAL_PRICE_MULTIPLIER = 5.0;             //Nastaví globální násobitel ceny zvířat na trhu
    public static final double ANIMAL_PRODUCT_PRICE_MULTIPLIER = 5.0;     //Nastaví globální násobitel ceny produktů zvířat na trhu
    public static final double FIELD_PRICE_MULTIPLIER = 8.0;              //Nastaví globální násobitel ceny pole na trhu
    public static final int MARKET_RESTART_ROUNDS = 20;                   //Nastavení po kolika kolech se obnovuje trh (doplnění zboží a úprava cen)
    public static final int MARKET_FIELDS_COUNT = 5;                      //Nastavení maximální počet dostupných polí na trhu
    public static final int DISASTER_START_ROUNDS = 10;                   //Nastavení po kolika kolech se mohou začít vyskytovat pohromy
    public static final double DISASTER_PROBABILITY = 0.15;               //Nastaví pravděpodobnost pohromy v každém kole (hodnoty 0 až 1)
}
