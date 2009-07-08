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

import java.util.Date;

import org.opennms.netmgt.model.AvailabilityReportLocator;

public class AvailabilityReportLocatorDaoTest extends AbstractTransactionalDaoTestCase {

    
	/*private AvailabilityReportLocatorDao m_availabilityReportLocatorDao;
        
        public AvailabilityReportLocatorDaoTest() throws MarshalException, ValidationException, IOException, PropertyVetoException, SQLException {
            
             * Note: I'm using the opennms-database.xml file in target/classes/etc
             * so that it has been filtered first.
             
            DataSourceFactory.setInstance(new C3P0ConnectionFactory("../opennms-base-assembly/target/classes/etc/opennms-database.xml"));
        }

	public void setAvailabilityReportLocatorDao(AvailabilityReportLocatorDao availabilityReportLocatorDao) {
		m_availabilityReportLocatorDao = availabilityReportLocatorDao;
	}*/
	
	public void testFindAll() {
		
		System.out.println("going for the report locator");
		AvailabilityReportLocator locator = new AvailabilityReportLocator();
		System.out.println("got the report locator");
		locator.setAvailable(true);
		locator.setCategory("cat1");
		locator.setDate(new Date());
		locator.setFormat("HTML");
		locator.setType("Random String");
		locator.setLocation("down the back of the sofa");
		
		getAvailabilityReportLocatorDao().save(locator);
		
		
		AvailabilityReportLocator retrieved = getAvailabilityReportLocatorDao().get(locator.getId());
		
		assertEquals(retrieved.getId(), locator.getId());
		assertEquals(retrieved.getAvailable(), locator.getAvailable());
		assertEquals(retrieved.getCategory(), locator.getCategory());
	}
	
}
