package assignment.components;

import assignment.events.InventoryAddEvent;
import assignment.events.OreMineEvent;
import assignment.model.Item;
import net.gameslabs.events.GiveXpEvent;
import net.gameslabs.model.Skill;

public class MyMineComponent extends PlayerComponent {
    @Override
    public void onLoad() {
        registerEvent(OreMineEvent.class, this::onMine);
    }

    // add the item to the player's inventory
    private void onMine(OreMineEvent event) {
        // you cannot mine X with your lvl
        if (getPlayerLevel(event.getPlayer(), Skill.MINING) < event.getOre().getMinMineLvl()) {
            event.setCancelled(true);// I would inline, but long lines are ew
        }

        else {
            // intern concatenation because it will be used often if multiple ores exist
            InventoryAddEvent add = new InventoryAddEvent(event.getPlayer(), new Item((event.getOre().name() + " Ore").intern()));
            send(add);
            
            // if the event has an item after being sent, the item wasn't added
            if (add.hasItem()) event.setCancelled(true);

            // only mine it if we have inventory space to add it
            else {
                send(new GiveXpEvent(event.getPlayer(), Skill.MINING, event.getOre().getMineXP()));
            }
        }
    }

    @Override
    public void onUnload() {
        // Do nothing
    }
}
