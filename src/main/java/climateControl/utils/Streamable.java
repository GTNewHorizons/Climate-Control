package climateControl.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Zeno410
 */
public interface Streamable {

    abstract public void readFrom(DataInput input) throws IOException;

    abstract public void writeTo(DataOutput output) throws IOException;
}
