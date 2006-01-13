//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2002-2003 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified 
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.                                                            
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//       
// For more information contact: 
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//
// Tab Size = 8
//

package org.opennms.netmgt.config;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Category;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.opennms.core.utils.ThreadCategory;
import org.opennms.netmgt.ConfigFileConstants;
import org.opennms.netmgt.config.passive.Attribute;
import org.opennms.netmgt.config.passive.AttributeValue;
import org.opennms.netmgt.config.passive.Constant;
import org.opennms.netmgt.config.passive.EventTranslationSpec;
import org.opennms.netmgt.config.passive.Field;
import org.opennms.netmgt.config.passive.FieldValue;
import org.opennms.netmgt.config.passive.Mapping;
import org.opennms.netmgt.config.passive.Parameter;
import org.opennms.netmgt.config.passive.ParameterValue;
import org.opennms.netmgt.config.passive.PassiveEvent;
import org.opennms.netmgt.config.passive.PassiveStatusConfiguration;
import org.opennms.netmgt.config.passive.StatusKey;
import org.opennms.netmgt.eventd.EventUtil;
import org.opennms.netmgt.poller.pollables.PollStatus;
import org.opennms.netmgt.xml.event.Event;
import org.opennms.netmgt.xml.event.Parm;
import org.opennms.netmgt.xml.event.Parms;
import org.springframework.beans.BeanUtils;

/**
 * This is the singleton class used to load the configuration from the
 * passive-status-configuration.xml. This provides convenience methods to get the configured
 * categories and their information, add/delete categories from category groups.
 * 
 * <strong>Note: </strong>Users of this class should make sure the
 * <em>init()</em> is called before calling any other method to ensure the
 * config is loaded before accessing other convenience methods.
 * 
 * @author <a href="mailto:david@opennms.org">David Hustace </a>
 * @author <a href="http://www.opennms.org/">OpenNMS </a>
 */
public final class PassiveStatusConfigFactory implements PassiveStatusConfig {
    /**
     * The singleton instance of this factory
     */
    private static PassiveStatusConfig m_singleton = null;

    /**
     * The config class loaded from the config file
     */
    private PassiveStatusConfiguration m_config;

	private List m_translationSpecs;

    /**
     * This member is set to true if the configuration file has been loaded.
     */
    private static boolean m_loaded = false;
    
    /**
     * Private constructor
     * 
     * @exception java.io.IOException
     *                Thrown if the specified config file cannot be read
     * @exception org.exolab.castor.xml.MarshalException
     *                Thrown if the file does not conform to the schema.
     * @exception org.exolab.castor.xml.ValidationException
     *                Thrown if the contents do not match the required schema.
     * 
     */
    private PassiveStatusConfigFactory(String configFile) throws IOException, MarshalException, ValidationException {
        Reader rdr = new InputStreamReader(new FileInputStream(configFile));
        marshallReader(rdr);
        rdr.close();
    }
    
    public PassiveStatusConfigFactory(Reader rdr) throws MarshalException, ValidationException {
        marshallReader(rdr);
    }
    
    private synchronized void marshallReader(Reader rdr) throws MarshalException, ValidationException {
        m_config = (PassiveStatusConfiguration) Unmarshaller.unmarshal(PassiveStatusConfiguration.class, rdr);
    }

    /**
     * Load the config from the default config file and create the singleton
     * instance of this factory.
     * 
     * @exception java.io.IOException
     *                Thrown if the specified config file cannot be read
     * @exception org.exolab.castor.xml.MarshalException
     *                Thrown if the file does not conform to the schema.
     * @exception org.exolab.castor.xml.ValidationException
     *                Thrown if the contents do not match the required schema.
     */
    public static synchronized void init() throws IOException, MarshalException, ValidationException {
        if (m_loaded) {
            // init already called - return
            // to reload, reload() will need to be called
            return;
        }

        File cfgFile = ConfigFileConstants.getFile(ConfigFileConstants.PASSIVE_CONFIG_FILE_NAME);

        m_singleton = new PassiveStatusConfigFactory(cfgFile.getPath());

        m_loaded = true;
    }

