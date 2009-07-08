/*******************************************************************************
 * This file is part of the OpenNMS(R) Application.
 *
 * Copyright (C) 2006, 2008-2009 The OpenNMS Group, Inc.  All rights reserved.
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

import java.io.Serializable;
import java.util.List;

import org.opennms.netmgt.model.OnmsCriteria;

public interface OnmsDao<T, K extends Serializable> {
    
    public abstract void initialize(Object obj);

    public abstract void flush();

    public abstract void clear();

    public abstract int countAll();

    public abstract void delete(T entity);

    public abstract List<T> findAll();
    
    public abstract List<T> findMatching(OnmsCriteria criteria);

    public abstract int countMatching(final OnmsCriteria onmsCrit);
    
    public abstract T get(K id);

    public abstract T load(K id);

    public abstract void save(T entity);

    public abstract void saveOrUpdate(T entity);

    public abstract void update(T entity);

}
