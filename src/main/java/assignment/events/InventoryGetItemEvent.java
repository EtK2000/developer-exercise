package assignment.events;

import assignment.model.Item;
import net.gameslabs.api.Player;
import net.gameslabs.api.PlayerEvent;

public class InventoryGetItemEvent extends PlayerEvent {
    private Item item;

    public InventoryGetItemEvent(Player player) {
        super(player);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
