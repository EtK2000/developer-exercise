package assignment.events;

import assignment.model.Item;
import net.gameslabs.api.Player;
import net.gameslabs.api.PlayerEvent;

public class InventoryQueryEvent extends PlayerEvent {
    private final Item item;
    private boolean found;// defaulted to false

    public InventoryQueryEvent(Player player, Item item) {
        super(player);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public boolean found() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }
}
