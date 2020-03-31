package assignment.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A simple base class for containing items
 * 
 * If custom data is required, Override this class and
 * {@link Item#equals(Object)}
 **/
public class Item implements Cloneable, Streamable {
    private String id;

    public Item(String id) {
        this.id = id;
    }

    public Item(ObjectInputStream ois) throws IOException {
        readFrom(ois);
    }

    final public String getId() {
        return id;
    }
    
    // similar to equals, but cannot be overridden
    final public boolean similar(Item other) {
        return other.getClass() == getClass() && id.equals(other.id);
    }

    @Override
    public boolean equals(Object obj) {
        // items are only equal if they are the same class
        // this also functions as instanceof check
        return obj.getClass() == getClass() && id.equals(((Item) obj).id);
    }
    
    @Override
    public Item clone() {
        return new Item(id);
    }

    @Override
    public void readFrom(ObjectInputStream ois) throws IOException {
        id = ois.readUTF();
    }

    @Override
    public void writeTo(ObjectOutputStream oos) throws IOException {
        oos.writeUTF(id);
    }
}
