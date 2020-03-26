package assignment.components;

import java.util.HashMap;

import assignment.events.InventoryAddEvent;
import assignment.events.InventoryQueryEvent;
import assignment.events.InventoryRemoveEvent;
import assignment.model.Inventory;
import net.gameslabs.api.Component;

public class MyInventoryComponent extends Component {
    // We can't interact with anything else directly, so we store invs here
    private static final HashMap<String, Inventory> inventories = new HashMap<>();

    @Override
    public void onLoad() {
        registerEvent(InventoryAddEvent.class, this::onAdd);
        registerEvent(InventoryQueryEvent.class, this::onQuery);
        registerEvent(InventoryRemoveEvent.class, this::onRemove);
    }

    // add the item to the player's inventory
    private void onAdd(InventoryAddEvent event) {
        Inventory inventory = inventories.get(event.getPlayer().getId());

        // if needed, create the inventory with some arbitrary size
        if (inventory == null)
            inventories.put(event.getPlayer().getId(), inventory = new Inventory(32));

        event.setItem(inventory.addItem(event.getItem()));
    }

    // return whether or not the item was found, for stacking items checks count
    private void onQuery(InventoryQueryEvent event) {
        Inventory inventory = inventories.get(event.getPlayer().getId());

        // only query the item if there's an inventory
        if (inventory != null)
            event.setFound(inventory.queryItem(event.getItem()));
    }

    // remove said item form the player's inventory
    private void onRemove(InventoryRemoveEvent event) {
        Inventory inventory = inventories.get(event.getPlayer().getId());

        // only remove the item if there's an inventory
        if (inventory != null)
            event.setItem(inventory.removeItem(event.getItem()));
    }

    @Override
    public void onUnload() {
        // we might as well cleanup
        inventories.clear();
    }
}
