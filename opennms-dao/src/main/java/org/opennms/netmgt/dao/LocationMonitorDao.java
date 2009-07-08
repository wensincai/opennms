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


package org.opennms.netmgt.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.opennms.netmgt.model.LocationMonitorIpInterface;
import org.opennms.netmgt.model.OnmsLocationMonitor;
import org.opennms.netmgt.model.OnmsLocationSpecificStatus;
import org.opennms.netmgt.model.OnmsMonitoredService;
import org.opennms.netmgt.model.OnmsMonitoringLocationDefinition;

/**
 * 
 * @author <a href="mailto:brozow@opennms.org">Mathew Brozowski</a>
 * @author <a href="mailto:david@opennms.org">David Hustace</a>
 *
 */
public interface LocationMonitorDao extends OnmsDao<OnmsLocationMonitor, Integer> {
    
    Collection<OnmsLocationMonitor> findByLocationDefinition(OnmsMonitoringLocationDefinition locationDefinition);
    
    List<OnmsMonitoringLocationDefinition> findAllMonitoringLocationDefinitions();
    
    OnmsMonitoringLocationDefinition findMonitoringLocationDefinition(String monitoringLocationDefinitionName);
    
    void saveMonitoringLocationDefinition(OnmsMonitoringLocationDefinition def);
    
    void saveMonitoringLocationDefinitions(Collection<OnmsMonitoringLocationDefinition> defs);

    void saveStatusChange(OnmsLocationSpecificStatus status);
    
    OnmsLocationSpecificStatus getMostRecentStatusChange(OnmsLocationMonitor locationMonitor, OnmsMonitoredService monSvc);

    Collection<OnmsLocationSpecificStatus> getAllMostRecentStatusChanges();
    
    Collection<OnmsLocationSpecificStatus> getAllStatusChangesAt(Date timestamp);
    
    /**
     * Returns all status changes since the date, <b>and</b> one previous
     * status change (so that status at the beginning of the period can be
     * determined).
     */
    Collection<OnmsLocationSpecificStatus> getStatusChangesBetween(Date startDate, Date endDate);

    Collection<LocationMonitorIpInterface> findStatusChangesForNodeForUniqueMonitorAndInterface(int nodeId);
    
}
