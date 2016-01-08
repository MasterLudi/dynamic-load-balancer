package controller;

import job.*;
import host.*;

import java.io.IOException;

/**
 * This is a Remote-side Transfer Handler class.
 */
public class TransferHandler {

    private JobQueue<Job> jobQueue;
    private JobQueue<Job> resultsQueue;
    private Host host;
    private boolean running;


    public TransferHandler(JobQueue<Job> q, JobQueue<Job> r, Host h) {
        jobQueue = q;
        resultsQueue = r;
        host = h;
        running = true;

        Runnable task = () -> {
            while (running) {
                handleTransfer();
            }
        };

        new Thread(task).start();
    }

    private void handleTransfer() {


        String raw = host.getMessage();

        // when all jobs are done
        if (raw.startsWith("F")) {
            host.sendMessage("F");
            sendJob(resultsQueue.count(), resultsQueue);
        } else {
            String[] message = raw.split(" ");

            // server sending jobs to client
            if (message[0].equals("S")) {
                receiveJob(Integer.parseInt(message[1]));
            }
            // server requesting jobs from client
            else if (message[0].equals("R")) {
                sendJob(Integer.parseInt(message[1]), jobQueue);
            }
        }
    }

    private void receiveJob(int num) {

        try {
            Object obj;

            for (int i = 0; i < num; i++) {
                obj = host.getObj();
                if (obj instanceof Job) {
                    Job job = (Job) obj;
                    System.out.printf("Received... Job #%d\n", job.idx);
                    jobQueue.push(job);
                } else {
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendJob(int num, JobQueue<Job> queue) {

        for (int i = 0; i < num; i++) {
            if (queue.isEmpty()) {
                host.sendObj(new EmptyJob());
                break;
            } else {
                host.sendObj(queue.pop());
            }
        }
        host.sendObj(new EmptyJob());
    }
}
