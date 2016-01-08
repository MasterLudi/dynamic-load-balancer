package job;

import java.util.concurrent.LinkedBlockingQueue;

public class JobQueue<J> {

    private LinkedBlockingQueue<J> queue;

    public JobQueue() {
        queue = new LinkedBlockingQueue<>();
    }

    public J pop() {
        return queue.poll();
    }

    public void push(J job) throws InterruptedException {
        queue.put(job);
    }

    public int count() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
