package org.exoplatform.platform.common.space.statistic;

/**
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

import org.apache.commons.lang.ArrayUtils;
import org.chromattic.api.ChromatticSession;
import org.chromattic.ext.ntdef.NTFolder;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 13/11/12
 */
public class LastSpaceAccessServiceImpl implements SpaceAccessService {
    private static final Log LOG = ExoLogger.getLogger(LastSpaceAccessServiceImpl.class);
    private ChromatticLifeCycle lifeCycle;
    private NodeHierarchyCreator nodeHierarchyCreator;
    private Executor executor;
    private static final String PARENT_PLATFORM_RELATIVE_PATH = "Platform";
    private static final String SEPARATOR = "@";
    private static final String PARENT_TOOLBAR_RELATIVE_PATH = "toolbar";
    private static final String PARENT_RELATIVE_PATH = PARENT_PLATFORM_RELATIVE_PATH + "/" + PARENT_TOOLBAR_RELATIVE_PATH;
    private final static String CHROMATTIC_LIFECYCLE_NAME = "spaceaccess";
    private final static String SPACE_ACCESS_NODE_NAME = "spaces-access-log";
    private final static String SPACE_ACCESS_LIFECYCLE_ROOT_PATH = "/Users/";

    public LastSpaceAccessServiceImpl(ChromatticManager chromatticManager,NodeHierarchyCreator nodeHierarchyCreator) {
        this.lifeCycle = chromatticManager.getLifeCycle(CHROMATTIC_LIFECYCLE_NAME);
        this.executor = Executors.newCachedThreadPool();
        this.nodeHierarchyCreator = nodeHierarchyCreator;
    }


    public void updateSpaceAccess(final String  spaceId, final String userId) {
        executor.execute(new Runnable() {
            public void run() {
                if (lifeCycle.getContext() == null) {
                    lifeCycle.openContext();
                }
                String parentNodePath = null;
                try {
                    parentNodePath = getUserApplicationDataNodePath(userId, true);
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
                SpaceAccess spaceAccess = getSession()
                        .findByPath(SpaceAccess.class, parentNodePath + "/" + SPACE_ACCESS_NODE_NAME, false);
                if (spaceAccess == null) {
                    NTFolder parentNode = getSession().findByPath(NTFolder.class, parentNodePath, false);
                    if (parentNode == null) {
                        throw new IllegalStateException("User ApplicationData node couldn't be found.");
                    }
                    spaceAccess = getSession().create(SpaceAccess.class, SPACE_ACCESS_NODE_NAME);
                    getSession().persist(parentNode, spaceAccess);
                    getSession().save();
                    spaceAccess = getSession().findByPath(SpaceAccess.class, parentNodePath + "/" + SPACE_ACCESS_NODE_NAME, false);
                }
                String[] spaces = spaceAccess.getSpaceAccessList();
                if (spaces == null || spaces.length == 0) {
                    spaces = (String[]) ArrayUtils.add(null, spaceId + SEPARATOR + "1" );
                    spaceAccess.setSpaceAccessList(spaces);
                    getSession().save();
                    return;
                }

                String prefix = spaceId + SEPARATOR;
                String spaceAccessEntry = null;
                int i = 0;
                while (i < spaces.length) {
                    String spaceAccessEntryTmp = spaces[i];
                    if (spaceAccessEntryTmp.startsWith(prefix)) {
                        spaceAccessEntry = spaceAccessEntryTmp;
                        break;
                    }
                    i++;
                }
                int k=i;
                if ((spaceAccessEntry != null)&&(i!=spaces.length-1)) {
                 //   for ( k = i; k < spaces.length-1; k++) spaces[k] = spaces[k + 1];
                    spaces=(String[]) ArrayUtils.remove(spaces, i);
                    spaces=(String[]) ArrayUtils.add(spaces, spaceAccessEntry);
                } else if (spaceAccessEntry == null){
                    spaces=(String[]) ArrayUtils.add(spaces, spaceId + SEPARATOR + "1");
                }
                spaceAccess.setSpaceAccessList(spaces);
                getSession().save();
            }
        });
    }

    public List<String> getSpaceAccessList(String userId) {
        String parentNodePath = getUserApplicationDataNodePath(userId, false);
        if (parentNodePath == null) {
            return new ArrayList<String>();
        }
        SpaceAccess spaceAccess = null;
        try {
            spaceAccess = getSession().findByPath(SpaceAccess.class, parentNodePath + "/" + SPACE_ACCESS_NODE_NAME, false);
        } catch (Exception exception) {
            LOG.error("spaceAccess for this user isn't yet created ", exception);
        }
        if (spaceAccess == null || spaceAccess.getSpaceAccessList() == null || spaceAccess.getSpaceAccessList().length == 0) {
            return new ArrayList<String>();
        }
        String[] spaces = spaceAccess.getSpaceAccessList();
        List<String> spacesList = Arrays.asList(spaces);
        int i = 0;
        while (i < spacesList.size()) {
            String space = spacesList.get(i);
            space = space.split(SEPARATOR)[0];
            spacesList.set(i, space);
            i++;
        }
        return spacesList;
    }

    private String getUserApplicationDataNodePath(String userId, boolean create) {
        String parentNodePath = null;
        try {
            Node userApplicationNode = nodeHierarchyCreator.getUserApplicationNode(SessionProvider.createSystemProvider(), userId);
            if (!userApplicationNode.hasNode(PARENT_RELATIVE_PATH)) {
                if (create) {
                    if (!userApplicationNode.hasNode(PARENT_PLATFORM_RELATIVE_PATH)) {
                        userApplicationNode.addNode(PARENT_PLATFORM_RELATIVE_PATH, "nt:folder");
                    }
                    userApplicationNode = userApplicationNode.addNode(PARENT_RELATIVE_PATH, "nt:folder");
                    userApplicationNode.addMixin("mix:referenceable");
                    userApplicationNode.getSession().save();
                    parentNodePath = userApplicationNode.getPath();
                } else {
                    return null;
                }
            } else {
                parentNodePath = userApplicationNode.getPath() + "/" + PARENT_RELATIVE_PATH;
            }
            parentNodePath = parentNodePath.split(SPACE_ACCESS_LIFECYCLE_ROOT_PATH, 2)[1];
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return parentNodePath;
    }

    public ChromatticSession getSession() {
        return lifeCycle.getChromattic().openSession();
    }
}
