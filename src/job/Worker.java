package job;

import controller.HardwareMonitor;

import java.util.Timer;


public class Worker {

    private Thread t;

    private JobQueue<Job> jobQueue;
    private JobQueue<Job> resQueue;

    private Timer timer;
    private HardwareMonitor hardwareMonitor;
    private double throttle;
    private Job job;
    private int id;


    public Worker(int id, HardwareMonitor hm, JobQueue<Job> q, JobQueue<Job> r) {
        this.id = id;
        hardwareMonitor = hm;
        jobQueue = q;
        resQueue = r;
        timer = new Timer();
    }

    //periodically put worker thread to sleep put job back into jobQueue
    public void scheduleJob() {
        try {
            jobQueue.push(job);

            Thread.sleep((1 - ((long) throttle) * 100));
            throttle = hardwareMonitor.getThrottlingValue();
            timer.schedule(scheduleJob, throttle * 100);

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (t == null) {
            
            t = new Thread((Runnable) this, Integer.toString(id));
            t.start();
        }
    }

    public void run() {

        try {
            throttle = 1;
        timer.schedule(this.scheduleJob, throttle * 100);
            while (true) {
                if (!jobQueue.isEmpty()) {
                    resQueue.push(processJob(jobQueue.pop()));
                }
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Job processJob(Job job) {

        double result = 0.0;

        for (int i=0; i<job.array.length; i++) {
            for (int j=0; j<1000; j++) {
                result += 1.1111;
            }
            job.array[i] = result;
        }

        return job;
    }
}
