package net.gameslabs.components;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import assignment.events.ReadDataEvent;
import assignment.events.WriteDataEvent;
import net.gameslabs.api.Component;
import net.gameslabs.api.Player;
import net.gameslabs.events.GetPlayerLevel;
import net.gameslabs.events.GetXPForLevelEvent;
import net.gameslabs.events.GiveXpEvent;
import net.gameslabs.model.PlayerStats;

public class ChartComponent extends Component {
    private static final int XP_STEP = 50;

    private Map<String, PlayerStats> persistence;

    public ChartComponent() {
        persistence = new HashMap<>();
    }

    @Override
    public void onLoad() {
        registerEvent(GetXPForLevelEvent.class, this::onGetXPForLevel);
        registerEvent(GiveXpEvent.class, this::onGiveXPToPlayer);
        registerEvent(GetPlayerLevel.class, this::onGetPlayerLevel);
        
        registerEvent(ReadDataEvent.class, this::onRead);
        registerEvent(WriteDataEvent.class, this::onWrite);
    }

    private void onGetXPForLevel(GetXPForLevelEvent event) {
        event.setXp(event.getLevel() * XP_STEP);
    }

    private void onGiveXPToPlayer(GiveXpEvent event) {
        getStats(event.getPlayer()).addXp(event.getSkill(), event.getXp());
    }

    private void onGetPlayerLevel(GetPlayerLevel event) {
        event.setLevel(getLevelFromXp(getStats(event.getPlayer()).getXp(event.getSkill())));
    }

    private int getLevelFromXp(int xp) {
        return 1 + Math.floorDiv(xp, XP_STEP);
    }

    private PlayerStats getStats(Player player) {
        return persistence.computeIfAbsent(player.getId(), p -> new PlayerStats());
    }

    @Override
    public void onUnload() {
        // we might as well cleanup
    	persistence.clear();
    }

    @SuppressWarnings("unchecked")
    private void onRead(ReadDataEvent event) {
        try {
        	persistence.clear();
            persistence = (Map<String, PlayerStats>) event.getIn().readObject();
        }
        catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();// LOW: do something
        }
    }

    private void onWrite(WriteDataEvent event) {
        try {
            event.getOut().writeObject(persistence);
        }
        catch (IOException e) {
            e.printStackTrace();// LOW: do something
        }
    }
}
