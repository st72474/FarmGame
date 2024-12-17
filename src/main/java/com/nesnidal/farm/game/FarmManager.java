package com.nesnidal.farm.game;

import com.nesnidal.farm.ConsoleCanvas;
import com.nesnidal.farm.animals.AnimalGroup;
import com.nesnidal.farm.animals.ProductGroup;
import com.nesnidal.farm.crops.CropField;
import com.nesnidal.farm.crops.CropGroup;
import com.nesnidal.farm.disasters.Disaster;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class FarmManager {
    private final FarmGame game;

    private ArrayList<CropField> fields = new ArrayList<>();
    private CropField selectedField = null;
    private ArrayList<CropGroup> cropGroups = new ArrayList<>();
    private ArrayList<AnimalGroup> animalGroups = new ArrayList<>();

    public FarmManager(FarmGame game) {
        this.game = game;
    }

    public void selectField(String param) {
        try {
            int fieldNum = Integer.parseInt(param) - 1;

            if (fieldNum < 0 || fieldNum + 1 > fields.size()) System.out.println("Zvolené pole neexistuje!");
            else {
                game.setGameState(GameState.STATE_FIELD);
                selectedField = fields.get(fieldNum);
                drawField();
            }
        } catch (NumberFormatException e) {
            System.out.println("Zvolené pole neexistuje!");
        }
    }

    public void plantField(String param) {
        CropGroup selectedCropGroup = null;
        for (CropGroup cg : cropGroups) {
            if (Objects.equals(cg.getId(), param)) {
                selectedCropGroup = cg;
                break;
            }
        }
        if (selectedCropGroup == null) System.out.println("Nevlastníš zvolenou plodinu!");
        else {
            if (selectedCropGroup.getCount() <= 0) System.out.println("Nemáš dostatek zvolené plodiny!");
            else if (selectedField.getCropCount() == selectedField.getCapacity()) System.out.println("Pole je již plné!");
            else if (selectedField.getCropGroup() != null && selectedField.getCropGroup() != selectedCropGroup) System.out.println("Na poli již roste jiná plodina!");
            else {
                selectedField.setCropGroup(selectedCropGroup);
                selectedField.addCropCount(selectedCropGroup.removeCount(selectedField.getCapacity() - selectedField.getCropCount()));
                drawField();
            }
        }
    }

    public void applyDisaster(Disaster disaster) {
        for (CropField cf : fields) {
            cf.setCropCount((int)(cf.getCropCount() * disaster.getMultiplier()));
            if (cf.getCropCount() == 0) {
                cf.setCropGroup(null);
                cf.resetGrow();
            } else {
                if (cf.grow()) {
                    cf.getCropGroup().addCount((int)(cf.getCropCount() * cf.getCropGroup().getProductivity()), true);
                    cf.setCropGroup(null);
                    cf.setCropCount(0);
                }
            }
        }
    }

    public void feedAnimals() {
        for (AnimalGroup ag : animalGroups) {
            CropGroup feedCropGroup = null;
            for (CropGroup cg : cropGroups) {
                if (Objects.equals(ag.getFoodName(), cg.getName())) feedCropGroup = cg;
            }

            int lostCount = 0;
            if (feedCropGroup == null) lostCount = ag.getCount();
            else {
                for (int i = 0; i < ag.getCount(); i++) {
                    if (ag.getFoodAmount() != feedCropGroup.removeCount(ag.getFoodAmount())) lostCount++;
                }
            }
            ag.removeCount(lostCount);

            if (ag.produce()) {
                ag.getProduct().addCount((int)(ag.getCount() * ag.getProduct().getProductivity()));
            }
        }
    }

    public void resetGeneratedStats() {
        for (CropGroup cg : cropGroups) {
            cg.setGeneratedCount(0);
        }
        for (AnimalGroup ag : animalGroups) {
            ag.setLostCount(0);
            ag.getProduct().setGeneratedCount(0);
        }
    }

    public void drawFarm() {
        ConsoleCanvas canvas = game.getCanvas();
        canvas.clearCanvas();

        canvas.drawStringCentre(String.format("Kolo %d", game.getRound()), 2, true);
        canvas.drawStringCentre("Farma", 0, true);
        canvas.drawHorizontalLine('■', 1, true);
        canvas.drawHorizontalLine('■', canvas.getLastY(), true);
        canvas.drawVerticalLine('█', 0, 2, canvas.getLastY(), false);
        canvas.drawVerticalLine('█', canvas.getLastX(), 2, canvas.getLastY(), false);

        canvas.drawString(String.format(Locale.getDefault(), "Kapitál: %.2f$", game.getBalance()), 2, 2, true);
        canvas.drawString(String.format("Počet polí: %d", fields.size()), 2, 3, true);
        canvas.drawString("Sklad", 2, 5, true);
        canvas.drawHorizontalLine('■', 8, canvas.getLastX(), 5, false);

        for (int i = 0; i < cropGroups.size(); i++) {
            CropGroup cg = cropGroups.get(i);
            canvas.drawString(String.format("%s: %d", cg.getName(), cg.getCount()), 2, 6 + i, false);
        }
        for (int i = 0; i < animalGroups.size(); i++) {
            AnimalGroup ag = animalGroups.get(i);
            canvas.drawString(String.format("%s: %d", ag.getProduct().getName(), ag.getProduct().getCount()), canvas.getWidth() / 4, 6 + i, false);
        }

        canvas.drawVerticalLine('█', (canvas.getWidth() / 2) - 1, 6, canvas.getLastY(), false);
        canvas.drawString(" Zvířata ", canvas.getWidth() / 2, 5, true);

        for (int i = 0; i < animalGroups.size(); i++) {
            AnimalGroup ag = animalGroups.get(i);
            ProductGroup pg = ag.getProduct();
            canvas.drawString(String.format("%s: %d", ag.getName(), ag.getCount()), (canvas.getWidth() / 2) + 1, 6 + 3 * i, false);
            canvas.drawString(String.format("Krmení: %s (%d/kolo)", ag.getFoodName(), ag.getFoodAmount()), (canvas.getWidth() / 2) + 5, 7 + 3 * i, false);
            canvas.drawString(String.format(Locale.getDefault(), "Produkuje: %s (%.2f/%d kol)", pg.getName(), pg.getProductivity(), pg.getRoundsToProduce()), (canvas.getWidth() / 2) + 5, 8 + 3 * i, false);
        }

        canvas.printCanvas();
    }

    public void drawField() {
        ConsoleCanvas canvas = game.getCanvas();
        canvas.clearCanvas();

        canvas.drawStringCentre(String.format("Kolo %d", game.getRound()), 2, true);
        canvas.drawStringCentre(String.format("Pole %d", fields.indexOf(selectedField) + 1), 0, true);
        canvas.drawHorizontalLine('■', 1, true);
        canvas.drawHorizontalLine('■', canvas.getLastY(), true);
        canvas.drawVerticalLine('█', 0, 2, canvas.getLastY(), false);
        canvas.drawVerticalLine('█', canvas.getLastX(), 2, canvas.getLastY(), false);
        canvas.drawHorizontalLine('■', 0, canvas.getLastX(), 5, false);

        CropGroup fieldCropGroup = selectedField.getCropGroup();
        if (fieldCropGroup == null) canvas.drawString(String.format("Zaseto: Nic (0/%d)", selectedField.getCapacity()), 2, 2, true);
        else {
            canvas.drawString(String.format("Zaseto: %s (%d/%d)", fieldCropGroup.getName(), selectedField.getCropCount(), selectedField.getCapacity()), 2, 2, true);
            canvas.drawString(String.format("Zbývá kol: %d", selectedField.getRemainingRounds()), 2, 3, true);
            canvas.drawString(String.format("Očekávaný výnos: %d", (int)(selectedField.getCropCount() * fieldCropGroup.getProductivity())), 2, 4, true);
        }

        for (int y = 6; y < canvas.getLastY(); y++) {
            canvas.drawHorizontalLine('~', 2, canvas.getLastX() - 2, y, false);
            if (fieldCropGroup != null) {
                for (int x = 2; x < canvas.getLastX() - 2; x++) {
                    if (game.getRandom().nextInt(100) < 10) canvas.drawPixel('|', x, y, true);
                }
            }
        }

        canvas.printCanvas();
    }

    public ArrayList<CropField> getFields() {
        return fields;
    }

    public CropField getSelectedField() {
        return selectedField;
    }
    public void setSelectedField(CropField selectedField) {
        this.selectedField = selectedField;
    }

    public ArrayList<CropGroup> getCropGroups() {
        return cropGroups;
    }
    public ArrayList<AnimalGroup> getAnimalGroups() {
        return animalGroups;
    }
}
