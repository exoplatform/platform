package org.exoplatform.platform.common.space.statistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.lang.ArrayUtils;
import org.chromattic.api.ChromatticSession;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;
import org.exoplatform.commons.chromattic.ChromatticManager;

public class SpaceAccessService {
  private static final String SEPARATOR = "@";
  private static final Comparator<String> spaceAccessComparator = new Comparator<String>() {
    public int compare(String o1, String o2) {
      o1 = o1.split(SEPARATOR)[1];
      o2 = o2.split(SEPARATOR)[1];
      return o1.compareTo(o2);
    }
  };
  private final static String CHROMATTIC_LIFECYCLE_NAME = "spaceaccess";

  private static final ThreadLocal<ChromatticSession> session = new ThreadLocal<ChromatticSession>();
  private ChromatticLifeCycle lifeCycle;
  private Executor executor;

  public SpaceAccessService(ChromatticManager chromatticManager) {
    this.lifeCycle = chromatticManager.getLifeCycle(CHROMATTIC_LIFECYCLE_NAME);
    this.executor = Executors.newCachedThreadPool();
  }

  public void incrementSpaceAccess(final String spaceId, final String userId) {
    executor.execute(new Runnable() {
      public void run() {
        if (lifeCycle.getContext() == null) {
          lifeCycle.openContext();
        }
        SpaceAccess spaceAccess = getSession().findByPath(SpaceAccess.class, userId);
        if (spaceAccess == null) {
          spaceAccess = getSession().create(SpaceAccess.class, userId);
          getSession().persist(spaceAccess);
          getSession().save();
          spaceAccess = getSession().findByPath(SpaceAccess.class, userId);
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
    SpaceAccess spaceAccess = getSession().findByPath(SpaceAccess.class, userId);
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

  public ChromatticSession getSession() {
    if (session.get() == null) {
      session.set(lifeCycle.getChromattic().openSession());
    }
    return session.get();
  }

}
