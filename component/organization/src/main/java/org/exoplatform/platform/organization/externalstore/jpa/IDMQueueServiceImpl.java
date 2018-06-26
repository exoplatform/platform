package org.exoplatform.platform.organization.externalstore.jpa;

import java.time.*;
import java.util.*;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.externalstore.IDMQueueService;
import org.exoplatform.services.organization.externalstore.model.IDMEntityType;
import org.exoplatform.services.organization.externalstore.model.IDMQueueEntry;

/**
 * IDM Queue Service implementation using JPA
 */
public class IDMQueueServiceImpl implements IDMQueueService {

  private static final Scope  IDM_SCOPE                        = Scope.APPLICATION.id("IDM");

  private static final Log    LOG                              = ExoLogger.getLogger(IDMQueueServiceImpl.class);

  private static final String IDM_QUEUE_PROCESSING_MAX_RETRIES = "exo.idm.queue.processing.error.retries.max";

  private static final int    DEFAULT_MAX_RETRIES              = 5;

  private int                 maxRetries                       = DEFAULT_MAX_RETRIES;

  private IDMQueueDAO         queueDAO;

  private SettingService      settingService;

  public IDMQueueServiceImpl(IDMQueueDAO queueDAO, SettingService settingService, InitParams params) {
    this.settingService = settingService;
    this.queueDAO = queueDAO;

    if (params != null && params.containsKey(IDM_QUEUE_PROCESSING_MAX_RETRIES)) {
      String maxRetriesString = params.getValueParam(IDM_QUEUE_PROCESSING_MAX_RETRIES).getValue();
      try {
        maxRetries = Integer.parseInt(maxRetriesString);
      } catch (NumberFormatException e) {
        LOG.warn("Unable to parse max retries " + maxRetriesString + ". Default value " + DEFAULT_MAX_RETRIES + " will be used",
                 e);
      }
    }
  }

  @Override
  public LocalDateTime getLastCheckedTime(IDMEntityType<?> entityType) {
    if (entityType == null) {
      throw new IllegalArgumentException("entityType is null");
    }
    SettingValue<?> settingValue = settingService.get(Context.GLOBAL,
                                                      IDM_SCOPE,
                                                      "DATE-" + entityType.getClassType().getSimpleName());
    if (settingValue == null) {
      return null;
    } else {
      Object value = settingValue.getValue();
      long timestamp;
      if (value instanceof Long) {
        timestamp = (Long) value;
      } else if (value instanceof String) {
        timestamp = Long.parseLong(String.valueOf(value));
      } else {
        LOG.warn("value of type" + value.getClass().getSimpleName() + " is not recognized");
        return null;
      }
      LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC.normalized());
      return dateTime;
    }
  }

  @Override
  public void setLastCheckedTime(IDMEntityType<?> entityType, LocalDateTime dateTime) {
    if (entityType == null) {
      throw new IllegalArgumentException("entityType is null");
    }
    if (dateTime == null) {
      settingService.remove(Context.GLOBAL, IDM_SCOPE, "DATE-" + entityType.getClassType().getSimpleName());
    } else {
      SettingValue<Long> value = SettingValue.create(dateTime.toInstant(ZoneOffset.UTC).toEpochMilli());
      settingService.set(Context.GLOBAL, IDM_SCOPE, "DATE-" + entityType.getClassType().getSimpleName(), value);
    }
  }

  @Override
  public int countAll() throws Exception {
    return queueDAO.countAllNotProcessedAndMaxNbRetries(getMaxRetries());
  }

  @Override
  public int count(int nbRetries) throws Exception {
    return queueDAO.countAllNotProcessedAndNbRetries(nbRetries);
  }

  @Override
  @ExoTransactional
  public void push(IDMQueueEntry queueEntry) throws Exception {
    IDMQueueEntity idmQueueEntity = convertToEntity(queueEntry);
    queueDAO.create(idmQueueEntity);
  }

  @Override
  @ExoTransactional
  public List<IDMQueueEntry> pop(int limit, int nbRetries, boolean keepInQueue) throws Exception {
    List<IDMQueueEntity> queueEntries = queueDAO.getEntriesNotProcessedWithNBRetries(nbRetries, limit);
    if (queueEntries == null || queueEntries.size() == 0) {
      return Collections.emptyList();
    }
    List<IDMQueueEntry> entries = new ArrayList<>();
    for (IDMQueueEntity idmQueueEntity : queueEntries) {
      entries.add(convertToDTO(idmQueueEntity));
    }
    if (!keepInQueue) {
      queueDAO.deleteAll(queueEntries);
    }
    return entries;
  }

  @Override
  @ExoTransactional
  public void storeAsProcessed(List<IDMQueueEntry> queueEntries) {
    if (queueEntries == null || queueEntries.isEmpty()) {
      return;
    }
    List<Long> ids = new ArrayList<>();
    for (IDMQueueEntry queueEntry : queueEntries) {
      queueEntry.setProcessed(true);
      if (queueEntry.getId() <= 0) {
        LOG.warn("ID of entry {} couldn't be 0", queueEntry);
      }
      ids.add(queueEntry.getId());
    }
    queueDAO.setProcessed(ids);
  }

  @Override
  @ExoTransactional
  public void incrementRetry(List<IDMQueueEntry> queueEntries) {
    if (queueEntries == null || queueEntries.isEmpty()) {
      return;
    }
    List<Long> ids = new ArrayList<>();
    for (IDMQueueEntry queueEntry : queueEntries) {
      queueEntry.setRetryCount(queueEntry.getRetryCount() + 1);
      if (queueEntry.getId() <= 0) {
        LOG.warn("ID of entry {} couldn't be 0", queueEntry);
      }
      ids.add(queueEntry.getId());
    }
    queueDAO.incrementRetry(ids);
  }

  @Override
  @ExoTransactional
  public void deleteProcessedEntries() {
    queueDAO.deleteProcessedEntries();
  }

  @Override
  @ExoTransactional
  public void deleteExceededRetriesEntries() {
    queueDAO.deleteExceededRetriesEntries(getMaxRetries());
  }

  @Override
  public int getMaxRetries() {
    return maxRetries;
  }

  private IDMQueueEntity convertToEntity(IDMQueueEntry queueEntry) {
    IDMQueueEntity idmQueueEntity = new IDMQueueEntity();
    idmQueueEntity.setCreationDate(queueEntry.getCreationDate() == null ? Calendar.getInstance() : queueEntry.getCreationDate())
                  .setId(queueEntry.getId())
                  .setEntityType(queueEntry.getEntityType().getTypeIndex())
                  .setEntityId(queueEntry.getEntityId())
                  .setNbRetries(queueEntry.getRetryCount())
                  .setType(queueEntry.getOperationType())
                  .setProcessed(queueEntry.isProcessed());
    return idmQueueEntity;
  }

  private IDMQueueEntry convertToDTO(IDMQueueEntity idmQueueEntity) {
    IDMQueueEntry queueEntry = new IDMQueueEntry();
    queueEntry.setId(idmQueueEntity.getId())
              .setCreationDate(idmQueueEntity.getCreationDate())
              .setEntityType(IDMEntityType.getEntityType(idmQueueEntity.getEntityType()))
              .setEntityId(idmQueueEntity.getEntityId())
              .setRetryCount(idmQueueEntity.getNbRetries())
              .setOperationType(idmQueueEntity.getType())
              .setProcessed(idmQueueEntity.isProcessed());
    return queueEntry;
  }

}
