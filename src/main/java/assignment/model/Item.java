package assignment.model;

/**
 * A simple base class for containing items
 * 
 * If custom data is required, Override this class and
 * {@link Item#equals(Object)}
 **/
public class Item {
    private final String id;// could be protected, eh...

    public Item(String id) {
        this.id = id;
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
}
