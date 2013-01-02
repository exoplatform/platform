package org.exoplatform.platform.portlet.juzu.gettingstarted;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 12/31/12
 */
public class NodeHierarchyCreatorProvider implements FactoryBean<NodeHierarchyCreator> {

    public NodeHierarchyCreator getObject() throws Exception {
        return (NodeHierarchyCreator) PortalContainer.getInstance().getComponentInstanceOfType(NodeHierarchyCreator.class);
    }

    public Class<NodeHierarchyCreator> getObjectType() {
        return NodeHierarchyCreator.class;
    }

    public boolean isSingleton() {
        return false;
    }

}
