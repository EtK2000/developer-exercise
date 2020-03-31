package assignment.model;

public class ItemStackable extends Item {
    private static final long serialVersionUID = 1913017045795532694L;

    // extern this so switching to other data types is ez pz
    public static final int STACK_SIZE_MAX = Integer.MAX_VALUE;

    // The amount if items in this stack
    // Anything negative is treated as 0
    private int count;

    public ItemStackable(String id, int count) {
        super(id);
        setCount(count);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count > 0 ? count : 0;// enforce 0 for negatives
    }

    // Yes, I know casting both here and in super.equals is less efficient...
    // Don't @ me, JIT will fix it
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && count == ((ItemStackable) obj).count;
    }
    
    @Override
    public ItemStackable clone() {
        return new ItemStackable(getId(), count);
    }
}
