package assignment.events;

import assignment.model.Ore;
import net.gameslabs.api.Player;
import net.gameslabs.api.PlayerEvent;

public class OreMineEvent extends PlayerEvent {
    private final Ore ore;

    public OreMineEvent(Player player, Ore ore) {
        super(player);
        this.ore = ore;
    }

    public Ore getOre() {
        return ore;
    }
}
