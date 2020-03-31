package assignment.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface Streamable {
    void readFrom(ObjectInputStream ois) throws IOException;

    void writeTo(ObjectOutputStream oos) throws IOException;
}
