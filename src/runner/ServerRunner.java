package runner;

import controller.*;
import gui.LogGui;
import host.*;
import job.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Local-side Dynamic Load Balancer.
 */
public class ServerRunner {

    static Local transferChannel;
    static Local stateChannel;

    static HardwareMonitor hardwareMonitor;
    static TransferManager transferManager;
    static StateManager stateManager;
    static Adaptor adaptor;
    static Worker worker;

    static int JOB_NUMBER = 512;
    static int JOB_SIZE = 1024 * 1024 * 32 / JOB_NUMBER;
    static ArrayList<Job> jobs;
    static JobQueue<Job> jobQueue;
    static JobQueue<Job> resQueue;

    static Job[] results;


    /**
     * Main thread.
     *
     * @param args : port number
     */
    public static void main(String[] args) throws IOException {

        // initialize transferChannel
        if (args.length != 1) {
            System.out.println("Usage: ServerRunner [#port]");
        }

        int port = Integer.parseInt(args[0]);

        transferChannel = new Local(port);
        stateChannel = new Local(port + 1);

        transferChannel.listen();
        stateChannel.listen();

        initialize();

        bootstrap();

        processJobs();

        aggregate();

    }

    /**
     * Phase 0: INITIALIZATION
     */
    static void initialize() {

        hardwareMonitor = new HardwareMonitor();

        LogGui lg = new LogGui(hardwareMonitor);

        while (true) {
            if (lg.guiSet)
                break;
        }

        System.out.println("--------------------------");
        System.out.println("PHASE 0: INITIALIZATION");
        System.out.println("--------------------------");

        jobQueue = new JobQueue<>();
        resQueue = new JobQueue<>();
        transferManager = new TransferManager(jobQueue, resQueue, transferChannel);
        stateManager = new StateManager(jobQueue, transferChannel, hardwareMonitor);
        stateManager.start();
        adaptor = new Adaptor(stateManager, transferManager);

        worker = new Worker(1, hardwareMonitor, jobQueue, resQueue);

        worker.start();

        results = new Job[JOB_NUMBER];

    }

    /**
     * Phase 1: BOOTSTRAPPING
     * initialize jobs and send half to the remote node.
     */
    private static void bootstrap() {

        System.out.println("--------------------------");
        System.out.println("PHASE 1: BOOTSTRAPPING");
        System.out.println("--------------------------");

        initJobs();
        System.out.println("Initial jobs allocated by transferChannel");

        // send half of the job
        transferChannel.sendMessage("S " + JOB_NUMBER / 2);
        for (int i = 0; i < JOB_NUMBER / 2; i++) {
            System.out.printf("bootstrapping..... job #%d\n", i);
            Job job = jobs.get(i);
            transferChannel.sendObj(job);
        }

        System.out.printf("bootstrapping %d jobs finished!\n", JOB_NUMBER);
    }

    /**
     * Phase 2: PROCESS JOBS
     */
    static void processJobs() {

        System.out.println("--------------------------");
        System.out.println("PHASE 2: PROCESSING");
        System.out.println("--------------------------");
    }

    /**
     * Phase 3: AGGREGATE
     */
    static void aggregate() throws IOException {

        System.out.println("--------------------------");
        System.out.println("PHASE 3: AGGREGATION");
        System.out.println("--------------------------");

        transferChannel.sendMessage("F");

        while (!resQueue.isEmpty()) {
            Job j = resQueue.pop();
            results[j.idx] = j;
        }
    }


    private static void initJobs() {

        jobs = new ArrayList<>();

        for (int i = 0; i < JOB_NUMBER; i++) {
            jobs.add(new Job(JOB_SIZE, i));
        }
    }

}
