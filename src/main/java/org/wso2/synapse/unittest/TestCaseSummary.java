/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at

 *      http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.synapse.unittest;

public class TestCaseSummary {

    private String testSuiteName = "Test";
    private int testCaseCount = 0 ;
    private int passTestCount = 0;
    private int failureTestCount = 0;
    private String exception;
    private String deploymentStatus = "SKIPPED";
    private String mediationStatus = "SKIPPED";
    private String assertionStatus = "SKIPPED";

    public void setTestSuiteName(String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public int getTestCaseCount() {
        return testCaseCount;
    }

    public int getPassTestCount() {
        return passTestCount;
    }

    public int getFailureTestCount() {
        return failureTestCount;
    }

    public String getException() {
        return exception;
    }

    public String getDeploymentStatus() {
        return deploymentStatus;
    }

    public String getMediationStatus() {
        return mediationStatus;
    }

    public String getAssertionStatus() {
        return assertionStatus;
    }

    public void setTestCaseCount(int testCaseCount) {
        this.testCaseCount = testCaseCount;
    }

    public void setPassTestCount(int passTestCount) {
        this.passTestCount = passTestCount;
    }

    public void setFailureTestCount(int failureTestCount) {
        this.failureTestCount = failureTestCount;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void setDeploymentStatus(String deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
    }

    public void setMediationStatus(String mediationStatus) {
        this.mediationStatus = mediationStatus;
    }

    public void setAssertionStatus(String assertionStatus) {
        this.assertionStatus = assertionStatus;
    }
}
