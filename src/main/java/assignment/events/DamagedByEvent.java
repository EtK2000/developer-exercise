package assignment.events;

import net.gameslabs.api.Player;
import net.gameslabs.api.PlayerEvent;

public class DamagedByEvent extends PlayerEvent {
    private final Player damager;

    public DamagedByEvent(Player player, Player damager) {
        super(player);
        this.damager = damager;
    }

    public Player getDamager() {
        return damager;
    }
}
