package assignment;

import assignment.components.MyInventoryComponent;
import assignment.components.MyXPBoosterComponent;
import net.gameslabs.model.Assignment;

public class Main {

    public static void main(String[] args) {
        new Assignment(
            // Assignment 1
            new MyXPBoosterComponent(),
            
            // Assignment 2
            new MyInventoryComponent()
        ).run();
    }
}
