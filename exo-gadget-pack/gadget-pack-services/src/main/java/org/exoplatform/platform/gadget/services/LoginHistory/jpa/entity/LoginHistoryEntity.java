package org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;
import org.hibernate.annotations.Entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@ExoEntity
@Table(name = "login_history")
@NamedQueries({
        @NamedQuery(name = "loginHistory.getUserLastLoginID",query = "SELECT MAX(l.ID) FROM LoginHistoryEntity l WHERE l.userID = :userId"),
        @NamedQuery(name = "loginHistory.getBeforeLastLoginID",query = "SELECT MAX(l.ID) FROM LoginHistoryEntity l WHERE l.userID = :userId AND l.ID < :id"),
        @NamedQuery(name = "loginHistory.getUserLoginHistory",query = "SELECT * FROM LoginHistoryEntity l WHERE l.userID = :userId AND l.loginDate BETWEEN :from AND :to"),
        @NamedQuery(name = "loginHistory.getLastLogins",query = "SELECT * FROM LoginHistoryEntity l WHERE l.userID = :userId AND l.loginDate < :today LIMIT :limit"),
        // @NamedQuery(name = "loginHistory.getLoginPerDayInRange",query = "SELECT * FROM LoginHistoryEntity l WHERE l.userID = :userId AND l.loginDate BETWEEN :from AND :to ORDER BY l.loginDate ASC"),
        @NamedQuery(name = "loginHistory.getLoginCountPerDay",query = "SELECT COUNT (*) FROM LoginHistoryEntity l WHERE l.userID = :userId AND l.loginDate BETWEEN :from AND :to"),
        @NamedQuery(name = "loginHistory.getLastUsersLogin",query = "SELECT l.userID FROM LoginHistoryEntity l WHERE l.loginDate >= :from ORDER BY l.ID DESC")
})
public class LoginHistoryEntity {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long ID;

    @Column(name = "USER_ID")
    private String userID;

    @Column(name = "DATE")
    private Timestamp loginDate;

    public LoginHistoryEntity() {
    }

    public LoginHistoryEntity(String userID) {
        this.userID = userID;
    }

    public LoginHistoryEntity(String userID, Timestamp loginDate) {
        this.userID = userID;
        this.loginDate = loginDate;
    }

    public long getID() {
        return ID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Timestamp getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Timestamp loginDate) {
        this.loginDate = loginDate;
    }
}
