package com.nesnidal.farm.crops;

public class CropField {
    private final int capacity;

    private CropGroup cropGroup;
    private int cropCount;
    private int growRound;

    public CropField(int capacity) {
        this.capacity = capacity;
    }

    public boolean grow() {
        if (cropGroup != null) {
            if (growRound < cropGroup.getRoundsToGrow() - 1) growRound++;
            else {
                growRound = 0;
                return true;
            }
        }
        return false;
    }
    public void resetGrow() {
        this.growRound = 0;
    }

    public int getCapacity() {
        return capacity;
    }

    public CropGroup getCropGroup() {
        return cropGroup;
    }
    public void setCropGroup(CropGroup cropGroup) {
        this.cropGroup = cropGroup;
    }

    public int getCropCount() {
        return cropCount;
    }
    public void setCropCount(int cropCount) {
        this.cropCount = cropCount;
    }
    public void addCropCount(int add) {
        this.cropCount += add;
    }

    public int getRemainingRounds() {
        if (cropGroup != null) {
            return cropGroup.getRoundsToGrow() - growRound;
        }
        return 0;
    }
}
