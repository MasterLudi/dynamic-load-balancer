package controller;

import job.*;
import host.Local;


public class StateManager {
    private Thread t;
    private JobQueue<Job> queue;
    private Local server;
    private HardwareMonitor monitor;

    private double throttling;
    private int clientQueueSize;
    private double clientCpuUsage;
    private double serverCpuUsage;

    public StateManager(JobQueue<Job> b, Local c, HardwareMonitor hm) {
        queue = b;
        server = c;
        monitor = hm;
    }

    public void start() {
        if (t == null) {
            t = new Thread(this::run, "State_manager");
            t.start();
        }
    }

    public void run() {

        while (true) {
            /* get hardware info */
            throttling = monitor.getThrottlingValue();

            String msg = "T " + throttling;

            server.sendMessage(msg);

            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getClientQueueSize() {
        return clientQueueSize;
    }

    public int getServerQueueSize() {
        return queue.count();
    }

    public double getThrottlingValue() {
        return throttling;
    }

    public double getClientCpuUsage() {
        return clientCpuUsage;
    }

    public double getServerCpuUsage() {
        return serverCpuUsage;
    }
}
