package com.nesnidal.farm.animals;

public class CowAnimalGroup extends AnimalGroup {
    public CowAnimalGroup(int count) {
        super("krava", "Kráva",
                new ProductGroup("mleko", "Mléko", 1, 2.0, 12.0),
                "Kukuřice", 3,
                12.0);
        this.setCount(count);
    }
}
