package org.exoplatform.platform.upgrade.plugins.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import javax.jcr.NamespaceRegistry;

import org.apache.commons.codec.binary.Hex;

import org.exoplatform.services.database.utils.DialectConstants;
import org.exoplatform.services.database.utils.DialectDetecter;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.impl.storage.jdbc.DBConstants;
import org.exoplatform.services.jcr.impl.storage.jdbc.JDBCDataContainerConfig.DatabaseStructureType;
import org.exoplatform.services.jcr.impl.storage.jdbc.JDBCWorkspaceDataContainer;
import org.exoplatform.services.jcr.impl.util.jdbc.DBInitializerHelper;

/**
 * Created by The eXo Platform SAS Author : Boubaker Khanfir
 * bkhanfir@exoplatform.com April 16, 2016
 */
public class Utils {

  public static final String DEFAULT_WORKSPACE_NAME   = "social";

  public static final String SQL_BASE                 = " sitem." + DBConstants.COLUMN_PARENTID + " from JCR_SVALUE svalue "
                                                          + "inner join JCR_SITEM sitem on sitem." + DBConstants.COLUMN_ID
                                                          + " = svalue." + DBConstants.COLUMN_VPROPERTY_ID + "  and sitem."
                                                          + DBConstants.COLUMN_NAME
                                                          + " = '[http://www.jcp.org/jcr/1.0]mixinTypes'  " + "  and sitem."
                                                          + DBConstants.COLUMN_CLASS + " = 2 " + " @CONTAINER_CONDITION@ "
                                                          + "  where @CONDITION@  ";

  public static final String CONTAINER_CONDITION      = "and sitem." + DBConstants.CONTAINER_NAME + "= '@WS_NAME@'";

  public static final String MYSQL_QUERY              = "select distinct " + SQL_BASE + "limit @LIMIT@";

  public static final String MYSQL_QUERY_CONDITION    = "CONVERT(svalue.DATA USING UTF8) = '@VALUE@'";

  public static final String POSTGRES_QUERY           = "select distinct " + SQL_BASE + "limit @LIMIT@";

  public static final String POSTGRES_QUERY_CONDITION = "convert_from(svalue.DATA , 'UTF8') = '@VALUE@'";

  public static final String MSSQL_QUERY              = "select TOP @LIMIT@ " + SQL_BASE;

  public static final String MSSQL_QUERY_CONDITION    = "convert_from(svalue.DATA , 'UTF8') = '@VALUE@'";

  public static final String ORACLE_QUERY             = "select distinct " + SQL_BASE + "    AND ROWNUM < @LIMIT@";

  public static final String ORACLE_QUERY_CONDITION   = "UTL_RAW.CAST_TO_VARCHAR2(DBMS_LOB.SUBSTR(svalue.DATA, 100,1)) = '@VALUE@'";

  public static final String HSQLDB_QUERY             = "select distinct " + SQL_BASE + "limit @LIMIT@";

  public static final String HSQLDB_QUERY_CONDITION   = "RAWTOHEX(svalue.DATA) = '@VALUE@'";

