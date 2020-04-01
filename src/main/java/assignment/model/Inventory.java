package assignment.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class Inventory implements Streamable {
    private static final byte ITEM_SINGLE = 0, ITEM_STACKABLE = 1;
    
    private final ArrayList<Item> inv = new ArrayList<>();

    /**
     * attempts to add the given item to the inventory
     **/
    public void addItem(Item item) {
        // stackable items get added to an existing stacks if available
        if (item instanceof ItemStackable) {
            ItemStackable toAdd = (ItemStackable) item;

            // check if there are any stacks of this item type in our inventory,
            // and add as many as we can into existing stacks
            inv.stream().filter(it -> it.similar(item) && ((ItemStackable) it).getCount() < ItemStackable.STACK_SIZE_MAX)
                .forEach(it -> {
                    // add as many as we can to this stack
                    ItemStackable invStack = (ItemStackable) it;

                    // add as many as we can to this stack
                    // number of available count in the stack
                    int stackFree = ItemStackable.STACK_SIZE_MAX - invStack.getCount();

                    // combined the counts are valid amounts, calculated like so to prevent underflows
                    if (stackFree >= toAdd.getCount()) invStack.setCount(invStack.getCount() + toAdd.getCount());

                    // max out the current stack, so add the rest in a new one down below
                    else {
                        toAdd.setCount(toAdd.getCount() - stackFree);
                        invStack.setCount(ItemStackable.STACK_SIZE_MAX);
                    }
                });
            
            // if we've depleted our stack to add we're already done
            if (toAdd.getCount() == 0)
                return;
        }

        // stacks are full or no need to stack, so add remainder to the end
        inv.add(item);
    }

    // get the first item from the inventory if there is one
    public Optional<Item> getFirstItem() {
        return inv.size() > 0 ? Optional.of(inv.get(0)) : Optional.empty();
    }

    // check if the item is contained, and look at count if stack
    public boolean queryItem(Item item) {
        if (!(item instanceof ItemStackable))
            return inv.stream().filter(it -> it.similar(item)).findFirst().isPresent();

        // sum all counts of the stack in the inventory, and see if they're enough
        return inv.stream().filter(it -> it.similar(item))
                .collect(Collectors.summarizingLong(it -> ((ItemStackable) it).getCount()))
                .getSum() >= ((ItemStackable) item).getCount();
    }

    public Optional<Item> removeItem(Item item) {
        if (!(item instanceof ItemStackable)) {
            int index = inv.lastIndexOf(item);
            
            if (index > -1) {
                inv.remove(index);
                return Optional.empty();// item removed
            }
            
            return Optional.of(item);// item not found
        }
        
        ItemStackable stack = (ItemStackable) item;

        // remove starting from the end
        for (int i = inv.size() - 1; i >= 0; i--) {
            if (inv.get(i).similar(item)) {
                // stackable items need to be decremented by the correct amount
                ItemStackable invStack = (ItemStackable) inv.get(i);

                // the found inventory stack has over enough
                if (invStack.getCount() > stack.getCount()) {
                    invStack.setCount(invStack.getCount() - stack.getCount());
                    return Optional.empty();
                }
                
                // the found inventory stack has the exact amount, and must be removed
                if (invStack.getCount() == stack.getCount()) {
                    inv.remove(i);
                    return Optional.empty();
                }

                // the found inventory slot doesn't have enough, so remove the slot and lower the remaining
                stack.setCount(stack.getCount() - invStack.getCount());
                inv.remove(i);
            }
        }

        // return what's left to remove and hasn't been found
        return Optional.of(item);
    }

    //
    // The below methods are for serialization
    //

    @Override
    public void readFrom(ObjectInputStream ois) throws IOException {
        for (int i = ois.readInt(); i > 0; i--) {
            switch (ois.readByte()) {
                case ITEM_STACKABLE:
                    inv.add(new ItemStackable(ois));
                    break;
                case ITEM_SINGLE:
                     inv.add(new Item(ois));
                    break;
                default:
                    throw new IOException("invalid item type");
            }
        }
    }

    @Override
    public void writeTo(ObjectOutputStream oos) throws IOException {
        oos.writeInt(inv.size());
        for (Item item : inv) {
            if (item instanceof ItemStackable) oos.writeByte(ITEM_STACKABLE);
            else oos.writeByte(ITEM_SINGLE);
            
            item.writeTo(oos);
        }
    }
}
