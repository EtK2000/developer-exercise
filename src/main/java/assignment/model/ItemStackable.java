package assignment.model;

public class ItemStackable extends Item {
    // extern this so switching to other data types is ez pz
    public static final int STACK_SIZE_MAX = Integer.MAX_VALUE, STACK_SIZE_INFINITE = -1;

    // The amount if items in this stack
    // Anything negative is treated as infinity and enforced to -1
    private int count;

    public ItemStackable(String id, int count) {
        super(id);
        setCount(count);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count <= -1 ? STACK_SIZE_INFINITE : count;// enforce -1 for infinity
    }

    // Yes, I know casting both here and in super.equals is less efficient...
    // Don't @ me, JIT will fix it
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && count == ((ItemStackable) obj).count;
    }
}
