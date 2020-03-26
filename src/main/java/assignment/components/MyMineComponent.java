package assignment.components;

import assignment.events.InventoryAddEvent;
import assignment.events.OreMineEvent;
import assignment.model.Item;
import net.gameslabs.api.Component;
import net.gameslabs.events.GetPlayerLevel;
import net.gameslabs.events.GiveXpEvent;
import net.gameslabs.model.Skill;

public class MyMineComponent extends Component {
    @Override
    public void onLoad() {
        registerEvent(OreMineEvent.class, this::onMine);
    }

    // add the item to the player's inventory
    private void onMine(OreMineEvent event) {
        GetPlayerLevel getPlayerLevel = new GetPlayerLevel(event.getPlayer(), Skill.MINING);
        send(getPlayerLevel);
        
        if (getPlayerLevel.getLevel() < event.getOre().getMinMineLvl())
            event.setCancelled(true);// you cannot mine X with low lvl
        
        else {
            // intern concatenation because it will be used often if multiple ores exist
            InventoryAddEvent add = new InventoryAddEvent(event.getPlayer(), new Item((event.getOre().name() + " Ore").intern()));
            send(add);
            if (add.getItem() != null)
                event.setCancelled(true);
            
            // only mine it if we have inventory space to add it
            else
                send(new GiveXpEvent(event.getPlayer(), Skill.MINING, event.getOre().getMineXP()));
        }
    }

    @Override
    public void onUnload() {
        // Do nothing
    }
}