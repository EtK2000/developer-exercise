package net.gameslabs.model;

import java.util.Arrays;

import assignment.events.InventoryAddEvent;
import assignment.events.InventoryQueryEvent;
import assignment.events.InventoryRemoveEvent;
import assignment.events.OreMineEvent;
import assignment.model.Item;
import assignment.model.ItemStackable;
import assignment.model.Ore;
import net.gameslabs.api.Component;
import net.gameslabs.api.ComponentRegistry;
import net.gameslabs.api.Player;
import net.gameslabs.components.ChartComponent;
import net.gameslabs.events.GetPlayerLevel;
import net.gameslabs.events.GetXPForLevelEvent;
import net.gameslabs.events.GiveXpEvent;
import net.gameslabs.implem.PlayerImplem;

public class Assignment {

    protected final ComponentRegistry registry;
    private final Player mainPlayer;

    public Assignment(Component ... myComponentsToAdd) {
        registry = new ComponentRegistry();
        Arrays.asList(myComponentsToAdd).forEach(registry::registerComponent);
        registry.registerComponent(new ChartComponent());
        registry.load();
        mainPlayer = PlayerImplem.newPlayer("MyPlayer");
    }

    public final void run() {
        // assignment 1
        registry.sendEvent(new GiveXpEvent(mainPlayer, Skill.CONSTRUCTION, 25));
        registry.sendEvent(new GiveXpEvent(mainPlayer, Skill.EXPLORATION, 25));
        GetPlayerLevel getPlayerLevel = new GetPlayerLevel(mainPlayer, Skill.CONSTRUCTION);
        log("Player level", mainPlayer, getPlayerLevel.getLevel());

        // assignment 2
        registry.sendEvent(new InventoryAddEvent(mainPlayer, new Item("Addy Sword")));
        registry.sendEvent(new InventoryAddEvent(mainPlayer, new ItemStackable("Blood Rune", 420)));

        // assignment 3
        {
            // put coal just out of reach, min_lvl - 1, because 1 is a lvl
            GetXPForLevelEvent getXPForLevelEvent = new GetXPForLevelEvent(Ore.COAL.getMinMineLvl() - 1);
            registry.sendEvent(getXPForLevelEvent);
            registry.sendEvent(new GiveXpEvent(mainPlayer, Skill.MINING, getXPForLevelEvent.getXp() - 1));
        }

        // the other stuffz
        runChecks();
        registry.unload();
    }

    private void runChecks() {
        // assignment 1
        if (getLevel(Skill.EXPLORATION) != 1) throw new AssignmentFailed("Exploration XP should be set to level 1");
        if (getLevel(Skill.CONSTRUCTION) != 2) throw new AssignmentFailed("Construction XP should be set to level 2");

        //
        // assignment 2
        InventoryQueryEvent query = new InventoryQueryEvent(mainPlayer, new Item("Addy Sword"));
        registry.sendEvent(query);
        if (!query.found()) throw new AssignmentFailed("What did you do with your sword?");

        query = new InventoryQueryEvent(mainPlayer, new ItemStackable("Blood Rune", 69));
        registry.sendEvent(query);
        if (!query.found()) throw new AssignmentFailed("Did you use ALL YOUR RUNES?!?!");

        registry.sendEvent(new InventoryRemoveEvent(mainPlayer, new ItemStackable("Blood Rune", 400)));
        registry.sendEvent(query);
        if (query.found()) throw new AssignmentFailed("What kinda magic is this?");
        
        //
        // assignment 3
        OreMineEvent mineEvent = new OreMineEvent(mainPlayer, Ore.COAL);
        registry.sendEvent(mineEvent);
        if (!mineEvent.isCancelled()) throw new AssignmentFailed("Silly player, you cannot mine coal!");
        
        mineEvent = new OreMineEvent(mainPlayer, Ore.COPPER);
        registry.sendEvent(mineEvent);
        if (mineEvent.isCancelled()) throw new AssignmentFailed("C'mon, you can't be THAT lame...");
        
        mineEvent = new OreMineEvent(mainPlayer, Ore.COAL);
        registry.sendEvent(mineEvent);
        if (mineEvent.isCancelled()) throw new AssignmentFailed("You didn't level up when missing 2 XP???");
        
    }

    private int getLevel(Skill skill) {
        GetPlayerLevel getPlayerLevel = new GetPlayerLevel(mainPlayer, skill);
        registry.sendEvent(getPlayerLevel);
        return getPlayerLevel.getLevel();
    }

    public void log(Object ... arguments) {
        System.out.println(Arrays.asList(arguments).toString());
    }
}
