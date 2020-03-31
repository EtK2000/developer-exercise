package assignment.components;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

import assignment.events.InventoryAddEvent;
import assignment.events.InventoryGetItemEvent;
import assignment.events.InventoryQueryEvent;
import assignment.events.InventoryRemoveEvent;
import assignment.events.ReadDataEvent;
import assignment.events.WriteDataEvent;
import assignment.model.Inventory;
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

        // if the inventory exists, get its first item
        event.setItem(inventory != null ? inventory.getFirstItem() : Optional.empty());
    }

    // add the item to the player's inventory
    private void onAdd(InventoryAddEvent event) {
        if (event.hasItem()) {
            Inventory inventory = inventories.get(event.getPlayer().getId());

            // if needed, create the inventory with some arbitrary size
            if (inventory == null) inventories.put(event.getPlayer().getId(), inventory = new Inventory());

            event.setItem(inventory.addItem(event.getItem()));
        }
    }

    // return whether or not the item was found, for stacking items checks count
    private void onQuery(InventoryQueryEvent event) {
        Inventory inventory = inventories.get(event.getPlayer().getId());

        // if the inventory exists, query the item
        event.setFound(inventory != null && inventory.queryItem(event.getItem()));
    }

    // remove said item form the player's inventory
    private void onRemove(InventoryRemoveEvent event) {
        if (event.hasItem()) {
            Inventory inventory = inventories.get(event.getPlayer().getId());

            // if the inventory exists, attempt to remove the item
            event.setItem(inventory != null ? inventory.removeItem(event.getItem()) : Optional.empty());
        }
    }

    @Override
    public void onUnload() {
        // we might as well cleanup
        inventories.clear();
    }

    private void onRead(ReadDataEvent event) {
        try {
            inventories.clear();
            for (int i = event.getIn().readInt(); i > 0; i--) {
                Inventory inv = new Inventory();
                inventories.put(event.getIn().readUTF(), inv);
                inv.readFrom(event.getIn());
            }
        }
        catch (IOException e) {
            e.printStackTrace();// LOW: do something
        }
    }

    private void onWrite(WriteDataEvent event) {
        try {
            event.getOut().writeInt(inventories.size());
            for (Entry<String, Inventory> e : inventories.entrySet()) {
                event.getOut().writeUTF(e.getKey());
                e.getValue().writeTo(event.getOut());
            }
        }
        catch (IOException e) {
            e.printStackTrace();// LOW: do something
        }
    }
}
