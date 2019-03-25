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

import java.util.ArrayList;

public class SynapseServer {

    private String host;
    private String unitTestPort;
    private ArrayList<String> localServer = new ArrayList<>();

    String getHost() {
        return host;
    }

    String getUnitTestPort() {
        return unitTestPort;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUnitTestPort(String port) {
        this.unitTestPort = port;
    }

    ArrayList<String> getLocalServer() {
        return localServer;
    }

    public void setLocalServer(ArrayList<String> localServer) {
        this.localServer = localServer;
    }
}
