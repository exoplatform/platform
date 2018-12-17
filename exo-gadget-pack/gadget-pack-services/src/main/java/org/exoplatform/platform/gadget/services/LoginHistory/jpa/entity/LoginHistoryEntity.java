package org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@ExoEntity
@Table(name = "login_history")
@NamedQueries({
        @NamedQuery(name = "loginHistory.getLastLoginHistory",query = "SELECT l FROM LoginHistoryEntity l WHERE l.userId = :userId ORDER BY l.id DESC"),
        @NamedQuery(name = "loginHistory.getUserLastLoginID",query = "SELECT MAX(l.id) FROM LoginHistoryEntity l WHERE l.userId = :userId"),
        @NamedQuery(name = "loginHistory.getBeforeLastLoginID",query = "SELECT MAX(l.id) FROM LoginHistoryEntity l WHERE l.userId = :userId AND l.id < :id"),
        @NamedQuery(name = "loginHistory.getUserLoginHistory",query = "SELECT l FROM LoginHistoryEntity l WHERE l.userId = :userId AND l.loginDate BETWEEN :from AND :to"),
        @NamedQuery(name = "loginHistory.getLastLogins",query = "SELECT l FROM LoginHistoryEntity l ORDER BY l.id DESC"),
        @NamedQuery(name = "loginHistory.getLastLoginsOfUser",query = "SELECT l FROM LoginHistoryEntity l WHERE l.userId = :userId"),
        @NamedQuery(name = "loginHistory.getLoginCountPerDay",query = "SELECT COUNT (l) FROM LoginHistoryEntity l WHERE l.userId = :userId AND l.loginDate BETWEEN :from AND :to"),
        @NamedQuery(name = "loginHistory.getLastUsersLogin",query = "SELECT l.userId FROM LoginHistoryEntity l WHERE l.loginDate >= :from ORDER BY l.id DESC"),
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
