package assignment.events;

import java.util.Optional;

import assignment.model.Item;
import net.gameslabs.api.Player;
import net.gameslabs.api.PlayerEvent;

public class InventoryAddEvent extends PlayerEvent {
    private Optional<Item> item;

    public InventoryAddEvent(Player player, Item item) {
        super(player);
        this.item = Optional.of(item);
    }

    public boolean hasItem() {
        return item.isPresent();
    }

    public Item getItem() {
        return item.get();
    }

    public void setItem(Optional<Item> item) {
        this.item = item;
    }
}
