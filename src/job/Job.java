package job;

import java.io.Serializable;

public class Job implements Serializable {

    private static final long serialVersionUID = 42L;

    public double[] array;
    public int idx;

    public Job(int num, int idx) {
        array = new double[num];
        this.idx = idx;
    }

}
