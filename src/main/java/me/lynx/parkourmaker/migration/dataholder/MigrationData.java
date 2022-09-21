package me.lynx.parkourmaker.migration.dataholder;

import java.util.ArrayList;
import java.util.List;

public class MigrationData {

    private String propertyKey;
    private String legacyKey;
    private String propertyValue;
    private boolean isStringValue;
    private boolean hasChildren;
    private List<MigrationData> childData;

    public MigrationData(String propertyKey) {
        this.propertyKey = propertyKey;
        hasChildren = false;
        childData = new ArrayList<>();
    }

    public void setLegacyKey(String legacyKey) {
        this.legacyKey = legacyKey;
    }

    public List<MigrationData> getChildData() {
        return childData;
    }

    public void addChildData(MigrationData childData) {
        this.childData.add(childData);
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public void setStringValue(boolean stringValue) {
        isStringValue = stringValue;
    }

    /**
     * It will return the latest key.
     */
    public String getPropertyKey() {
        return propertyKey;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public boolean isStringValue() {
        return isStringValue;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

}