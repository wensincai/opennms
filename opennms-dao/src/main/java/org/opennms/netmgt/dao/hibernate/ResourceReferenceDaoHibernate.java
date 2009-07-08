/*******************************************************************************
 * This file is part of the OpenNMS(R) Application.
 *
 * Copyright (C) 2007-2008 The OpenNMS Group, Inc.  All rights reserved.
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

package org.opennms.netmgt.dao.hibernate;

import org.opennms.netmgt.dao.ResourceReferenceDao;
import org.opennms.netmgt.model.ResourceReference;

/**
 * DAO implementation for accessing ResourceReference objects.
 * 
 * @author <a href="mailto:dj@opennms.org">DJ Gregor</a>
 * @see ResourceReference
 */
public class ResourceReferenceDaoHibernate extends AbstractDaoHibernate<ResourceReference, Integer> implements ResourceReferenceDao {

	public ResourceReferenceDaoHibernate() {
		super(ResourceReference.class);
	}

    public ResourceReference getByResourceId(String resourceId) {
        return findUnique("from ResourceReference where resourceId = ?", resourceId);
    }

}
