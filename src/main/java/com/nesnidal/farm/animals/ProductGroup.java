package com.nesnidal.farm.animals;

public class ProductGroup {
    private final String id;
    private final String name;
    private final int roundsToProduce;
    private final double productivity;
    private final double priceMultiplier;

    private int count;
    private int generatedCount;
    private double price;

    public ProductGroup(String id, String name, int roundsToProduce, double productivity, double priceMultiplier) {
        this.id = id;
        this.name = name;
        this.roundsToProduce = roundsToProduce;
        this.productivity = productivity;
        this.priceMultiplier = priceMultiplier;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getRoundsToProduce() {
        return roundsToProduce;
    }

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public void addCount(int add) {
        this.count += add;
        this.generatedCount += add;
    }
    public int removeCount(int remove) {
        int toRemove = Math.min(this.count, remove);
        this.count -= toRemove;

        return toRemove;
    }

    public int getGeneratedCount() {
        return generatedCount;
    }
    public void setGeneratedCount(int generatedCount) {
        this.generatedCount = generatedCount;
    }

    public double getProductivity() {
        return productivity;
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

    public ProductGroup getClone() {
        return new ProductGroup(this.getId(), this.getName(), this.getRoundsToProduce(), this.getProductivity(), this.getPriceMultiplier());
    }
}
