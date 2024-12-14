package com.nesnidal.farm.crops;

public class CropGroup {
    private final String id;
    private final String name;
    private final int roundsToGrow;
    private final double productivity;
    private final double priceMultiplier;

    private int count;
    private int generatedCount;
    private double price;

    public CropGroup(String id, String name, int roundsToGrow, double productivity, double priceMultiplier) {
        this.id = id;
        this.name = name;
        this.roundsToGrow = roundsToGrow;
        this.productivity = productivity;
        this.priceMultiplier = priceMultiplier;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getRoundsToGrow() {
        return roundsToGrow;
    }

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public void addCount(int add, boolean isHarvested) {
        this.count += add;
        if (isHarvested) this.generatedCount += add;
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

    public CropGroup getClone() {
        return new CropGroup(this.getId(), this.getName(), this.getRoundsToGrow(), this.getProductivity(), this.getPriceMultiplier());
    }
}
