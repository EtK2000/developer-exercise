package assignment.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;

public class Inventory implements Serializable {
    private static final long serialVersionUID = 6872973361634140398L;

    private final Item[] inv;

    // holds the index of the next free slot for appending and reverse removal
    private int freeSlot;// defaults to 0

    public Inventory(int maxSize) {
        if (maxSize < 1) throw new IllegalArgumentException("inventory size must be at least 1");
        inv = new Item[maxSize];
    }

    /**
     * attempts to add the given item to the inventory
     * 
     * @return the leftover item after adding or null if added
     **/
    public Optional<Item> addItem(Item item) {
        // stackable items get added to an existing stacks if available
        if (item instanceof ItemStackable) {
            ItemStackable toAdd = (ItemStackable) item;

            // check if there are any stacks of this item type in our inventory
            for (int i = 0; i < freeSlot && toAdd.getCount() > 0; i++) {
                if (inv[i].similar(toAdd)) {
                    // add as many as we can to this stack
                    ItemStackable invStack = (ItemStackable) inv[i];

                    // add as many as we can to this stack (if we can)
                    if (invStack.getCount() != ItemStackable.STACK_SIZE_MAX) {
                        // number of available count in the stack
                        int stackFree = ItemStackable.STACK_SIZE_MAX - invStack.getCount();

                        // combined the counts are valid amounts,
                        // calculated like so to prevent underflows
                        if (stackFree >= toAdd.getCount()) {
                            invStack.setCount(invStack.getCount() + toAdd.getCount());
                            return Optional.empty(); // all done here
                        }

                        // max out the current stack,
                        // so add the rest in a new one down below
                        toAdd.setCount(toAdd.getCount() - stackFree);
                        invStack.setCount(ItemStackable.STACK_SIZE_MAX);

                        break;// we found the stack, hence there are no more
                    }
                }
            }
        }

        // stacks are full or no need to stack, so add it to the end
        // TODO: when changed to an array, add bounds check
        if (freeSlot < inv.length) {
            inv[freeSlot++] = item;
            return Optional.empty();
        }

        // return the leftover item because this inventory is full
        return Optional.of(item);
    }

    // get the first item from the inventory if there is one
    public Optional<Item> getFirstItem() {
        return freeSlot > 0 ? Optional.of(inv[0]) : Optional.empty();
    }

    // check if the item is contained, and look at count if stack
    public boolean queryItem(Item item) {
        ItemStackable stack = item instanceof ItemStackable ? (ItemStackable) item : null;

        // remove starting from the end
        for (int i = 0; i < freeSlot; i++) {
            if (inv[i].similar(item)) {
                if (stack == null) return true;// item found

                ItemStackable invStack = (ItemStackable) inv[i];

                if (invStack.getCount() >= stack.getCount()) return true;// found enough or more

                // remove from our stack, so we know how much we need to find
                stack.setCount(stack.getCount() - invStack.getCount());
            }
        }

        // we didn't find the item, or we didn't have enough of it
        return false;
    }

    // remove the slot, move holes to the end, and decrement the count
    private void removeSlot(int index) {
        inv[index] = index == freeSlot - 1 ? null : inv[freeSlot - 1];
        freeSlot--;
    }

    public Optional<Item> removeItem(Item item) {
        ItemStackable stack = item instanceof ItemStackable ? (ItemStackable) item : null;

        // remove starting from the end
        for (int i = freeSlot - 1; i >= 0; i--) {
            if (inv[i].similar(item)) {
            	//non-stackable items just need to be removed
                if (stack == null) {
                    removeSlot(i);
                    return Optional.empty();// item removed
                }

                
                // stackable items need to be decremented by the correct amount
                ItemStackable invStack = (ItemStackable) inv[i];

                if (invStack.getCount() > stack.getCount()) {
                    invStack.setCount(invStack.getCount() - stack.getCount());
                    return Optional.empty();
                }
                if (invStack.getCount() == stack.getCount()) {
                    removeSlot(i);
                    return Optional.empty();
                }

                // remove the slot, update freeSlot, and lower the remaining
                stack.setCount(stack.getCount() - invStack.getCount());
                removeSlot(i);
            }
        }

        // return what's left to remove and hasn't been found
        return Optional.of(item);
    }

    //
    // The below methods are for serialization
    //

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        freeSlot = ois.readInt();
        for (int i = 0; i < freeSlot; i++)
            inv[i] = (Item) ois.readObject();
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeInt(freeSlot);
        for (int i = 0; i < freeSlot; i++)
        	oos.writeObject(inv[i]);
    }
}
