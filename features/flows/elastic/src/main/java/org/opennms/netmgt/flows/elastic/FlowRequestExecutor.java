/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2017-2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.flows.elastic;

import java.io.IOException;

import org.opennms.plugins.elasticsearch.rest.RequestExecutorFactory;
import org.opennms.plugins.elasticsearch.rest.executors.RequestExecutor;

import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.exception.CouldNotConnectException;

public class FlowRequestExecutor implements RequestExecutor, RequestExecutorFactory {

    @Override
    public <T extends JestResult> T execute(JestClient client, Action<T> clientRequest) {
        try {
            T result = client.execute(clientRequest);
            return result;
        } catch (CouldNotConnectException connectException) {
            return execute(client, clientRequest);
        } catch (IOException ex) {
            return execute(client, clientRequest);
        } catch (com.google.gson.JsonSyntaxException gsonException) {
            return execute(client, clientRequest);
        }
    }

    @Override
    public RequestExecutor createExecutor(int timeout, int retryCount) {
        return new FlowRequestExecutor();
    }
}

