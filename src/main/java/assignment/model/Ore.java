package assignment.model;

public enum Ore {
    COPPER(5, 1), TIN(5, 1), COAL(10, 5), IRON(20, 10), RUNE(50, 30), PHRIK(150, 50);
    
    // no need for a getter, but I'm being consistant
    private final int mineXP, minLvl;
    
    private Ore(int mineXP, int minLvl) {
        this.mineXP = mineXP;
        this.minLvl = minLvl;
    }
    
    public int getMineXP() {
        return mineXP;
    }
    
    public int getMinMineLvl() {
        return minLvl;
    }
}
