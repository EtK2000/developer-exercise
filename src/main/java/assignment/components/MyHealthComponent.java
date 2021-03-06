package assignment.components;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;

import assignment.events.DamagedByEvent;
import assignment.events.InventoryAddEvent;
import assignment.events.InventoryGetItemEvent;
import assignment.events.InventoryRemoveEvent;
import assignment.events.QueryHealthEvent;
import assignment.events.ReadDataEvent;
import assignment.events.WriteDataEvent;
import net.gameslabs.api.Player;
import net.gameslabs.model.Skill;

public class MyHealthComponent extends PlayerComponent {
    private static final float DMG_PER_LVL = 0.5f, ESPILON_HP = 0.01f;

    // We can't interact with anything else directly, so we store health here
    // Yes, I realize the cost of boxing and unboxing isn't worth it
    private final HashMap<String, Float> hp = new HashMap<>();

    @Override
    public void onLoad() {
        registerEvent(DamagedByEvent.class, this::onDamagedBy);
        registerEvent(QueryHealthEvent.class, this::onHealthChecked);
        
        registerEvent(ReadDataEvent.class, this::onRead);
        registerEvent(WriteDataEvent.class, this::onWrite);
    }

    private float getHealth(Player player) {
        // set player health to their max if we haven't seen it set
        return hp.getOrDefault(player.getId(), (float) getPlayerLevel(player, Skill.CONSTITUTION));
    }

    // called when another player attempts to damage us
    private void onDamagedBy(DamagedByEvent event) {
        float health = getHealth(event.getPlayer());

        // TODO: add support for weapons and armour in inventory
        float damage =
                // attacker's damage
                getPlayerLevel(event.getDamager(), Skill.ATTACK) * DMG_PER_LVL

                // defender's defense
                / getPlayerLevel(event.getPlayer(), Skill.DEFENSE);

        // hurt the defender if we can
        if (damage < ESPILON_HP) event.setCancelled(true);
        else {
            // floats can be jank sometimes, so make it a bit better
            health = health - damage;
            if (health < ESPILON_HP)
                health = 0;

            hp.put(event.getPlayer().getId(), health);

            // player has been killed, attempt to give their items to the killer
            if (health == 0) {
                InventoryGetItemEvent inventoryGetItemEvent = new InventoryGetItemEvent(event.getPlayer());
                inventoryGetItemEvent.setItem(Optional.empty());// set default, so it doesn't require other components to work

                while (true) {
                    send(inventoryGetItemEvent);

                    if (!inventoryGetItemEvent.hasItem())
                        break;// no more items to remove

                    // the arguments to events are modified, so we need to clone
                    // them
                    // ya, I know it's discouraged to modify parameters, oh
                    // well...
                    send(new InventoryRemoveEvent(event.getPlayer(), inventoryGetItemEvent.getItem().clone()));
                    send(new InventoryAddEvent(event.getDamager(), inventoryGetItemEvent.getItem()));

                    // TODO: if the damager has no inventory space, do something with extra items...
                }

                // heal the dead player fully
                hp.put(event.getPlayer().getId(), (float) getPlayerLevel(event.getPlayer(), Skill.CONSTITUTION));
            }
        }
    }

    private void onHealthChecked(QueryHealthEvent event) {
        event.setHealth(getHealth(event.getPlayer()));
    }

    @Override
    public void onUnload() {
        // we might as well cleanup
        hp.clear();
    }

    //
    // The below methods are for serialization
    //

    private void onRead(ReadDataEvent event) {
        try {
            hp.clear();
            for (int i = event.getIn().readInt(); i > 0; i--)
                hp.put(event.getIn().readUTF(), event.getIn().readFloat());
        }
        catch (IOException e) {
            e.printStackTrace();// LOW: do something
        }
    }

    private void onWrite(WriteDataEvent event) {
        try {
            event.getOut().writeInt(hp.size());
            for (Entry<String, Float> e : hp.entrySet()) {
                event.getOut().writeUTF(e.getKey());
                event.getOut().writeFloat(e.getValue());
            }
        }
        catch (IOException e) {
            e.printStackTrace();// LOW: do something
        }
    }
}
