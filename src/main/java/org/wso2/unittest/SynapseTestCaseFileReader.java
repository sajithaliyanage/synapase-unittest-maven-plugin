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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static org.wso2.unittest.Constants.ARTIFACT;
import static org.wso2.unittest.Constants.ARTIFACT_FILE;

/**
 * SynapseTestCase file read class in unit test framework.
 */
class SynapseTestCaseFileReader {

    private SynapseTestCaseFileReader() {}
    private static Log log;
    /**
     * Check that SynapseTestCase data includes artifact data.
     * If not read artifact from given file and append data into the artifact data
     *
     * @return SynapseTestCase data which is ready to send to the server
     */
    static String processArtifactData(String synapseTestCaseFilePath) {

        try {
            String synapseTestCaseFileAsString = FileUtils.readFileToString(new File(synapseTestCaseFilePath));
            OMElement importedXMLFile = AXIOMUtil.stringToOM(synapseTestCaseFileAsString);

            getLog().info("Checking SynapseTestCase file contains artifact data");

            //Read artifact from SynapseTestCase file
            QName qualifiedArtifact = new QName("", ARTIFACT, "");
            OMElement artifactNode = importedXMLFile.getFirstChildWithName(qualifiedArtifact);

            //check if artifact or artifact-file available in SynapseTestCase file
            if (artifactNode == null) {

                getLog().info("SynapseTestCase file does not contains artifact data. Checking for artifact file data");
                QName qualifiedArtifactFile = new QName("", ARTIFACT_FILE, "");
                OMElement artifactFileNode = importedXMLFile.getFirstChildWithName(qualifiedArtifactFile);
                String artifactFilePath = artifactFileNode.getText();


                String artifactFileAsString = FileUtils.readFileToString(new File(artifactFilePath));
                artifactNode = AXIOMUtil.stringToOM(artifactFileAsString);

                //check artifact-file contains artifact data
                if (artifactNode == null) {

                    throw new IOException
                            ("SynapseTestCase file does not contains artifact data or artifact file details");

                } else {

                    getLog().info("Artifact file data imported to the SynapseTestCase file data");

                    //create new tag name artifact to add artifact-file data as a child
                    OMFactory fac = OMAbstractFactory.getOMFactory();
                    OMElement artifactNewNode = fac.createOMElement(new QName(ARTIFACT));
                    artifactNewNode.addChild(artifactNode);

                    //add artifact node to the SynapseTestCase data
                    importedXMLFile.addChild(artifactNewNode);

                    //remove ARTIFACT-FILE tag from SynapseTestCase data
                    Iterator item = importedXMLFile.getChildrenWithName(new QName(ARTIFACT_FILE));
                    if (item.hasNext()) {
                        item.next();
                        item.remove();
                    }
                }

            }

            return importedXMLFile.toString();

        } catch (IOException | XMLStreamException e) {
            getLog().error("Artifact data reading failed ", e);
        }

        return null;
    }

    static Log getLog() {
        if ( log == null )
        {
            log = new SystemStreamLog();
        }

        return log;
    }
}
