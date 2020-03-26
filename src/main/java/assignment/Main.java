package assignment;

import assignment.components.MyHealthComponent;
import assignment.components.MyInventoryComponent;
import assignment.components.MyMineComponent;
import assignment.components.MyXPBoosterComponent;
import net.gameslabs.model.Assignment;

public class Main {
    public static void main(String[] args) {
        new Assignment(
            // Assignment 1
            new MyXPBoosterComponent(),
            
            // Assignment 2
            new MyInventoryComponent(),
            
            // Assignment 3
            new MyMineComponent(),// lol, it's my component which is mine!
            
            // Assignment 4
            new MyHealthComponent()
        ).run();
    }
}
