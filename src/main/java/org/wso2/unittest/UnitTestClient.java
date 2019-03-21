/*
 Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.unittest;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

/**
 * SynapseTestCase file read class in unit test framework.
 */
public class UnitTestClient {

    private static Log log;

    /**
     * Main method of the synapse unit testing client.
     */
    static String executeTests(String synapseTestCaseFilePath, String synapseHost, String synapsePort) {

        getLog().info("Unit testing client started");
        String responseFromServer = null;
        try {

            //process SynapseTestCase data for send to the server
            String deployableMessage = SynapseTestCaseFileReader.processArtifactData(synapseTestCaseFilePath);

            if (deployableMessage != null) {
                //create tcp connection, send SynapseTestCase file to server and get the response from the server
                TCPClient tcpClient = new TCPClient(synapseHost, synapsePort);
                tcpClient.writeData(deployableMessage);
                responseFromServer = tcpClient.readData();
                tcpClient.closeSocket();

            } else {
                getLog().error("Error in creating deployable message");
            }

            return responseFromServer;

        } catch (Exception e) {
            getLog().error("Error while executing client", e);
        }

        getLog().info("Unit testing client stopped");
        return responseFromServer;
    }

    static Log getLog() {
        if (log == null) {
            log = new SystemStreamLog();
        }

        return log;
    }

}
