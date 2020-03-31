package net.gameslabs.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;
import java.util.Map.Entry;

import assignment.model.Streamable;

public class PlayerStats implements Streamable {
    private EnumMap<Skill, Integer> xpStats;

    public PlayerStats() {
        xpStats = new EnumMap<>(Skill.class);
    }

    public PlayerStats(ObjectInputStream ois) throws IOException {
        this();
        readFrom(ois);
    }

    public void setXp(Skill skill, int xp) {
        xpStats.put(skill, xp);
    }

    public int getXp(Skill skill) {
        return xpStats.getOrDefault(skill, 0);
    }

    public void addXp(Skill skill, int xp) {
        setXp(skill, getXp(skill) + xp);
    }

    //
    // The below methods are for serialization
    //

    @Override
    public void readFrom(ObjectInputStream ois) throws IOException {
        xpStats.clear();
        for (int i = ois.readInt(); i > 0; i--)
            xpStats.put(Skill.values()[ois.readInt()], ois.readInt());
    }

    @Override
    public void writeTo(ObjectOutputStream oos) throws IOException {
        oos.writeInt(xpStats.size());
        for (Entry<Skill, Integer> e : xpStats.entrySet()) {
            oos.writeInt(e.getKey().ordinal());
            oos.writeInt(e.getValue());
        }
    }
}