    /**
     * Reload the config from the default config file
     * 
     * @exception java.io.IOException
     *                Thrown if the specified config file cannot be read/loaded
     * @exception org.exolab.castor.xml.MarshalException
     *                Thrown if the file does not conform to the schema.
     * @exception org.exolab.castor.xml.ValidationException
     *                Thrown if the contents do not match the required schema.
     */
    public static synchronized void reload() throws IOException, MarshalException, ValidationException {
        m_singleton = null;
        m_loaded = false;

        init();
    }

    /**
     * Return the singleton instance of this factory.
     * 
     * @return The current factory instance.
     * 
     * @throws java.lang.IllegalStateException
     *             Thrown if the factory has not yet been initialized.
     */
    public static synchronized PassiveStatusConfig getInstance() {
        if (!m_loaded)
            throw new IllegalStateException("getInstance: The factory has not been initialized");

        return m_singleton;
    }

	public static void setInstance(PassiveStatusConfig singleton) {
		m_singleton=singleton;
		m_loaded=true;
	}
	
    /**
     * Return the PassiveStatus configuration.
     * 
     * @return the PassiveStatus configuration
     */
    private synchronized PassiveStatusConfiguration getConfig() {
        return m_config;
    }
    

    /*
     *  (non-Javadoc)
     * @see org.opennms.netmgt.config.PassiveStatusConfig#getUEIList()
     */
    public List getUEIList() {
        Set ueiSet = new TreeSet(getPassiveStatusUEIs());
        ueiSet.addAll(getTranslationUEIs());
        return new ArrayList(ueiSet);
    }

    private List getTranslationUEIs() {
		List translatedEvents = getConfig().getTranslation().getEventTranslationSpecCollection();
		List ueis = new ArrayList();
		for (Iterator it = translatedEvents.iterator(); it.hasNext();) {
			EventTranslationSpec event = (EventTranslationSpec) it.next();
			ueis.add(event.getUei());
		}
		return ueis;
	}
    
    private List getPassiveStatusUEIs() {
        List passiveEvents = getConfig().getPassiveEvents().getPassiveEventCollection();
        List ueis = new ArrayList();
        for (Iterator it = passiveEvents.iterator(); it.hasNext();) {
            PassiveEvent event = (PassiveEvent) it.next();
            ueis.add(event.getUei());
        }
        return ueis;
    }
    
    public PassiveStatusValue getPassiveStatusValue(Event e) {
        PassiveStatusKey key = new PassiveStatusKey(getMatchedNodeLabel(e), getMatchedIpAddr(e), getMatchedServiceName(e));
        return new PassiveStatusValue(key, getMatchedStatus(e));
    }

    /**
     * This method determines the nodelabel value by using the config to determine this value
     * based on field and parm values of a passive event.
     * 
     * @param e
     * @return The PollStatus from the event
     */
    public PollStatus getMatchedStatus(Event e) {
        String eventToken = getPassiveEventByUei(e.getUei()).getStatusKey().getStatus().getEventToken().getName();
        String expr = getPassiveEventByUei(e.getUei()).getStatusKey().getStatus().getEventToken().getValue();
        String pattern = getPassiveEventByUei(e.getUei()).getStatusKey().getStatus().getEventToken().getFormat();
        boolean isParm = getPassiveEventByUei(e.getUei()).getStatusKey().getStatus().getEventToken().getIsParm();
        
        String tokenValue = getValueFromFieldOrParm(e, eventToken, isParm);
        return PollStatus.decodePollStatus(parseExpression(tokenValue, expr, pattern), e.getLogmsg().getContent());
    }

