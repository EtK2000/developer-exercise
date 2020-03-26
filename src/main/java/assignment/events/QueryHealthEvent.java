package assignment.events;

import net.gameslabs.api.Player;
import net.gameslabs.api.PlayerEvent;

public class QueryHealthEvent extends PlayerEvent {
    private float health;

    public QueryHealthEvent(Player player) {
        super(player);
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }
}
