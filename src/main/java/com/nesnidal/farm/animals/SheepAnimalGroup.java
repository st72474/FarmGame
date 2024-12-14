package com.nesnidal.farm.animals;

public class SheepAnimalGroup extends AnimalGroup {
    public SheepAnimalGroup(int count) {
        super("ovce", "Ovce",
                new ProductGroup("vlna", "Vlna", 8, 10.0, 10.0),
                "Oves", 2,
                10.0);
        this.setCount(count);
    }
}
