package assignment.events;

import assignment.model.Item;
import net.gameslabs.api.Player;
import net.gameslabs.api.PlayerEvent;

public class InventoryAddEvent extends PlayerEvent {
    private Item item;

    public InventoryAddEvent(Player player, Item item) {
        super(player);
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
