/*******************************************************************************
 * This file is part of the OpenNMS(R) Application.
 *
 * Copyright (C) 2006-2008 The OpenNMS Group, Inc.  All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc.:
 *
 *      51 Franklin Street
 *      5th Floor
 *      Boston, MA 02110-1301
 *      USA
 *
 * For more information contact:
 *
 *      OpenNMS Licensing <license@opennms.org>
 *      http://www.opennms.org/
 *      http://www.opennms.com/
 *
 *******************************************************************************/

package org.opennms.netmgt.dao.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.opennms.netmgt.rrd.RrdDataSource;
import org.opennms.netmgt.rrd.RrdException;
import org.opennms.netmgt.rrd.RrdGraphDetails;
import org.opennms.netmgt.rrd.RrdStrategy;

public class NullRrdStrategy implements RrdStrategy {
	
	// THIS IS USED FOR TESTS SO RrdUtils can be initialized
	// but doesn't need to do anything

	public void closeFile(Object rrd) throws Exception {
	}

	public Object createDefinition(String creator, String directory,
			String rrdName, int step, List<RrdDataSource> dataSources, List<String> rraList)
			throws Exception {
		return null;
	}

	public void createFile(Object rrdDef) throws Exception {
	}

    public InputStream createGraph(String command, File workDir)
            throws IOException, RrdException {
        return null;
    }
    
    public RrdGraphDetails createGraphReturnDetails(String command, File workDir)
            throws IOException, RrdException {
        return null;
    }
    
	public Double fetchLastValue(String rrdFile, String ds, int interval)
			throws NumberFormatException, RrdException {
		return null;
	}

	public Double fetchLastValueInRange(String rrdFile, String ds, int interval, int range)
			throws NumberFormatException, RrdException {
		return null;
	}

	public String getStats() {
		return null;
	}

	public void graphicsInitialize() throws Exception {
	}

	public void initialize() throws Exception {
	}

	public Object openFile(String fileName) throws Exception {
		return null;
	}

	public void updateFile(Object rrd, String owner, String data)
			throws Exception {
	}

    public int getGraphLeftOffset() {
        return 0;
    }

    public int getGraphRightOffset() {
        return 0;
    }

    public int getGraphTopOffsetWithText() {
        return 0;
    }

    public String getDefaultFileExtension() {
        return ".nullRrd";
    }

    public Double fetchLastValue(String rrdFile, String ds,
            String consolidationFunction, int interval)
            throws NumberFormatException, RrdException {
        return null;
    }

    public void promoteEnqueuedFiles(Collection<String> rrdFiles) {
    }

}