  public static String getQuery(Connection jdbcConn,
                                ManageableRepository repository,
                                String workspaceName,
                                Set<String> mixinNames,
                                int fetchSize) throws Exception {
    WorkspaceEntry wsEntry = Utils.getWorkspaceEntry(repository, workspaceName);
    NamespaceRegistry namespaceRegistry = repository.getNamespaceRegistry();

    String dialect = resolveDialect(jdbcConn, wsEntry);
    if (dialect.startsWith(DialectConstants.DB_DIALECT_MYSQL)) {
      return getQuery(wsEntry, namespaceRegistry, Utils.MYSQL_QUERY, Utils.MYSQL_QUERY_CONDITION, mixinNames, fetchSize, false);
    } else if (dialect.startsWith(DialectConstants.DB_DIALECT_ORACLE)) {
      return getQuery(wsEntry, namespaceRegistry, Utils.ORACLE_QUERY, Utils.ORACLE_QUERY_CONDITION, mixinNames, fetchSize, false);
    } else if (dialect.startsWith(DialectConstants.DB_DIALECT_MSSQL)) {
      return getQuery(wsEntry, namespaceRegistry, Utils.MSSQL_QUERY, Utils.MSSQL_QUERY_CONDITION, mixinNames, fetchSize, false);
    } else if (dialect.startsWith(DialectConstants.DB_DIALECT_PGSQL)) {
      return getQuery(wsEntry,
                      namespaceRegistry,
                      Utils.POSTGRES_QUERY,
                      Utils.POSTGRES_QUERY_CONDITION,
                      mixinNames,
                      fetchSize,
                      false);
    } else if (dialect.startsWith(DialectConstants.DB_DIALECT_HSQLDB)) {
      return getQuery(wsEntry, namespaceRegistry, Utils.HSQLDB_QUERY, Utils.HSQLDB_QUERY_CONDITION, mixinNames, fetchSize, true);
    }
    return null;
  }

  public static WorkspaceEntry getWorkspaceEntry(ManageableRepository currentRepo, String workspaceName) {
    WorkspaceEntry wEntry = null;
    for (WorkspaceEntry entry : currentRepo.getConfiguration().getWorkspaceEntries()) {
      if (entry.getName().equals(workspaceName)) {
        wEntry = entry;
        break;
      }
    }
    if (wEntry == null) {
      throw new IllegalStateException("Worksapce \"" + workspaceName + "\" was not found.");
    }
    return wEntry;
  }

  private static String getQuery(WorkspaceEntry wsEntry,
                                 NamespaceRegistry namespaceRegistry,
                                 String queryBase,
                                 String conditionBase,
                                 Set<String> mixinNames,
                                 int fetchSize,
                                 boolean useHex) throws Exception {
    String query = queryBase;

    DatabaseStructureType structureType = DatabaseStructureType.SINGLE;
    if (wsEntry.getContainer().hasParameter(JDBCWorkspaceDataContainer.DB_STRUCTURE_TYPE)) {
      structureType = DatabaseStructureType.valueOf(wsEntry.getContainer()
                                                           .getParameter(JDBCWorkspaceDataContainer.DB_STRUCTURE_TYPE)
                                                           .getValue()
                                                           .toUpperCase());
    }

    if (DatabaseStructureType.SINGLE.equals(structureType)) {
      query = query.replaceAll("@CONTAINER_CONDITION@", CONTAINER_CONDITION);
    } else {
      query = query.replaceAll("@CONTAINER_CONDITION@", "");
    }
    query = query.replaceAll("@LIMIT@", "" + fetchSize);
    query = query.replaceAll("JCR_SITEM", DBInitializerHelper.getItemTableName(wsEntry));
    query = query.replaceAll("JCR_SVALUE", DBInitializerHelper.getValueTableName(wsEntry));
    query = query.replaceAll("@WS_NAME@", wsEntry.getName());

    StringBuilder condition = new StringBuilder("(");
    boolean appendOr = false;
    for (String mixinType : mixinNames) {
      if (appendOr) {
        condition.append(" OR ");
      } else {
        appendOr = true;
      }
      mixinType = "[" + namespaceRegistry.getURI(mixinType.substring(0, mixinType.indexOf(':'))) + "]"
          + mixinType.substring(mixinType.indexOf(':') + 1);
      if (useHex) {
        mixinType = Hex.encodeHexString(mixinType.getBytes());
      }
      condition.append(conditionBase.replace("@VALUE@", mixinType));
    }
    condition.append(")");
    query = query.replace("@CONDITION@", condition.toString());
    return query;
  }

  private static String resolveDialect(Connection jdbcConn, WorkspaceEntry wsEntry) {
    String dialect = DBInitializerHelper.getDatabaseDialect(wsEntry);
    if (dialect.startsWith(DBConstants.DB_DIALECT_AUTO)) {
      try {
        dialect = DialectDetecter.detect(jdbcConn.getMetaData());
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    return dialect;
  }

}
