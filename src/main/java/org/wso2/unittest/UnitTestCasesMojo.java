package org.wso2.unittest;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
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

    @Parameter(property = "synapseHost")
    private String synapseHost;

    @Parameter(property = "synapsePort")
    private String synapsePort;

    public void execute() throws MojoExecutionException {

        try {
            appendTestLogs();
            testCaseRunner();
        } catch (Exception e) {
            throw new MojoExecutionException("Exception occurred while running test cases " + e.getMessage());
        }
    }

    private void testCaseRunner() throws IOException {

        int testCaseCount = 0;
        ArrayList<String> synapseTestCasePaths = getTestCasesFileNamesWithPaths(testCasesFilePath);

        getLog().info(" ");
        getLog().info("Detect " + synapseTestCasePaths.size() + " Synapse test case files to execute");
        getLog().info(" ");

        for (String synapseTestCaseFile : synapseTestCasePaths) {

            String responseFromUnitTestFramework =
                    UnitTestClient.executeTests(synapseTestCaseFile, synapseHost, synapsePort);

            if (responseFromUnitTestFramework != null) {
                isAssertResults(responseFromUnitTestFramework, testCaseCount);
            } else {
                throw new IOException("Test case parsing failed ");
            }
            testCaseCount++;
        }

    }

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

    private void isAssertResults(String response, int testCaseCount) throws IOException {
        String expectedResponse = "{\"test-cases\":\"SUCCESS\"}";

        if (!response.equals(expectedResponse)) {
            throw new IOException("Test Cases are failed - " + response);
        }

        getLog().info("SynapseTestCaseFile " + (testCaseCount + 1) +" passed successfully");
        getLog().info(" ");
    }

    private void appendTestLogs() {
        getLog().info("------------------------------------------------------------------------");
        getLog().info("U N I T - T E S T S");
        getLog().info("------------------------------------------------------------------------");

    }
}
