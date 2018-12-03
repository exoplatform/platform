package org.exoplatform.platform.gadget.services.LoginHistory.jpa;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.platform.gadget.services.LoginHistory.jpa.dao.LoginHistoryDAO;
import org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity.LoginHistoryEntity;

import java.util.List;

public class LoginHistoryDAOImpl extends GenericDAOJPAImpl implements LoginHistoryDAO {
    @Override
    public Long countAll() {
        return (Long) getEntityManager().createNamedQuery("loginHistory.count").getSingleResult();
    }

    @Override
    public LoginHistoryEntity getLastLogin(String userID) {
        long ID = (long) getEntityManager().createNamedQuery("loginHistory.getUserLastLoginID").setParameter("id",userID).getSingleResult();
        return (LoginHistoryEntity) getEntityManager().createNamedQuery("loginHistory.getLoginByID").setParameter("id",ID).getSingleResult();
    }

    @Override
    public LoginHistoryEntity getBeforeLastLogin(String userID) {
        Long lastLoginID = (Long) getEntityManager().createNamedQuery("getLastUserLoginID").setParameter("userId",userID).getSingleResult();
        Long beforeLastLoginID = (Long) getEntityManager().createNamedQuery("getBeforeLastLoginID").setParameter("userId",userID).setParameter("id",lastLoginID).getSingleResult();
        return (LoginHistoryEntity) getEntityManager().createNamedQuery("getLoginByID").setParameter("id",beforeLastLoginID).getResultList();
    }

    @Override
    public List<LoginHistoryEntity> findAll() {
        return getEntityManager().createNamedQuery("loginHistory.findAll").getResultList();
    }

    @Override
    public List<LoginHistoryEntity> getLoginHistory(String userId, long fromTime, long toTime) {
        return getEntityManager().createNamedQuery("loginHistory.getUserLoginHistory")
                                                                                    .setParameter("userId",userId)
                                                                                    .setParameter("from",fromTime)
                                                                                    .setParameter("to",toTime).getResultList();
    }

}
