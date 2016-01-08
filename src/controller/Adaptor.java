package controller;

import java.io.IOException;


public class Adaptor {
    private StateManager stateManager;
    private TransferManager transferManager;

    private int localQueueLength;
    private int remoteQueueLength;
    private double localCpuUsage;
    private double remoteCpuUsage;

    //constructor
    public Adaptor(StateManager sm, TransferManager tm){
        stateManager = sm;
        transferManager = tm;
    }

    // sender initiated transfer policy
    public void senderInitiatedBalance() throws IOException {
        int balance = balance();

        if (!queueEmpty()) {
            if (balance > 0) {
                transferManager.sendJob(balance / 2);
            }
        }
    }

    // receiver initiated transfer policy
    public void receiverInitiatedBalance() throws IOException {
        int balance = balance();
        if (!queueEmpty()) {
            if (balance < 0) {
                transferManager.requestJob(-balance / 2);
            }
        }
    }

    // bidirectional transfer policy
    public void bidirectionalBalance() throws  IOException {
        senderInitiatedBalance();
        receiverInitiatedBalance();
    }

    // weighted policy
    public void weightedBalance() throws IOException {

        double balance = localQueueLength * localCpuUsage - remoteQueueLength * remoteCpuUsage;
        if(!queueEmpty()) {
            if (balance < 0) {
                transferManager.requestJob((int) balance * remoteQueueLength);
            } else {
                transferManager.sendJob((int) balance * localQueueLength);
            }
        }
    }

    //calculate difference in queue size
    public int balance(){
        return (localQueueLength - remoteQueueLength);
    }


    public boolean queueEmpty(){

        if(localQueueLength == 0 && remoteQueueLength == 0){
            return true;
        }
        return false;
    }

    private void updateInfo() {
        localCpuUsage = stateManager.getServerCpuUsage();
        remoteCpuUsage = stateManager.getClientCpuUsage();
        localQueueLength = stateManager.getServerQueueSize();
        remoteQueueLength = stateManager.getClientQueueSize();
    }
}
