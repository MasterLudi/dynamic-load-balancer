package controller;

import job.*;
import host.*;

import java.io.IOException;


/**
 * This is a server-side Transfer Manager class.
 */
public class TransferManager {

    private JobQueue<Job> jobQueue;
    private JobQueue<Job> resQueue;
    private Host host;

    /**
     * constructor
     *
     * @param q : local queue
     * @param h : server or client
     */
    public TransferManager(JobQueue<job.Job> q, JobQueue<Job> r, Host h) {
        jobQueue = q;
        resQueue = r;
        host = h;

        // create new thread for getting message indicating aggregation
        Runnable task = this::handleMessage;

        new Thread(task).start();
    }

    /**
     * send jobs from the job queue. send string "S #jobs" prior to sending jobs
     *
     * @param num : number of jobs to send
     */
    public void sendJob(int num) {
        host.sendMessage("S " + Integer.toString(num));

        for (int i = 0; i < num; i++) {
            // if no more jobs in queue, send EmptyJob so the Remote knows
            if (jobQueue.isEmpty()) {
                System.out.println("Job queue is empty!");
                host.sendObj(new EmptyJob());
                break;
            } else {
                Job job = jobQueue.pop();
                System.out.printf("Transferring job #%d...\n", job.idx);
                host.sendObj(jobQueue.pop());
            }
        }

    }

    /**
     * receive jobs and puts to job queue. send string "R #jobs".
     *
     * @param num : number of jobs to receive
     */
    public void requestJob(int num) {
        try {
            host.sendMessage("R " + Integer.toString(num));

            Object obj;

            for (int i = 0; i < num; i++) {
                obj = host.getObj();
                if (obj instanceof Job) {
                    Job job = (Job) obj;
                    jobQueue.push(job);
                }
                // if there is no more jobs to send from client
                else {
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void handleMessage() {

        try {
            String message = host.getMessage();

            if (message.equals("F")) {

                Object obj;

                while (true) {

                    obj = host.getObj();
                    if (obj instanceof Job) {
                        Job job = (Job) obj;
                        resQueue.push(job);
                    }
                    // if there is no more jobs to send from client
                    else {
                        break;
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
