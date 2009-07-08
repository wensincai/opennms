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

package org.opennms.netmgt.dao.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.opennms.core.utils.LazyList;
import org.opennms.netmgt.dao.ResourceDao;
import org.opennms.netmgt.model.OnmsAttribute;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.model.OnmsResourceType;

public class DomainResourceType implements OnmsResourceType {
    private static final Set<OnmsAttribute> s_emptyAttributeSet = Collections.unmodifiableSet(new HashSet<OnmsAttribute>());
    private ResourceDao m_resourceDao;

    public DomainResourceType(ResourceDao resourceDao) {
        m_resourceDao = resourceDao;
    }
    
    public String getLabel() {
        return "Domain";
    }

    public String getName() {
        return "domain";
    }

    public List<OnmsResource> getResourcesForDomain(String domain) {
        throw new UnsupportedOperationException("method not implemented");
    }

    public List<OnmsResource> getResourcesForNode(int nodeId) {
        throw new UnsupportedOperationException("method not implemented");
    }

    public boolean isResourceTypeOnDomain(String domain) {
        return false;
    }

    public boolean isResourceTypeOnNode(int nodeId) {
        return false;
    }

    public String getLinkForResource(OnmsResource resource) {
        // Need a search for hosts in a domain. The present nodeList capability won't support it.
        // Just return null for now
        return null;
    }
    
    public OnmsResource createChildResource(String domain) {
        DomainChildResourceLoader loader = new DomainChildResourceLoader(domain);
        OnmsResource resource = new OnmsResource(domain, domain, this, s_emptyAttributeSet, new LazyList<OnmsResource>(loader));
        loader.setParent(resource);
        
        return resource;
    }
    
    public class DomainChildResourceLoader implements LazyList.Loader<OnmsResource> {
        private String m_domain;
        private OnmsResource m_parent;
        
        public DomainChildResourceLoader(String domain) {
            m_domain = domain;
        }
        
        public void setParent(OnmsResource parent) {
            m_parent = parent;
        }

        public List<OnmsResource> load() {
            List<OnmsResource> children = new LinkedList<OnmsResource>();

            for (OnmsResourceType resourceType : getResourceTypesForDomain(m_domain)) {
                for (OnmsResource resource : resourceType.getResourcesForDomain(m_domain)) {
                    resource.setParent(m_parent);
                    children.add(resource);
                }
            }

            return children;
        }
        
        private Collection<OnmsResourceType> getResourceTypesForDomain(String domain) {
            Collection<OnmsResourceType> resourceTypes = new LinkedList<OnmsResourceType>();
            for (OnmsResourceType resourceType : m_resourceDao.getResourceTypes()) {
                if (resourceType.isResourceTypeOnDomain(domain)) {
                    resourceTypes.add(resourceType);
                }
            }
            return resourceTypes;
        }
    }
}
