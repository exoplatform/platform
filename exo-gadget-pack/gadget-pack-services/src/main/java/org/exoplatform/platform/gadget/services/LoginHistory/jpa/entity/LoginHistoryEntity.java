package org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;
import org.hibernate.annotations.Entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@ExoEntity
@Table(name = "login_history")
@NamedQueries({
        @NamedQuery(name = "loginHistory.getLastLoginHistory",query = "SELECT * FROM LoginHistoryEntity l WHERE l.userID = :userId ORDER BY ID DESC LIMIT 1"),
        @NamedQuery(name = "loginHistory.getUserLastLoginID",query = "SELECT MAX(l.ID) FROM LoginHistoryEntity l WHERE l.userID = :userId"),
        @NamedQuery(name = "loginHistory.getBeforeLastLoginID",query = "SELECT MAX(l.ID) FROM LoginHistoryEntity l WHERE l.userID = :userId AND l.ID < :id"),
        @NamedQuery(name = "loginHistory.getUserLoginHistory",query = "SELECT * FROM LoginHistoryEntity l WHERE l.userID = :userId AND l.loginDate BETWEEN :from AND :to"),
        @NamedQuery(name = "loginHistory.getLastLogins",query = "SELECT * FROM LoginHistoryEntity l WHERE l.userID = :userId AND l.loginDate < :today LIMIT :limit"),
        @NamedQuery(name = "loginHistory.addLoginHistory",query = "INSERT INTO LoginHistoryEntity l (l.userID) VALUES (:userId)"),
        @NamedQuery(name = "loginHistory.getLoginCountPerDay",query = "SELECT COUNT (*) FROM LoginHistoryEntity l WHERE l.userID = :userId AND l.loginDate BETWEEN :from AND :to"),
        @NamedQuery(name = "loginHistory.getLastUsersLogin",query = "SELECT l.userID FROM LoginHistoryEntity l WHERE l.loginDate >= :from ORDER BY l.ID DESC"),
        @NamedQuery(name = "loginHistory.getActiveUsersId",query = "SELECT DISTINCT l.userId FROM LoginHistoryEntity l where l.loginDate < :from")
})
public class LoginHistoryEntity {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long id;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "LOGIN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp loginDate;

    public LoginHistoryEntity() {
    }

    public LoginHistoryEntity(String userID) {
        this.userId = userID;
    }

    public LoginHistoryEntity(String userID, Timestamp loginDate) {
        this.userId = userID;
        this.loginDate = loginDate;
    }

    public long getID() {
        return id;
    }

    public String getUserID() {
        return userId;
    }

    public void setUserID(String userID) {
        this.userId = userID;
    }

    public Timestamp getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Timestamp loginDate) {
        this.loginDate = loginDate;
    }
}
