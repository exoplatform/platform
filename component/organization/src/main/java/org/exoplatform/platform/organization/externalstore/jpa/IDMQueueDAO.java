package org.exoplatform.platform.organization.externalstore.jpa;

import java.util.List;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;

public class IDMQueueDAO extends GenericDAOJPAImpl<IDMQueueEntity, Long> {

  public int countAllNotProcessedAndMaxNbRetries(int maxRetries) {
    return getEntityManager().createNamedQuery("IDMQueueEntity.countAllNotProcessedAndMaxNbRetries", Long.class)
                             .setParameter("nbRetries", maxRetries)
                             .getSingleResult()
                             .intValue();
  }

  public int countAllNotProcessedAndNbRetries(int nbRetries) {
    return getEntityManager().createNamedQuery("IDMQueueEntity.countAllNotProcessedAndNbRetries", Long.class)
                             .setParameter("nbRetries", nbRetries)
                             .getSingleResult()
                             .intValue();
  }

  public List<IDMQueueEntity> getEntriesNotProcessedWithNBRetries(int nbRetries, int limit) {
    return getEntityManager().createNamedQuery("IDMQueueEntity.getEntriesNotProcessedWithNBRetries", IDMQueueEntity.class)
                             .setParameter("nbRetries", nbRetries)
                             .setMaxResults(limit)
                             .getResultList();
  }

  @ExoTransactional
  public void setProcessed(List<Long> ids) {
    getEntityManager().createNamedQuery("IDMQueueEntity.setEntriesAsProcessed").setParameter("ids", ids).executeUpdate();
  }

  @ExoTransactional
  public void incrementRetry(List<Long> ids) {
    getEntityManager().createNamedQuery("IDMQueueEntity.incrementEntriesRetry").setParameter("ids", ids).executeUpdate();
  }

  @ExoTransactional
  public void deleteProcessedEntries() {
    getEntityManager().createNamedQuery("IDMQueueEntity.deleteProcessedEntries").executeUpdate();
  }

  @ExoTransactional
  public void deleteExceededRetriesEntries(int maxRetries) {
    getEntityManager().createNamedQuery("IDMQueueEntity.deleteExceededRetriesEntries")
                      .setParameter("maxRetries", maxRetries)
                      .executeUpdate();
  }

}
