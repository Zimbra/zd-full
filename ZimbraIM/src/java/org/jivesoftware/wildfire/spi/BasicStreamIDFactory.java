/*
 * 
 */
package org.jivesoftware.wildfire.spi;

import org.jivesoftware.wildfire.StreamID;
import org.jivesoftware.wildfire.StreamIDFactory;
import java.util.Random;

/**
 * A basic stream ID factory that produces id's using java.util.Random
 * and a simple hex representation of a random int.
 *
 * @author Iain Shigeoka
 */
public class BasicStreamIDFactory implements StreamIDFactory {

    /**
     * The random number to use, someone with Java can predict stream IDs if they can guess the current seed *
     */
    Random random = new Random();

    public StreamID createStreamID() {
        return new BasicStreamID(Integer.toHexString(random.nextInt()));
    }

    public StreamID createStreamID(String name) {
        return new BasicStreamID(name);
    }

    private class BasicStreamID implements StreamID {
        String id;

        public BasicStreamID(String id) {
            this.id = id;
        }

        public String getID() {
            return id;
        }

        public String toString() {
            return id;
        }

        public int hashCode() {
            return id.hashCode();
        }
    }
}
