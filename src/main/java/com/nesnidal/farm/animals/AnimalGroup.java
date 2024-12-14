package com.nesnidal.farm.animals;

public class AnimalGroup {
    private final String id;
    private final String name;
    private final ProductGroup product;
    private final String foodName;
    private final int foodAmount;
    private final double priceMultiplier;

    private int count;
    private int lostCount;
    private double price;
    private int produceRound;

    public AnimalGroup(String id, String name, ProductGroup product, String foodName, int foodAmount, double priceMultiplier) {
        this.id = id;
        this.name = name;
        this.product = product;
        this.foodName = foodName;
        this.foodAmount = foodAmount;
        this.priceMultiplier = priceMultiplier;
    }

    public boolean produce() {
        if (produceRound < product.getRoundsToProduce() - 1) produceRound++;
        else {
            produceRound = 0;
            return true;
        }
        return false;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public void addCount(int add) {
        this.count += add;
    }
    public int removeCount(int remove) {
        int toRemove = Math.min(this.count, remove);
        this.count -= toRemove;
        this.lostCount += toRemove;

        return toRemove;
    }

    public int getLostCount() {
        return lostCount;
    }
    public void setLostCount(int lostCount) {
        this.lostCount = lostCount;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public double getPriceMultiplier() {
        return priceMultiplier;
    }

    public ProductGroup getProduct() {
        return product;
    }

    public String getFoodName() {
        return foodName;
    }
    public int getFoodAmount() {
        return foodAmount;
    }

    public AnimalGroup getClone() {
        return new AnimalGroup(this.getId(), this.getName(), this.getProduct().getClone(), this.getFoodName(), this.getFoodAmount(), this.getPriceMultiplier());
    }
}
