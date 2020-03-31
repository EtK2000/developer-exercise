package assignment.events;

import java.io.ObjectOutputStream;

import net.gameslabs.api.Event;

public class WriteDataEvent extends Event {
	private final ObjectOutputStream out;

    public WriteDataEvent(ObjectOutputStream out) {
        this.out = out;
    }

    public ObjectOutputStream getOut() {
        return out;
    }
}
