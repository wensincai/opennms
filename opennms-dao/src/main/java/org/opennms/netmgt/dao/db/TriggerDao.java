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

package org.opennms.netmgt.dao.db;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TriggerDao {
    private Map<String, Trigger> m_nameMap;
    private Map<String, List<Trigger>> m_tableMap;

    public TriggerDao() {
        reset();
    }

    public void reset() {
        m_nameMap = new LinkedHashMap<String, Trigger>();
        m_tableMap = new HashMap<String, List<Trigger>>();
    }

    public void add(Trigger t) {
        String lowerName = t.getName().toLowerCase();
        if (m_nameMap.containsKey(lowerName)) {
            throw new IllegalArgumentException("Trigger with name of '"
                                               + lowerName
                                               + "' already exists.");
        }
        
        m_nameMap.put(lowerName, t);
        
        getTriggersForTableCreateIfEmpty(t.getTable().toLowerCase()).add(t);
    }
    
    private List<Trigger> getTriggersForTableCreateIfEmpty(String table) {
        if (!m_tableMap.containsKey(table)) {
            m_tableMap.put(table, new LinkedList<Trigger>());
        }
        return m_tableMap.get(table);
    }
    
    public List<Trigger> getTriggersForTable(String table) {
        String lowerName = table.toLowerCase();
        if (!m_tableMap.containsKey(lowerName)) {
            return new LinkedList<Trigger>();
        }
        return m_tableMap.get(lowerName);
    }
}