    /**
     * This method determines the nodelabel value by using the config to determine this value
     * based on field and parm values of a passive event.
     * 
     * @param e
     * @return The correct value of the servicename portion of the status key
     */
    public String getMatchedServiceName(Event e) {
        String eventToken = getPassiveEventByUei(e.getUei()).getStatusKey().getServiceName().getEventToken().getName();
        String expr = getPassiveEventByUei(e.getUei()).getStatusKey().getServiceName().getEventToken().getValue();
        String pattern = getPassiveEventByUei(e.getUei()).getStatusKey().getServiceName().getEventToken().getFormat();
        boolean isParm = getPassiveEventByUei(e.getUei()).getStatusKey().getServiceName().getEventToken().getIsParm();
        
        String tokenValue = getValueFromFieldOrParm(e, eventToken, isParm);
        return parseExpression(tokenValue, expr, pattern);
    }

    /**
     * This method determines the nodelabel value by using the config to determine this value
     * based on field and parm values of a passive event.
     * 
     * @param e
     * @return The correct value of the ipaddr portion of the status key
     */
    public String getMatchedIpAddr(Event e) {
        String eventToken = getPassiveEventByUei(e.getUei()).getStatusKey().getIpaddr().getEventToken().getName();
        String expr = getPassiveEventByUei(e.getUei()).getStatusKey().getIpaddr().getEventToken().getValue();
        String pattern = getPassiveEventByUei(e.getUei()).getStatusKey().getIpaddr().getEventToken().getFormat();
        boolean isParm = getPassiveEventByUei(e.getUei()).getStatusKey().getIpaddr().getEventToken().getIsParm();
        
        String tokenValue = getValueFromFieldOrParm(e, eventToken, isParm);
        return parseExpression(tokenValue, expr, pattern);
    }

    /**
     * This method determines the nodelabel value by using the config to determine this value
     * based on field and parm values of a passive event.
     * 
     * @param e
     * @return The correct value of the nodelabel portion of the status key
     */
    public String getMatchedNodeLabel(Event e) {
        String eventToken = getPassiveEventByUei(e.getUei()).getStatusKey().getNodeLabel().getEventToken().getName();
        String expr = getPassiveEventByUei(e.getUei()).getStatusKey().getNodeLabel().getEventToken().getValue();
        String pattern = getPassiveEventByUei(e.getUei()).getStatusKey().getNodeLabel().getEventToken().getFormat();
        boolean isParm = getPassiveEventByUei(e.getUei()).getStatusKey().getNodeLabel().getEventToken().getIsParm();

        String tokenValue = getValueFromFieldOrParm(e, eventToken, isParm);
        return parseExpression(tokenValue, expr, pattern);
    }

    /**
     * Wish there was a better way to do this!
     * @param eventToken
     * @param e
     * @return String value from the event field specified in @param eventToken
     */
    private String getEventField(String eventToken, Event e) {
        if (eventToken.equalsIgnoreCase("descr")) {
            return e.getDescr();
        } else if (eventToken.equalsIgnoreCase("distPoller")) {
            return e.getDistPoller();
        } else if (eventToken.equalsIgnoreCase("host")) {
            return e.getHost();
        } else if (eventToken.equalsIgnoreCase("ifAlias")) {
            return e.getIfAlias();
        } else if (eventToken.equalsIgnoreCase("interface")) {
            return e.getInterface();
        } else if (eventToken.equalsIgnoreCase("service")) {
            return e.getService();
        } else if (eventToken.equalsIgnoreCase("severity")) {
            return e.getSeverity();
        } else if (eventToken.equalsIgnoreCase("snmpHost")) {
            return e.getSnmphost();
        } else if (eventToken.equalsIgnoreCase("source")) {
            return e.getSource();
        } else if (eventToken.equalsIgnoreCase("logGroup")) {
            return e.getLogmsg().getContent();
        } else if (eventToken.equalsIgnoreCase("masterStation")) {
            return e.getMasterStation();
        } else if (eventToken.equalsIgnoreCase("mouseOverText")) {
            return e.getMouseovertext();
        } else if (eventToken.equalsIgnoreCase("operInstruct")) {
            return e.getOperinstruct();
        }
        return null;
    }

    /**
     * Use this method to verify that the event is quailified to be processed
     * by the passive status keeper.
     * 
     * @param e
     *      The event to be analyzed
     * @return
     *      true or false
     */
    public boolean isPassiveStatusEvent(Event e) {
        if (!getPassiveStatusUEIs().contains(e.getUei()))
            return false;
        log().debug("isPassiveStatusEvent: Received valid UEI: "+e.getUei()+", checking parms...");
        return passiveStatusEventContainsRequiredParms(e);
    }
    
