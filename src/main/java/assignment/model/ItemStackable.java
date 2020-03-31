package assignment.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ItemStackable extends Item {
    // extern this so switching to other data types is ez pz
    public static final int STACK_SIZE_MAX = Integer.MAX_VALUE;

    // The amount if items in this stack
    // Anything negative is treated as 0
    private int count;

    public ItemStackable(String id, int count) {
        super(id);
        setCount(count);
    }

    public ItemStackable(ObjectInputStream ois) throws IOException {
        super(ois);// will end up calling: this.readFrom(ois)
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

    @Override
    public void readFrom(ObjectInputStream ois) throws IOException {
        super.readFrom(ois);
        count = ois.readInt();
    }

    @Override
    public void writeTo(ObjectOutputStream oos) throws IOException {
        super.writeTo(oos);
        oos.writeInt(count);
    }
}
