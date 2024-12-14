package com.nesnidal.farm.animals;

public class ChickenAnimalGroup extends AnimalGroup {
    public ChickenAnimalGroup(int count) {
        super("slepice", "Slepice",
                new ProductGroup("vejce", "Vejce", 1, 1.0, 10.0),
                "Pšenice", 1,
                6.0);
        this.setCount(count);
    }
}
