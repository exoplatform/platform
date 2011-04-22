package org.exoplatform.services.security.jaas.weblogic.rolemapper; 

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import weblogic.management.security.ProviderMBean;
import weblogic.security.service.ContextHandler;
import weblogic.security.service.SecurityRole;
import weblogic.security.spi.ApplicationInfo;
import weblogic.security.spi.ApplicationRemovalException;
import weblogic.security.spi.ApplicationVersionCreationException;
import weblogic.security.spi.ApplicationVersionRemovalException;
import weblogic.security.spi.DeployHandleCreationException;
import weblogic.security.spi.DeployRoleHandle;
import weblogic.security.spi.DeployableRoleProviderV2;
import weblogic.security.spi.Resource;
import weblogic.security.spi.RoleCreationException;
import weblogic.security.spi.RoleMapper;
import weblogic.security.spi.RoleRemovalException;
import weblogic.security.spi.SecurityServices;
import weblogic.security.spi.VersionableApplicationProvider;
import weblogic.security.spi.WLSGroup;

public final class ExoRoleMapperProviderImpl implements DeployableRoleProviderV2, RoleMapper, VersionableApplicationProvider {
        
	private String description;

    //private static final Map NO_ROLES = Collections.unmodifiableMap(new HashMap(1));

    public void initialize(ProviderMBean mbean, SecurityServices services) {
    }

    public String getDescription() {
        return description;
    }

    public void shutdown() {
    }

    public RoleMapper getRoleMapper() {
        return this;
    }

    /**
     * Determines what roles the current subject is in
     * for this resource.
     *
     * @param subject A Subject that contains the Principals that identify
     * the user who is trying to access the resource as well as the user's
     * groups.
     *
     * @param resource The Resource the Subject is trying to access.
     *
     * @param handler A ContextHandler contains additional information that
     * may be considered in computing the roles.  This parameter is not used
     * by the simple sample role mapper.
     *
     * @return a Map containing SecurityRoles identifying the computed roles.
     * The SecurityRoles are instances of the simple sample identity asserter's
     * SecurityRole implementation (SimpleSampleSecurityRoleImpl).
     *
     * @see RoleMapper
    */
    @SuppressWarnings("unchecked")
    public Map getRoles(Subject subject, Resource resource, ContextHandler handler) {

    	// each group is a role

    	Map<String, SecurityRole> roles = new HashMap<String, SecurityRole>();

        Set<WLSGroup> principalsWLSGroup = subject.getPrincipals(weblogic.security.spi.WLSGroup.class);
        
        for(WLSGroup group : principalsWLSGroup) {
        	String strGroupName = group.getName();
        	roles.put(strGroupName, new ExoSecurityRoleImpl(strGroupName));
        }      

        return roles;
    }

	public void deleteApplicationRoles(ApplicationInfo arg0)
			throws RoleRemovalException {
	}
	
    /**
     * Stores the role definitions specified in a deployed webapp or EJB
     * in the simple sample role mapper's properties file.
     *
     * A webapp or EJB's web.xml file may contain a list of roles that
     * is allowed to access the webapp or EJB.  The webapp or EJB may have
     * a weblogic.xml file that maps the role name to a list of users and
     * groups that are in that role.  If weblogic.xml doesn't exist, then
     * the role defaults to any user or group whose name matches the role
     * name.
     *
     * When the webapp or EJB is deployed, this information is sent to
     * this call so that the role mapper provider can find the roles for
     * these webapps and EJBs.
     *
     * This method will replace the role definition on the resource
     * if there is already one specified.
     * 
     * @param resource A Resource that identifies the webapp or EJB.
     *
     * @param roleName A String containing the name of the role
     * (scoped by this resource).
     *
     * @param principalNames An array of String containing the users and
     * groups that are in this role on this resource (that is, the role
     * definition).
     *
     */
	public void deployRole(DeployRoleHandle deployHandler, Resource resource, String roleName,
			String[] principalNames) throws RoleCreationException {    
   	}
	
	public void endDeployRoles(DeployRoleHandle arg0) throws RoleCreationException {	
	}
	
	public DeployRoleHandle startDeployRoles(ApplicationInfo arg0)
			throws DeployHandleCreationException {
		return null;
	}

	  /**
	   * Removes the role definitions for an undeployed webapp or EJB
	   * from the simple sample role mapper's properties file.
	   *
	   * A webapp or EJB's web.xml file may contain a list of roles that
	   * is allowed to access the webapp or EJB.  When the webapp or EJB
	   * is undeployed, this provider is notified so that it may remove
	   * any role definitions it is holding on behalf of the webapp
	   * or EJB.
	   *
	   * This method will do nothing if it doesn't have a definition
	   * for this role.
	   * 
	   * @param resource A Resource that identifies the webapp or EJB.
	   *
	   * @param roleName A String containing the name of the role
	   * (scoped by this resource).
	   */
	public void undeployAllRoles(DeployRoleHandle arg0)
			throws RoleRemovalException {
	}

    public void createApplicationVersion(String arg0, String arg1)
		throws ApplicationVersionCreationException {
	}
	
	public void deleteApplication(String arg0)
		throws ApplicationRemovalException {
	}
	
	public void deleteApplicationVersion(String arg0)
		throws ApplicationVersionRemovalException {
	}

}