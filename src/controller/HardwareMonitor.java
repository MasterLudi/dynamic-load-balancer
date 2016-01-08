package controller;

import java.io.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hardware monitor used for getting user cpu usage.
 */
public class HardwareMonitor {

   private static String statPath = "/proc/stat";// stat info
   private static String cpuinfoPath = "/proc/cpuinfo";//cpu info

    private static String LINE_SEPARATOR = "line.separator";

    private double throttle;

    private class cpuInfo {
        int user;
        int nice;
        int sys;
        int idle;
    }

    /**
     * Fetch the entire contents of a text file, and return it in a String. This
     * style of implementation does not throw Exceptions to the caller.
     *
     * @param path is a file which already exists and can be read.
     */
    private synchronized String getContents(String path) {
        //...checks on aFile are elided
        StringBuilder contents = new StringBuilder();
        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(new File(path)));
            try {
                String line = null; //not declared within while loop
                    /*
                 		* readLine is a bit quirky : it returns the content of a line
                 		* MINUS the newline. it returns null only for the END of the
                 		* stream. it returns an empty String if two newlines appear in
                 		* a row.
                 		*/
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty(LINE_SEPARATOR));
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(HardwareMonitor.class.getName()).log(Level.SEVERE, null, ex);
        }

        return contents.toString();
    }


    /**
     * Opens a stream from a existing file and return it. This style of
     * implementation does not throw Exceptions to the caller.
     */
    private synchronized BufferedReader getStream(String _path) {
        BufferedReader br = null;

        File file = new File(_path);

        FileReader fileReader;

        try {
            fileReader = new FileReader(file);
            br = new BufferedReader(fileReader);
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        return br;
    }

    /**
     * This is for one core.
     * TODO: refactor for multi-core.
     * @param info : info structure to store cpu info
     */
    private void getCpuInfo (cpuInfo info) {

        try {
            BufferedReader br = null;
            // We gonna parse de first line (total)
            // Line example: cpu0 311689 2102 654770 6755602 32431 38 4127 0 0 0
            br = getStream(statPath);

            //read the total cpu line
            String[] tempData;
            tempData = br.readLine().split(" ");

            //first four fields
            info.user = Integer.parseInt(tempData[2]);
            info.nice = Integer.parseInt(tempData[3]);
            info.sys = Integer.parseInt(tempData[4]);
            info.idle = Integer.parseInt(tempData[5]);

            br.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * returns the user cpu usage in percentage.
     */
    public double getUserCpuUsage() {

        double percent = 0;

        try {
            cpuInfo info1 = new cpuInfo();
            cpuInfo info2 = new cpuInfo();

            // get cpu info
            getCpuInfo(info1);
            // sleep for 1 sec
            Thread.sleep(1000);
            getCpuInfo(info2);

            int total = (info2.user + info2.nice + info2.sys) - (info1.user + info1.nice + info1.sys);

            percent = total;

            total += (info2.idle - info1.idle);

            percent = (percent / total);

        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }

		System.out.printf("user cpu-usage : %f percent \n", percent);
        return percent;

    }

    private int getNumberOfCores() {
        String[] tempData = null;

        // Parse /proc/cpuinfo to obtain how many cores the CPU has.
        String[] tempFile = getContents(cpuinfoPath).split(System.getProperty(LINE_SEPARATOR));

        for (String line : tempFile) {
            if (line.contains("cpu cores")) {
                tempData = line.split(":");
                break;
            }
        }

        if (tempData != null) {
            return Integer.parseInt(tempData[1].trim());
        }
        return -1;
    }

    public void setThrottlingValue(String val) {
        throttle = Double.parseDouble(val);
        System.out.printf("Throttle value changed to %s\n", val);
    }

    public double getThrottlingValue() {
        return throttle;
    }

}
