/*******************************************************************************
 * This file is part of the OpenNMS(R) Application.
 *
 * Copyright (C) 2008 The OpenNMS Group, Inc.  All rights reserved.
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

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.opennms.netmgt.model.ExternalValueAttribute;
import org.opennms.netmgt.model.OnmsAttribute;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.model.ResourceVisitor;
import org.opennms.netmgt.model.StringPropertyAttribute;
import org.opennms.test.ThrowableAnticipator;
import org.opennms.test.mock.EasyMockUtils;

/**
 * @author <a href="dj@opennms.org">DJ Gregor</a>
 */
public class ResourceAttributeFilteringResourceVisitorTest extends TestCase {
    private EasyMockUtils m_mocks = new EasyMockUtils();
    private ResourceVisitor m_delegatedVisitor = m_mocks.createMock(ResourceVisitor.class);
    
    public void testAfterPropertiesSet() throws Exception {
        ResourceAttributeFilteringResourceVisitor filteringVisitor = new ResourceAttributeFilteringResourceVisitor();
        filteringVisitor.setDelegatedVisitor(m_delegatedVisitor);
        filteringVisitor.setResourceAttributeKey("ifSpeed");
        filteringVisitor.setResourceAttributeValueMatch("100000000");
        filteringVisitor.afterPropertiesSet();
    }
    
    public void testAfterPropertiesSetNoDelegatedVisitor() throws Exception {
        ResourceAttributeFilteringResourceVisitor filteringVisitor = new ResourceAttributeFilteringResourceVisitor();
        
        ThrowableAnticipator ta = new ThrowableAnticipator();
        ta.anticipate(new IllegalStateException("property delegatedVisitor must be set to a non-null value"));
        
        filteringVisitor.setDelegatedVisitor(null);
        filteringVisitor.setResourceAttributeKey("ifSpeed");

        try {
            filteringVisitor.afterPropertiesSet();
        } catch (Throwable t) {
            ta.throwableReceived(t);
        }
        ta.verifyAnticipated();
    }
    
    public void testAfterPropertiesSetNoResourceTypeMatch() throws Exception {
        ResourceAttributeFilteringResourceVisitor filteringVisitor = new ResourceAttributeFilteringResourceVisitor();
        
        ThrowableAnticipator ta = new ThrowableAnticipator();
        ta.anticipate(new IllegalStateException("property resourceAttributeKey must be set to a non-null value"));

        filteringVisitor.setDelegatedVisitor(m_delegatedVisitor);
        filteringVisitor.setResourceAttributeValueMatch("1000000000");

        try {
            filteringVisitor.afterPropertiesSet();
        } catch (Throwable t) {
            ta.throwableReceived(t);
        }
        ta.verifyAnticipated();
    }
    
    public void testAfterPropertiesSetNoResourceAttributeValueMatch() throws Exception {
        ResourceAttributeFilteringResourceVisitor filteringVisitor = new ResourceAttributeFilteringResourceVisitor();
        
        ThrowableAnticipator ta = new ThrowableAnticipator();
        ta.anticipate(new IllegalStateException("property resourceAttributeValueMatch must be set to a non-null value"));

        filteringVisitor.setDelegatedVisitor(m_delegatedVisitor);
        filteringVisitor.setResourceAttributeKey("ifSpeed");

        try {
            filteringVisitor.afterPropertiesSet();
        } catch (Throwable t) {
            ta.throwableReceived(t);
        }
        ta.verifyAnticipated();
    }
    
    public void testVisitWithExternalValueMatch() throws Exception {
        ResourceAttributeFilteringResourceVisitor filteringVisitor = new ResourceAttributeFilteringResourceVisitor();
        filteringVisitor.setDelegatedVisitor(m_delegatedVisitor);
        filteringVisitor.setResourceAttributeKey("ifSpeed");
        filteringVisitor.setResourceAttributeValueMatch("1000000000");
        filteringVisitor.afterPropertiesSet();

        Set<OnmsAttribute> attributes = new HashSet<OnmsAttribute>(1);
        attributes.add(new ExternalValueAttribute("ifSpeed", "1000000000"));

        MockResourceType resourceType = new MockResourceType();
        resourceType.setName("interfaceSnmp");
        OnmsResource resource = new OnmsResource("1", "Node One", resourceType, attributes);
        
        // Expect
        m_delegatedVisitor.visit(resource);

        m_mocks.replayAll();
        filteringVisitor.visit(resource);
        m_mocks.verifyAll();
    }
    
    public void testVisitWithStringPropertyMatch() throws Exception {
        ResourceAttributeFilteringResourceVisitor filteringVisitor = new ResourceAttributeFilteringResourceVisitor();
        filteringVisitor.setDelegatedVisitor(m_delegatedVisitor);
        filteringVisitor.setResourceAttributeKey("ifSpeed");
        filteringVisitor.setResourceAttributeValueMatch("1000000000");
        filteringVisitor.afterPropertiesSet();

        Set<OnmsAttribute> attributes = new HashSet<OnmsAttribute>(1);
        attributes.add(new StringPropertyAttribute("ifSpeed", "1000000000"));

        MockResourceType resourceType = new MockResourceType();
        resourceType.setName("interfaceSnmp");
        OnmsResource resource = new OnmsResource("1", "Node One", resourceType, attributes);
        
        // Expect
        m_delegatedVisitor.visit(resource);

        m_mocks.replayAll();
        filteringVisitor.visit(resource);
        m_mocks.verifyAll();
    }
    
    public void testVisitWithoutMatch() throws Exception {
        ResourceAttributeFilteringResourceVisitor filteringVisitor = new ResourceAttributeFilteringResourceVisitor();
        filteringVisitor.setDelegatedVisitor(m_delegatedVisitor);
        filteringVisitor.setResourceAttributeKey("ifSpeed");
        filteringVisitor.setResourceAttributeValueMatch("1000000000");
        filteringVisitor.afterPropertiesSet();

        MockResourceType resourceType = new MockResourceType();
        resourceType.setName("something other than interfaceSnmp");
        OnmsResource resource = new OnmsResource("1", "Node One", resourceType, new HashSet<OnmsAttribute>(0));

        m_mocks.replayAll();
        filteringVisitor.visit(resource);
        m_mocks.verifyAll();
    }
}
