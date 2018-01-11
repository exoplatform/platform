package org.exoplatform.platform.migration;

import org.exoplatform.commons.upgrade.UpgradeProductPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.database.HibernateService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.idm.PicketLinkIDMCacheService;
import org.exoplatform.services.organization.idm.PicketLinkIDMOrganizationServiceImpl;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;

import java.sql.*;

/**
 * Upgrade class used to remove IDM attribute enabled (From table jbid_io_attr ) when value equal to TRUE
 * (From table jbid_io_attr_text_values)
 */
public class EnableUserUpgradePlugin extends UpgradeProductPlugin {
    private static final Log LOG = ExoLogger.getLogger(EnableUserUpgradePlugin.class);
    private HibernateService hibernateService;
    private PicketLinkIDMCacheService picketLinkIDMCacheService;
    private OrganizationService organizationService;

    /**
     * Count query where name = enabled and value TRUE
     */
    private final String SELECT_COUNT_ENABLE_ATTRIBUTE = "select count(*) from jbid_io_attr  inner join jbid_io_attr_text_values  " +
            "on jbid_io_attr.ATTRIBUTE_ID =jbid_io_attr_text_values.TEXT_ATTR_VALUE_ID where NAME='enabled' and ATTR_VALUE='true'";

    /**
     * Select all attribute id where name = enabled and value TRUE
     */
    private final String SELECT_ENABLE_ATTRIBUTE = "select ATTRIBUTE_ID from jbid_io_attr  inner join jbid_io_attr_text_values  " +
            "on jbid_io_attr.ATTRIBUTE_ID =jbid_io_attr_text_values.TEXT_ATTR_VALUE_ID where NAME='enabled' and ATTR_VALUE='true'";
    /**
     * Remove attribute  item from jbid_io_attr
     */
    private final String REMOVE_ENABLE_ATTRIBUTE = "delete from jbid_io_attr where ATTRIBUTE_ID IN ( ";
    /**
     * Remove attribute  value from jbid_io_attr_text_values
     */
    private final String REMOVE_ENABLE_ATTRIBUTE_VALUE = "delete from jbid_io_attr_text_values where TEXT_ATTR_VALUE_ID IN (";
    private final String COLUMN_ID = "ATTRIBUTE_ID";

    private PreparedStatement findItemsStatement = null;
    private PreparedStatement countItemsStatement = null;
    private PreparedStatement removeItemStatement = null;
    private PreparedStatement removeValueStatement = null;


    public EnableUserUpgradePlugin(InitParams initParams, HibernateService hibernateService,
                                   OrganizationService organizationService, PicketLinkIDMCacheService picketLinkIDMCacheService) {
        super(initParams);
        this.hibernateService = hibernateService;
        this.picketLinkIDMCacheService = picketLinkIDMCacheService;
        this.organizationService = organizationService;
    }

    @Override
    public boolean isEnabled(){
        return true;
    }

    @Override
    public void processUpgrade(String oldVersion, String newVersion) {

        Session session = hibernateService.openNewSession();

        ResultSet rs = null;
        ResultSet count = null;
        Connection connection = null;
        boolean auto = false;
        boolean success = false;
        int nb = 0;
        long started = System.currentTimeMillis();

        try {
            connection = session.doReturningWork(new ReturningWork<Connection>() {
                @Override
                public Connection execute(Connection conn) throws SQLException {
                    return conn;
                }
            });
            if (connection != null) {
                auto = connection.getAutoCommit();
                connection.setAutoCommit(false);
                countItemsStatement = connection.prepareStatement(SELECT_COUNT_ENABLE_ATTRIBUTE);
                count = countItemsStatement.executeQuery();
                if (count.next() && count.getInt(1) > 0) {
                    nb = count.getInt(1);

                    findItemsStatement = connection.prepareStatement(SELECT_ENABLE_ATTRIBUTE);
                    findItemsStatement.setFetchSize(1000);

                    LOG.info("Start Select items NAME=enabled and ATTR_VALUE=true");
                    rs = findItemsStatement.executeQuery();


                    StringBuilder temp= new StringBuilder();
                    int i = 0;

                    while (rs.next()) {
                        i++;
                        temp.append(rs.getString(COLUMN_ID));
                        if (i % 1000 == 0) {
                            removeBatch(connection, temp.toString());
                            connection.commit();
                            LOG.info("Clean in progress : {}/{}", i, nb);
                            temp = new StringBuilder();
                        }else{
                            temp.append(",");
                        }
                    }
                    if(i% 1000 != 0){
                        removeBatch(connection, temp.substring(0, temp.lastIndexOf(",")));
                        connection.commit();
                        LOG.info("Clean in progress : {}/{}", i, nb);
                    }
                }
                success = true;
            }
        } catch (SQLException e) {
            LOG.error("Error while Clean items", e);
        } finally {
            if(picketLinkIDMCacheService != null){
                picketLinkIDMCacheService.invalidateAll();
            }
            if(organizationService!= null && organizationService instanceof PicketLinkIDMOrganizationServiceImpl){
                ((PicketLinkIDMOrganizationServiceImpl)organizationService).clearCaches();
            }
            if (connection != null) {
                try {
                    connection.setAutoCommit(auto);
                } catch (SQLException e) {
                    LOG.error("Can't change auto commit", e);
                }
            }
            if(count != null){
                try {
                    count.close();
                } catch (SQLException e) {
                    LOG.error("Can't close the ResultSet: " + e.getMessage());
                }
            }
            if(rs != null){
                try {
                    count.close();
                } catch (SQLException e) {
                    LOG.error("Can't close the ResultSet: " + e.getMessage());
                }
            }

            if (countItemsStatement != null) {
                try {
                    countItemsStatement.close();
                } catch (SQLException e) {
                    LOG.error("Can't close statement", e);
                }
            }
            if (findItemsStatement != null) {
                try {
                    findItemsStatement.close();
                } catch (SQLException e) {
                    LOG.error("Can't close statement", e);
                }
            }
            if (removeItemStatement != null) {
                try {
                    removeItemStatement.close();
                } catch (SQLException e) {
                    LOG.error("Can't close statement", e);
                }
            }
            if (removeValueStatement != null) {
                try {
                    removeValueStatement.close();
                } catch (SQLException e) {
                    LOG.error("Can't close statement", e);
                }
            }
            if(session != null){
                session.close();
            }
            if (success){
                LOG.info("Finished successfully  on {} milliseconds, Clean {} items",(System.currentTimeMillis()-started), nb);
            } else {
                LOG.error("An unexpected error occurs when migrating");
                throw new RuntimeException("Error during remove enable attribute");
            }
        }
    }

    private void removeBatch(Connection connection, String temp) throws  SQLException{
        String query2 = REMOVE_ENABLE_ATTRIBUTE_VALUE + temp + ")";
        removeValueStatement = connection.prepareStatement(query2);
        removeValueStatement.execute();

        String query1 = REMOVE_ENABLE_ATTRIBUTE + temp + ")";
        removeItemStatement = connection.prepareStatement(query1);
        removeItemStatement.execute();
    }
}
