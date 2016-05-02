/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
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

package org.opennms.features.topology.plugins.topo.graphml.model;

public class GraphMLNode extends GraphMLElement {

    public GraphMLNode() {
    }

    public Integer getX() {
        return getProperty(GraphMLProperties.X);
    }

    public Integer getY() {
        return getProperty(GraphMLProperties.Y);
    }

    @Override
    public String getId() {
        return String.valueOf(super.getId());
    }

    public String getIconKey() {
        return getProperty(GraphMLProperties.ICON_KEY);
    }

    public String getIpAddr() {
        return getProperty(GraphMLProperties.IP_ADDRESS);
    }

    public String getLabel() {
        return getProperty(GraphMLProperties.LABEL);
    }

    public boolean isLocked() {
        return Boolean.valueOf(getProperty(GraphMLProperties.LOCKED));
    }

    public Integer getNodeID() {
        return getProperty(GraphMLProperties.NODE_ID);
    }

    public boolean isSelected() {
        return Boolean.valueOf(getProperty(GraphMLProperties.SELECTED));
    }

    public String getTooltipText() {
        return getProperty(GraphMLProperties.TOOLTIP_TEXT);
    }

    public String getStyleName() {
        return getProperty(GraphMLProperties.STYLE_NAME);
    }

    @Override
    public <T> T accept(GraphMLElementVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
