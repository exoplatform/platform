package org.exoplatform.platform.common.space.statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jcr.Node;

import org.apache.commons.lang.ArrayUtils;
import org.chromattic.api.ChromatticSession;
import org.chromattic.ext.ntdef.NTFolder;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;

public class SpaceAccessService {
  private static final String PARENT_PLATFORM_RELATIVE_PATH = "Platform";
  private static final String PARENT_TOOLBAR_RELATIVE_PATH = "toolbar";
  private static final String PARENT_RELATIVE_PATH = PARENT_PLATFORM_RELATIVE_PATH + "/" + PARENT_TOOLBAR_RELATIVE_PATH;
  private static final String SEPARATOR = "@";
  private final static String CHROMATTIC_LIFECYCLE_NAME = "spaceaccess";
  private final static String SPACE_ACCESS_NODE_NAME = "spaces-access-log";
  private final static String SPACE_ACCESS_LIFECYCLE_ROOT_PATH = "/Users/";
  private static final Comparator<String> spaceAccessComparator = new Comparator<String>() {
    public int compare(String o1, String o2) {
      o1 = o1.split(SEPARATOR)[1];
      o2 = o2.split(SEPARATOR)[1];
      return o1.compareTo(o2);
    }
  };

  private static final ThreadLocal<ChromatticSession> session = new ThreadLocal<ChromatticSession>();
  private ChromatticLifeCycle lifeCycle;
  private NodeHierarchyCreator nodeHierarchyCreator;
  private Executor executor;

  public SpaceAccessService(ChromatticManager chromatticManager, NodeHierarchyCreator nodeHierarchyCreator) {
    this.lifeCycle = chromatticManager.getLifeCycle(CHROMATTIC_LIFECYCLE_NAME);
    this.executor = Executors.newCachedThreadPool();
    this.nodeHierarchyCreator = nodeHierarchyCreator;
  }

  public void incrementSpaceAccess(final String spaceId, final String userId) {
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
        String[] spaces = spaceAccess.getMostAccessedSpaces();
        if (spaces == null || spaces.length == 0) {
          spaces = (String[]) ArrayUtils.add(null, spaceId + SEPARATOR + "1");
          spaceAccess.setMostAccessedSpaces(spaces);
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
        if (spaceAccessEntry != null) {
          String[] spaceKeys = spaceAccessEntry.split(SEPARATOR);
          int spaceAccessCount = Integer.parseInt(spaceKeys[1]);
          spaceAccessCount++;
          spaceAccessEntry = spaceKeys[0] + SEPARATOR + spaceAccessCount;
          spaces[i] = spaceAccessEntry;
        } else {
          spaces = (String[]) ArrayUtils.add(spaces, spaceId + SEPARATOR + "1");
        }
        Arrays.sort(spaces, spaceAccessComparator);
        spaceAccess.setMostAccessedSpaces(spaces);
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
      // spaceAccess for this user isn't yet created
    }
    if (spaceAccess == null || spaceAccess.getMostAccessedSpaces() == null || spaceAccess.getMostAccessedSpaces().length == 0) {
      return new ArrayList<String>();
    }
    String[] spaces = spaceAccess.getMostAccessedSpaces();
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
    if (session.get() == null) {
      session.set(lifeCycle.getChromattic().openSession());
    }
    return session.get();
  }

}
