
package climateControl.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Zeno410
 */
public class IntStreamer extends Streamer<Integer> {

    public Integer readFrom(DataInput input) throws IOException {
        return input.readInt();
    }

    public void writeTo(Integer written, DataOutput output) throws IOException {
        output.writeInt(written);
    }

}
