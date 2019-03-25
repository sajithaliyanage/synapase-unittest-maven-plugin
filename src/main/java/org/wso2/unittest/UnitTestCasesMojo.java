package org.wso2.unittest;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * @phase process-sources
 */

@Mojo(name = "SynapseUnitTest")
public class UnitTestCasesMojo extends AbstractMojo {

    @Parameter(property = "testCasesFilePath")
    private String testCasesFilePath;

    @Parameter(property = "synapseServer")
    private SynapseServer synapseServer;

    /**
     * Execution method of Mojo class.
     */
    public void execute() throws MojoExecutionException {

        try {
            appendTestLogs();
            startTestingServer();
            testCaseRunner();
        } catch (Exception e) {
            throw new MojoExecutionException("Exception occurred while running test cases " + e.getMessage());
        } finally {
            stopTestingServer();
        }
    }

    /**
     * Execution method of Mojo class.
     */
    private void testCaseRunner() throws IOException {

        ArrayList<String> synapseTestCasePaths = getTestCasesFileNamesWithPaths(testCasesFilePath);

        getLog().info("Detect " + synapseTestCasePaths.size() + " Synapse test case files to execute\n");

        for (String synapseTestCaseFile : synapseTestCasePaths) {

            String responseFromUnitTestFramework = UnitTestClient.executeTests
                            (synapseTestCaseFile, synapseServer.getHost(), synapseServer.getUnitTestPort());

            if (responseFromUnitTestFramework != null) {
                isAssertResults(responseFromUnitTestFramework, synapseTestCaseFile);
            } else {
                throw new IOException("Test case parsing failed ");
            }
        }

    }

    /**
     * Get saved SynapseTestcaseFiles from the given destination.
     *
     * @param testFileFolder current files location
     * @return list of files with their file paths
     */
    private ArrayList<String> getTestCasesFileNamesWithPaths(String testFileFolder) {

        ArrayList<String> fileNamesWithPaths = new ArrayList<>();
        File folder = new File(testFileFolder);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                String filename = file.getName();
                if (filename.endsWith(".xml") || filename.endsWith(".XML")) {

                    fileNamesWithPaths.add(testFileFolder + "/" + filename);
                }
            }
        }

        return fileNamesWithPaths;
    }

    /**
     * Assert the expected value with receiving response from unit test client.
     *
     * @param response response from client
     * @param synapseTestCaseFile test-case file
     */
    private void isAssertResults(String response, String synapseTestCaseFile) throws IOException {
        String expectedResponse = "{\"test-cases\":\"SUCCESS\"}";

        if (!response.equals(expectedResponse)) {
            throw new IOException("Test Cases are failed - " + response);
        }

        getLog().info("SynapseTestCaseFile " + synapseTestCaseFile +" passed successfully");
        getLog().info(" ");
    }

    /**
     * Append log of UNIT-TEST when testing started.
     */
    private void appendTestLogs() {
        getLog().info("------------------------------------------------------------------------");
        getLog().info("U N I T - T E S T S");
        getLog().info("------------------------------------------------------------------------");

    }

    /**
     * Start the Unit testing agent server if user defined it in configuration.
     */
    private void startTestingServer() throws IOException {

        //check already has unit testing server
        if (!synapseServer.getLocalServer().isEmpty()) {
            String[] cmd = {synapseServer.getLocalServer().get(0), "unitTest", synapseServer.getUnitTestPort()};
            Runtime.getRuntime().exec(cmd);

            getLog().info("Starting unit testing agent of path - " + synapseServer.getLocalServer().get(0));
            getLog().info("Waiting for testing agent initialization");

            //check port availability
            boolean isAvailable = true;
            long timeoutExpiredMs = System.currentTimeMillis() + 25000;
            while (isAvailable) {
                long waitMillis = timeoutExpiredMs - System.currentTimeMillis();
                isAvailable = checkPortAvailability(Integer.parseInt(synapseServer.getUnitTestPort()));

                if (waitMillis <= 0) {
                    // timeout expired
                    throw new IOException("Connection refused for service in port - " + synapseServer.getUnitTestPort());
                }
            }

            getLog().info("Unit testing agent started\n");

        } else {
            getLog().info("Testing will execute with already started unit testing agent");
        }

    }

    /**
     * Stop the Unit testing agent server.
     */
    private void stopTestingServer() {

        if (!synapseServer.getLocalServer().isEmpty()) {

            try {
                getLog().info("Stopping unit testing agent runs on port " + synapseServer.getUnitTestPort());

                //check running operating system
                if (System.getProperty("os.name").toLowerCase().contains("win")) {

                    Runtime runTime = Runtime.getRuntime();
                    Process proc =
                            runTime.exec("cmd /c netstat -ano | findstr " + synapseServer.getUnitTestPort());

                    BufferedReader stdInput = new BufferedReader(new
                            InputStreamReader(proc.getInputStream()));

                    String stream = stdInput.readLine();
                    if (stream != null) {
                        int index = stream.lastIndexOf(' ');
                        String pid = stream.substring(index);

                        getLog().info("Unit testing agent runs with PID of " + pid);
                        runTime.exec("cmd /c Taskkill /PID" + pid + " /T /F");
                    }

                } else {
                    Process pr = Runtime.getRuntime().exec("lsof -t -i:" + synapseServer.getUnitTestPort());
                    BufferedReader read = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                    String pid = read.readLine();

                    getLog().info("Unit testing agent runs with PID of " + pid);
                    Runtime.getRuntime().exec("kill -9 " + pid);
                }

                getLog().info("Unit testing agent stopped");

            } catch (Exception e) {
                getLog().error("Error in closing the server");
            }
        }

    }

    /**
     * Check port availability.
     *
     * @param port port which want to check availability
     * @return  if available true else false
     */
    private static boolean checkPortAvailability(int port) {
        boolean isAvailable;
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", port));
            isAvailable = false;
        } catch (IOException e) {
            isAvailable = true;
        }

        return isAvailable;
    }
}
