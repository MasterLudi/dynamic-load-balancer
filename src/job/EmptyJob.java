package job;

import java.io.Serializable;

/**
 * Empty object passed when queue is empty
 */
public class EmptyJob implements Serializable {

    private static final long serialVersionUID = 32L;
    boolean empty = true;
}
