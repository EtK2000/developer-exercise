package assignment.components;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import assignment.events.InventoryAddEvent;
import assignment.events.InventoryGetItemEvent;
import assignment.events.InventoryQueryEvent;
import assignment.events.InventoryRemoveEvent;
import assignment.events.ReadDataEvent;
import assignment.events.WriteDataEvent;
import assignment.model.Inventory;
import assignment.model.Item;
import net.gameslabs.api.Component;

public class MyInventoryComponent extends Component {
    // We can't interact with anything else directly, so we store invs here
    private final HashMap<String, Inventory> inventories = new HashMap<>();

    @Override
    public void onLoad() {
        registerEvent(InventoryAddEvent.class, this::onAdd);
        registerEvent(InventoryGetItemEvent.class, this::onQueryFirstItem);
        registerEvent(InventoryQueryEvent.class, this::onQuery);
        registerEvent(InventoryRemoveEvent.class, this::onRemove);

        registerEvent(ReadDataEvent.class, this::onRead);
        registerEvent(WriteDataEvent.class, this::onWrite);
    }

    private void onQueryFirstItem(InventoryGetItemEvent event) {
        Inventory inventory = inventories.get(event.getPlayer().getId());

        // if no item or no inventory return empty
        if (inventory == null) event.setItem(Optional.empty());
        else {
            Optional<Item> item = inventory.getFirstItem();
            if (!item.isPresent()) event.setCancelled(true);

            event.setItem(item);
        }
    }

    // add the item to the player's inventory
    private void onAdd(InventoryAddEvent event) {
        if (event.hasItem()) {
            Inventory inventory = inventories.get(event.getPlayer().getId());

            // if needed, create the inventory with some arbitrary size
            if (inventory == null) inventories.put(event.getPlayer().getId(), inventory = new Inventory(32));

            event.setItem(inventory.addItem(event.getItem()));
        }
    }

    // return whether or not the item was found, for stacking items checks count
    private void onQuery(InventoryQueryEvent event) {
        Inventory inventory = inventories.get(event.getPlayer().getId());

        // only query the item if there's an inventory
        if (inventory == null) event.setCancelled(true);
        else event.setFound(inventory.queryItem(event.getItem()));
    }

    // remove said item form the player's inventory
    private void onRemove(InventoryRemoveEvent event) {
        if (event.hasItem()) {
            Inventory inventory = inventories.get(event.getPlayer().getId());

            // only remove the item if there's an inventory
            if (inventory == null) event.setCancelled(true);
            else event.setItem(inventory.removeItem(event.getItem()));
        }
    }

    @Override
    public void onUnload() {
        // we might as well cleanup
        inventories.clear();
    }

    @SuppressWarnings("unchecked")
    private void onRead(ReadDataEvent event) {
        try {
            inventories.clear();
            inventories.putAll((HashMap<String, Inventory>) event.getIn().readObject());
        }
        catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();// LOW: do something
        }
    }

    private void onWrite(WriteDataEvent event) {
        try {
            event.getOut().writeObject(inventories);
        }
        catch (IOException e) {
            e.printStackTrace();// LOW: do something
        }
    }
}
