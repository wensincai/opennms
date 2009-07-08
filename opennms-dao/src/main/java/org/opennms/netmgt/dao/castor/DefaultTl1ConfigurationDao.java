/*******************************************************************************
 * This file is part of the OpenNMS(R) Application.
 *
 * Copyright (C) 2008-2009 The OpenNMS Group, Inc.  All rights reserved.
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

package org.opennms.netmgt.dao.castor;

import java.util.Collections;
import java.util.List;

import org.opennms.netmgt.config.tl1d.Tl1Element;
import org.opennms.netmgt.config.tl1d.Tl1dConfiguration;
import org.opennms.netmgt.dao.Tl1ConfigurationDao;

/**
 * DefaultTl1ConfigurationDao
 *
 * @author brozow
 */
public class DefaultTl1ConfigurationDao extends AbstractCastorConfigDao<Tl1dConfiguration, List<Tl1Element>>implements Tl1ConfigurationDao {

    public DefaultTl1ConfigurationDao() {
        super(Tl1dConfiguration.class, "TL1d configuration");
    }

    /* (non-Javadoc)
     * @see org.opennms.netmgt.dao.Tl1ConfigurationDao#getElements()
     */
    public List<Tl1Element> getElements() {
        return getContainer().getObject();
    }


    @Override
    public List<Tl1Element> translateConfig(Tl1dConfiguration castorConfig) {
        return Collections.unmodifiableList(castorConfig.getTl1ElementCollection());
    }

}
