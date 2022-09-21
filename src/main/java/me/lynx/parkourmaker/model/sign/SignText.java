package me.lynx.parkourmaker.model.sign;

import me.lynx.parkourmaker.ParkourMakerPlugin;
import me.lynx.parkourmaker.model.map.ParkourMap;

public class SignText {

    private String lineOne;
    private String lineTwo;
    private String lineThree;
    private String lineFour;
    private ParkourMap owningMap;

    public SignText(ParkourMap owningMap) {
        this.owningMap = owningMap;
    }

    public SignText() {}

    public ParkourMap getOwningMap() {
        return owningMap;
    }

    public void setOwningMap(ParkourMap owningMap) {
        this.owningMap = owningMap;
    }

    public boolean isEmpty() {
        return lineOne == null && lineTwo == null && lineThree == null && lineFour == null;
    }

    public String getLine(int line) {
        switch (line) {
            case 1:
                return lineOne;
            case 2:
                return lineTwo;
            case 3:
                return lineThree;
            case 4:
                return lineFour;
            default:
                return null;
        }
    }

    public void setLine(int line, String text, boolean save) {
        if (text.isEmpty()) text = null;
        if (line > 0 && line < 5 && save) {
            ParkourMakerPlugin.instance().getStorage().addSignText(owningMap.getName(), line, text);
        }

        switch (line) {
            case 1:
                lineOne = text;
                break;
            case 2:
                lineTwo = text;
                break;
            case 3:
                lineThree = text;
                break;
            case 4:
                lineFour = text;
                break;
            default:
                break;
        }
    }

}