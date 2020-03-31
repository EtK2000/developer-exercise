package assignment.events;

import java.io.ObjectInputStream;

import net.gameslabs.api.Event;

public class ReadDataEvent extends Event {
	private final ObjectInputStream in;

    public ReadDataEvent(ObjectInputStream in) {
        this.in = in;
    }

    public ObjectInputStream getIn() {
        return in;
    }
}
