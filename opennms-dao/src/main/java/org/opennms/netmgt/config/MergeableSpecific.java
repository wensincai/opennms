/*******************************************************************************
 * This file is part of the OpenNMS(R) Application.
 *
 * Copyright (C) 2007-2009 The OpenNMS Group, Inc.  All rights reserved.
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


package org.opennms.netmgt.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Category;
import org.opennms.core.utils.InetAddressUtils;
import org.opennms.core.utils.ThreadCategory;

/**
 * This class is used as a wrapper object for the generated Specific class in the
 * config package.
 * 
 * @author <a href="mailto:david@opennms.org">David Hustace</a>
 */
public final class MergeableSpecific implements Comparable<String> {
    private String m_specific;
    private SpecificComparator m_comparator;
    private long m_value;

    public MergeableSpecific(String specific) {
        Category log = ThreadCategory.getInstance(getClass());
        m_specific = specific;
        try {
            m_value = InetAddressUtils.toIpAddrLong(InetAddress.getByName(specific));
        } catch (UnknownHostException e) {
            log.error("ComparableSpecific(): Exception in construction.", e);
            throw new IllegalArgumentException(e.getLocalizedMessage());
        }
        m_comparator = new SpecificComparator();
    }
    
    /**
     * Uses a comparable comparing to Specifics from the config package.
     * 
     */
    public int compareTo(String specific) {
        return m_comparator.compare(m_specific, specific);
    }
    public String getSpecific() {
        return m_specific;
    }
    public void setSpecific(String specific) {
        m_specific = specific;
    }
    public String toString() {
        return m_specific;
    }
    
    /**
     * @return the value
     */
    public long getValue() {
        return m_value;
    }

}
    
