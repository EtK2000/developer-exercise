package net.gameslabs.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import assignment.events.DamagedByEvent;
import assignment.events.InventoryAddEvent;
import assignment.events.InventoryQueryEvent;
import assignment.events.InventoryRemoveEvent;
import assignment.events.OreMineEvent;
import assignment.events.QueryHealthEvent;
import assignment.events.ReadDataEvent;
import assignment.events.WriteDataEvent;
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
    private final Player mainPlayer, offPlayer;

    public Assignment(Component ... myComponentsToAdd) {
        registry = new ComponentRegistry();
        Arrays.asList(myComponentsToAdd).forEach(registry::registerComponent);
        registry.registerComponent(new ChartComponent());
        registry.load();
        mainPlayer = PlayerImplem.newPlayer("MyPlayer");
        offPlayer = PlayerImplem.newPlayer("Steve");
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

        // assignment 3, put coat 1 xp out of reach
        registry.sendEvent(new GiveXpEvent(mainPlayer, Skill.MINING, getXpForLevel(Ore.COAL.getMinMineLvl()) - 1));

        // assignment 4
        registry.sendEvent(new GiveXpEvent(offPlayer, Skill.DEFENSE, getXpForLevel(51)));
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("start.bin"))) {
            registry.sendEvent(new WriteDataEvent(oos));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // the other stuffz
        runChecks();
        registry.unload();
    }

    private void runChecks() {
        //////////////////
        // assignment 1 //
        //////////////////
        
        if (getLevel(Skill.EXPLORATION) != 1) throw new AssignmentFailed("Exploration XP should be set to level 1");
        if (getLevel(Skill.CONSTRUCTION) != 2) throw new AssignmentFailed("Construction XP should be set to level 2");

        //////////////////
        // assignment 2 //
        //////////////////
        
        InventoryQueryEvent query = new InventoryQueryEvent(mainPlayer, new Item("Addy Sword"));
        registry.sendEvent(query);
        if (!query.found()) throw new AssignmentFailed("What did you do with your sword?");

        query = new InventoryQueryEvent(mainPlayer, new ItemStackable("Blood Rune", 69));
        registry.sendEvent(query);
        if (!query.found()) throw new AssignmentFailed("Did you use ALL YOUR RUNES?!?!");

        registry.sendEvent(new InventoryRemoveEvent(mainPlayer, new ItemStackable("Blood Rune", 400)));
        registry.sendEvent(query);
        if (query.found()) throw new AssignmentFailed("What kinda magic is this?");

        //////////////////
        // assignment 3 //
        //////////////////
        
        OreMineEvent mineEvent = new OreMineEvent(mainPlayer, Ore.COAL);
        registry.sendEvent(mineEvent);
        if (!mineEvent.isCancelled()) throw new AssignmentFailed("Silly player, you cannot mine coal!");

        mineEvent = new OreMineEvent(mainPlayer, Ore.COPPER);
        registry.sendEvent(mineEvent);
        if (mineEvent.isCancelled()) throw new AssignmentFailed("C'mon, you can't be THAT lame...");

        mineEvent = new OreMineEvent(mainPlayer, Ore.COAL);
        registry.sendEvent(mineEvent);
        if (mineEvent.isCancelled()) throw new AssignmentFailed("You didn't level up when missing 2 XP???");

        //////////////////
        // assignment 4 //
        //////////////////
        
        QueryHealthEvent queryHealthEvent = new QueryHealthEvent(mainPlayer);
        registry.sendEvent(queryHealthEvent);
        float fullHP = queryHealthEvent.getHealth();
        if (fullHP == 0) throw new AssignmentFailed("You're already dead, you just haven't caught up yet...");

        // p2 attacks p1
        registry.sendEvent(new DamagedByEvent(mainPlayer, offPlayer));
        registry.sendEvent(queryHealthEvent);
        if (fullHP == queryHealthEvent.getHealth()) throw new AssignmentFailed("Dodging is illegal!");

        // p1 attacks back, but misses
        DamagedByEvent retaliation = new DamagedByEvent(offPlayer, mainPlayer);
        registry.sendEvent(retaliation);
        if (!retaliation.isCancelled()) throw new AssignmentFailed("You Haxx0r!");

        // p2 kills p1
        registry.sendEvent(new DamagedByEvent(mainPlayer, offPlayer));

        query = new InventoryQueryEvent(mainPlayer, new Item("Addy Sword"));
        registry.sendEvent(query);
        if (query.found()) throw new AssignmentFailed("Keepinventory is off, bro...");

        query = new InventoryQueryEvent(offPlayer, new Item("Addy Sword"));
        registry.sendEvent(query);
        if (!query.found()) throw new AssignmentFailed("Did you drop it already?");
        
        // check that end != start
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("end.bin"))) {
            registry.sendEvent(new WriteDataEvent(oos));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        // compare the files byte by byte
        try (FileInputStream start = new FileInputStream("start.bin")) {
            try (FileInputStream end = new FileInputStream("end.bin")) {
                int from_start, from_end;
                
                while ((from_start = start.read()) == (from_end = end.read()));
                
                // only true if we reached EOF in both
                if (from_start == from_end) throw new AssignmentFailed("Things have changed!");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        // attempt to load and validate the loaded data
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("start.bin"))) {
            registry.sendEvent(new ReadDataEvent(ois));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        query = new InventoryQueryEvent(mainPlayer, new Item("Addy Sword"));
        registry.sendEvent(query);
        if (!query.found()) throw new AssignmentFailed("Load, dammit!");
    }

    private int getLevel(Skill skill) {
        GetPlayerLevel getPlayerLevel = new GetPlayerLevel(mainPlayer, skill);
        registry.sendEvent(getPlayerLevel);
        return getPlayerLevel.getLevel();
    }

    private int getXpForLevel(int lvl) {
        // min_lvl - 1, because lvls start from 1
        GetXPForLevelEvent getXPForLevelEvent = new GetXPForLevelEvent(lvl - 1);
        registry.sendEvent(getXPForLevelEvent);
        return getXPForLevelEvent.getXp();
    }

    public void log(Object ... arguments) {
        System.out.println(Arrays.asList(arguments).toString());
    }
}