	static class TranslationFailedException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		TranslationFailedException(String msg) {
    			super(msg);
    		}
    }

    public boolean isTranslationEvent(Event e) {
    	
		List specs = getTranslationSpecs();
		for (Iterator it = specs.iterator(); it.hasNext();) {
			TranslationSpec spec = (TranslationSpec) it.next();
			if (spec.matches(e))
				return true;
		}
		return false;
    }
    

	static private Category log() {
        return ThreadCategory.getInstance(PassiveStatusConfigFactory.class);
    }

    
    private String getValueFromFieldOrParm(Event e, String eventToken, boolean isParm) {
        String tokenValue;
        if (isParm == true) {
            tokenValue = EventUtil.getNamedParmValue("parm["+ eventToken +"]", e);
        } else {
            tokenValue = getEventField(eventToken, e);
        }
        return tokenValue;
    }

    /**
     * 
     * @param e
     * @param labels
     * @return
     */
    private boolean passiveStatusEventContainsRequiredParms(Event e) {
        
        /*
         * Check to see if the parms required by the configuration are actually
         * in the event.
         */
        boolean hasAllParms = false;
        List passiveStatusParmNames = getPassiveStatusParmNames(e);
        Parms parms = e.getParms();
        if (parms != null && passiveStatusParmNames != null) {
            List labelList = getParmsLabels(parms);
            if (labelList.containsAll(passiveStatusParmNames))
                hasAllParms = true;
        }
        log().debug("eventContainsRequiredParms: this passive event has all parms required in configuration: "+Boolean.toString(hasAllParms));
        return hasAllParms;
    }

	public List translateEvent(Event e) {
		EventTranslationSpec transSpec = getEventTranslationSpecByUei(e.getUei());
		if (transSpec == null) return null;
		
		List events = new ArrayList();
		List mappings = transSpec.getMappings().getMappingCollection();
		for (Iterator it = mappings.iterator(); it.hasNext();)
			try {
				Mapping mapping = (Mapping) it.next();
				Event translated = translateEvent(mapping, e);
				events.add(translated);
			} catch (TranslationFailedException ex) {
		}
		return events.size() > 0 ? events : null;

		
	}
    private Event translateEvent(Mapping mapping, Event e) {
    		Event translated = cloneEvent(e);
    		
    		assignAttributes(mapping.getParameterCollection(), e, translated);
    		assignAttributes(mapping.getFieldCollection(), e, translated);
    		
    		return translated;
	}

	private void assignAttributes(List attributes, Event e, Event translated) {
		for (Iterator it = attributes.iterator(); it.hasNext();) {
			Attribute attribute = (Attribute) it.next();
			assignAttribute(attribute, e, translated);
		}
	}

	private void assignAttribute(Attribute attr, Event e, Event translated) {
		if (attr.getFieldValue() != null)
			new FieldValueSpec(attr.getFieldValue()).assign(e, translated);
		else if (attr.getParameterValue() != null)
			new ParameterValueSpec(attr.getParameterValue()).assign(e, translated);
		else if (attr.getConstant() != null)
			new ConstantValueSpec(attr.getConstant()).assign(e, translated);
		
			
	}

	private static Event cloneEvent(Event orig) {
	       Event copy = null;
	        try {
	            // Write the object out to a byte array
	            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
	            ObjectOutputStream out = new ObjectOutputStream(bos);
	            out.writeObject(orig);
	            out.flush();
	            out.close();

	            // Make an input stream from the byte array and read
	            // a copy of the object back in.
	            ObjectInputStream in = new ObjectInputStream(
	                new ByteArrayInputStream(bos.toByteArray()));
	            copy = (Event)in.readObject();
	        }
	        catch(IOException e) {
	            log().error("Exception copying event", e);
	        }
	        catch(ClassNotFoundException cnfe) {
	            log().error("Exception copying event", cnfe);
	        }
	        return copy;
	}

    /**
     * Parses regular expressions and returns either the expr string
     * or the back reference(s) within the expression.  If the string begins
     * with "~", then it is treated as a regular expression begining with the
     * second character otherwise the expression is treated as a literal and
     * is returned without matching.
     * 
     * If no grouping is used in the string, then the entire match (group 0) is
     * returned.  If there is one or more groups, then the groups are returned
     * concatenated into one string.
     * 
     * Use the pattern to do a very limited printf style formatting of the string
     * using $1 - $9 to reference back references of the expr.  Example:
     *      value = "Channel 9"
     *      expr = "~^(Channel) (9)"
     *      formatPattern = "$1-$2"
     *      
     *      retValue will be: "Channel-9"
     *      
     * @param value
     * @param expr
     * @param formatPattern
     * @return a formatted regex/the literal/or empty string
     */
    public String parseExpression(String value, String expr, String formatPattern) {
        String retValue = "";
        if (expr.startsWith("~")) {
            Pattern p = Pattern.compile(expr.substring(1));
            Matcher m = p.matcher(value);
    
            if (m.matches()) {
                if (m.groupCount() == 0 || formatPattern == null) {
                    retValue = m.group(0);
                } else {
                    retValue = applyFormat(formatPattern, m);
                }
            }
            
        } else {
            //this, in-fact, makes the the field/parm value unused and takes
            //the literal of expr (the value in the config)
            retValue = expr;
        }
        
        return retValue;
    }

    /**
     * Use formatPattern to reference matching groups in the matcher m.  Use
     * '$[0-9]' to referenece a group in the matcher. 
     * 
     * @param formatPattern
     * @param m
     * @return a string representing the format in formatPattern
     */
    private String applyFormat(String formatPattern, Matcher m) {
        String retValue = "";
        //Loop through the expression looking for $
        for (int i=0; i<formatPattern.length(); i++) {
            String nextChar = formatPattern.substring(i, i+1);
            if (nextChar.equals("$") && i+1 <= formatPattern.length()) {
                nextChar = formatPattern.substring(++i, i+1);
                if (nextChar.matches("[0123456789]") && Integer.parseInt(nextChar) <= m.groupCount()) {
                    retValue += m.group(Integer.parseInt(nextChar));
                }
            } else {
                retValue += nextChar;
            }
        }
        return retValue;
    }

    /**
     * Returns a list of parms required in the passive status configuration
     * for this event.
     * 
     * @param e
     * @return parms
     *      List containing strings representing the names a parms required
     *      for the passive status key.
     */
    private List getPassiveStatusParmNames(Event e) {
        List parms = new ArrayList();
        PassiveEvent pe = getPassiveEventByUei(e.getUei());
        StatusKey key = pe.getStatusKey();
        if (key.getNodeLabel().getEventToken().getIsParm())
            parms.add(key.getNodeLabel().getEventToken().getName());
        if (key.getIpaddr().getEventToken().getIsParm())
            parms.add(key.getIpaddr().getEventToken().getName());
        if (key.getServiceName().getEventToken().getIsParm())
            parms.add(key.getServiceName().getEventToken().getName());
        if (key.getStatus().getEventToken().getIsParm())
            parms.add(key.getStatus().getEventToken().getName());
        return parms;
    }
    
    /**
     * Get the configured passive event based on UEI.
     * @param uei
     * @return pe
     *      the configured passive event
     */
    private PassiveEvent getPassiveEventByUei(String uei) {
        PassiveEvent pe = null;
        Collection eventList = m_config.getPassiveEvents().getPassiveEventCollection();
        for (Iterator iter = eventList.iterator(); iter.hasNext();) {
            PassiveEvent event = (PassiveEvent) iter.next();
            if (event.getUei().equals(uei)) {
                pe = event;
            }
        }
        return pe;
    }
    

    private List getParmsLabels(Parms parms) {
        List labels = new ArrayList();
        Collection parmColl = parms.getParmCollection();
        for (Iterator it = parmColl.iterator(); it.hasNext();) {
            labels.add(((Parm) it.next()).getParmName());
        }
        return labels;
    }

	private EventTranslationSpec getEventTranslationSpecByUei(String uei) {
        Collection eventList = m_config.getTranslation().getEventTranslationSpecCollection();
        for (Iterator iter = eventList.iterator(); iter.hasNext();) {
        	EventTranslationSpec transSpec = (EventTranslationSpec) iter.next();
            if (transSpec.getUei().equals(uei)) {
                return transSpec;
            }
        }
        return null;
	}
	
	private List getTranslationSpecs() {
		if (m_translationSpecs == null)
			m_translationSpecs = constructTranslationSpecs();
		
		return m_translationSpecs;
	}

	private List constructTranslationSpecs() {
		List specs = new ArrayList();
		for (Iterator it = m_config.getTranslation().getEventTranslationSpecCollection().iterator(); it.hasNext();) {
			EventTranslationSpec eventTrans = (EventTranslationSpec) it.next();
			specs.add(new TranslationSpec(eventTrans));
		}
		return specs;
	}
	
	static class TranslationSpec {
		private EventTranslationSpec m_spec;
		private List m_translationMappings;
		TranslationSpec(EventTranslationSpec spec) {
			m_spec = spec;
			m_translationMappings = null; // lazy init
		}
		String getUei() { return m_spec.getUei(); }
		public EventTranslationSpec getEventTranslationSpec() {
			return m_spec;
		}
		
		private List constructTranslationMappings() {
			if (m_spec.getMappings() == null) return Collections.EMPTY_LIST;

			List mappings = m_spec.getMappings().getMappingCollection();
			
			List transMaps = new ArrayList(mappings.size());
			for (Iterator it = mappings.iterator(); it.hasNext();) {
				Mapping mapping = (Mapping) it.next();
				TranslationMapping transMap = new TranslationMapping(mapping);
				transMaps.add(transMap);
			}
			
			return transMaps;
		}
		
		List getTranslationMappings() {
			if (m_translationMappings == null)
				m_translationMappings = constructTranslationMappings();
			return m_translationMappings;
		}
		boolean matches(Event e) {
			if (!m_spec.getUei().equals(e.getUei())) return false;
			
			List transMaps = getTranslationMappings();
			for (Iterator it = transMaps.iterator(); it.hasNext();) {
				TranslationMapping transMap = (TranslationMapping) it.next();
				if (transMap.matches(e))
					return true;
			}
			return false;
		}
		
		
	}
	
	static class TranslationMapping {
		Mapping m_mapping;
		List m_assignments;
		TranslationMapping(Mapping mapping) { 
			m_mapping = mapping;
			m_assignments = null; // lazy init
		}
		
		public Mapping getMapping() {
			return m_mapping;
		}
		
		private List getAssignmentSpecs() {
			if (m_assignments == null)
				m_assignments = constructAssignmentSpecs();
			return m_assignments;
		}
		
		private List constructAssignmentSpecs() {
			Mapping mapping = getMapping();
			List assignments = new ArrayList();
			for (Iterator iter = mapping.getParameterCollection().iterator(); iter.hasNext();) {
				Parameter param = (Parameter) iter.next();
				AssignmentSpec assignSpec = new ParameterAssignmentSpec(param);
				assignments.add(assignSpec);
			}
			for (Iterator iter = mapping.getFieldCollection().iterator(); iter.hasNext();) {
				Field field = (Field) iter.next();
				AssignmentSpec assignSpec = new FieldAssignmentSpec(field);
				assignments.add(assignSpec);
			}
			return assignments;
		}
		
		private boolean assignmentsMatch(Event e) {
			for (Iterator it = getAssignmentSpecs().iterator(); it.hasNext();) {
				AssignmentSpec assignSpec = (AssignmentSpec) it.next();
			
				if (!assignSpec.matches(e))
					return false;
			}	
			return true;
		}
		boolean matches(Event e) {
			return assignmentsMatch(e);
		}
	}
	
	static class AssignmentSpec {
		private Attribute m_attribute;
		private ValueSpec m_valueSpec;
		AssignmentSpec(Attribute attribute) {
			m_attribute = attribute; 
			m_valueSpec = null; // lazy init
		}
		private Attribute getAttribute() { return m_attribute; }
		private ValueSpec constructValueSpec() {
			if (getAttribute().getFieldValue() != null)
				return new FieldValueSpec(getAttribute().getFieldValue());
			else if (getAttribute().getParameterValue() != null)
				return new ParameterValueSpec(getAttribute().getParameterValue());
			else if (getAttribute().getConstant() != null)
				return new ConstantValueSpec(getAttribute().getConstant());
			else
				return new ValueSpecUnspecified();
		}
		private ValueSpec getValueSpec() {
			if (m_valueSpec == null)
				m_valueSpec = constructValueSpec();
			return m_valueSpec;
		}
		boolean matches(Event e) {
			return getValueSpec().matches(e);
		}
	}
	
	static class FieldAssignmentSpec extends AssignmentSpec {
		FieldAssignmentSpec(Field field) { super(field); }
		
	}
	
	static class ParameterAssignmentSpec extends AssignmentSpec {
		ParameterAssignmentSpec(Parameter param) { super(param); }
	}
	
	static abstract class ValueSpec {
		public abstract boolean matches(Event e);
		public abstract void assign(Event e, Event translated);
	}
	
	static class ConstantValueSpec extends ValueSpec {
		
		Constant m_constant;

		public ConstantValueSpec(Constant constant) {
			m_constant = constant;
		}
		

		public boolean matches(Event e) {
			return true;
		}

		public void assign(Event e, Event translated) {
		}
		
	}

	static class ValueSpecUnspecified extends ValueSpec {
		
		public boolean matches(Event e) {
			// TODO: this should probably throw an exception since it makes no sense
			return true;
		}

		public void assign(Event e, Event translated) {
			// TODO: this should probably throw an exception since it makes no sense
		}
		
	}

	static abstract class AttributeValueSpec extends ValueSpec {
		AttributeValue m_val;
		AttributeValueSpec(AttributeValue val) { m_val = val; }
		public boolean matches(Event e) {
			return valueMatches(getAttributeValue(m_val.getName(), e));
		}
		public void assign(Event e, Event translated) {}
		
		private boolean valueMatches(String attributeValue) {
			if (attributeValue == null) return false;
			if (m_val.getMatches() == null) return true;
			
			Pattern p = Pattern.compile(m_val.getMatches());
			Matcher m = p.matcher(attributeValue);
			
			return m.matches();
		}
		abstract public String getAttributeValue(String attrName, Event e);
	}
	
	static class FieldValueSpec extends AttributeValueSpec {
		FieldValueSpec(FieldValue val) { super(val); }
		public String getAttributeValue(String attrName, Event e) {
			PropertyDescriptor[] desc = BeanUtils.getPropertyDescriptors(Event.class);
			for (int i = 0; i < desc.length; i++) {
				PropertyDescriptor descriptor = desc[i];
				if (descriptor.getName().equals(attrName)) {
					
					try {
						Object result = descriptor.getReadMethod().invoke(e, new Object[0]);
						return result == null ? "" : result.toString();
					} catch (Exception e1) {
						log().error("Unable to retrieve l-value from event!", e1);
						return null;
					}
				}
			}
			log().warn("Attempt to retrive l-value for event field named: "+attrName+".  This field is invalid!");
			return null;
		}

	}
	
	static class ParameterValueSpec extends AttributeValueSpec {
		ParameterValueSpec(ParameterValue val) { super(val); }
		public String getAttributeValue(String attrName, Event e) {
			Parms parms = e.getParms();
			if (parms == null) return null;
			
			for (Iterator it = parms.getParmCollection().iterator(); it.hasNext();) {
				Parm parm = (Parm) it.next();
				if (parm.getParmName().equals(attrName))
					return (parm.getValue() == null ? "" : parm.getValue().getContent());
					
			}
			return null;
		}

	}
	


}
