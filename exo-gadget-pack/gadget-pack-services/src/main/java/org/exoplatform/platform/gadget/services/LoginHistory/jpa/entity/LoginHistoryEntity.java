package org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;
import org.hibernate.annotations.Entity;

import javax.persistence.*;

@Entity
@ExoEntity
@Table(name = "login_history")
@NamedQueries({
        @NamedQuery(name = "loginHistory.getUserLastLoginID",query = "SELECT MAX(l.ID) FROM LoginHistoryEntity l WHERE l.userID = :userId"),
        @NamedQuery(name = "loginHistory.getBeforeLastLoginID",query = "SELECT MAX(l.ID) FROM LoginHistoryEntity l WHERE l.userID = :userId AND l.ID < :id"),
        @NamedQuery(name = "loginHistory.getLoginByID",query = "SELECT * FROM LoginHistoryEntity l WHERE l.ID = :id"),
        @NamedQuery(name = "loginHistory.getUserLoginHistory",query = "SELECT * FROM LoginHistoryEntity l WHERE l.userID = :userId AND l.loginDate BETWEEN :from AND :to"),
        @NamedQuery(name = "loginHistory.addLoginHistory",query = "INSERT INTO LoginHistoryEntity l (l.userID,l.loginDate) VALUES (':userId',':date')"),
        @NamedQuery(name = "loginHistory.getLastLogins",query = "SELECT * FROM LoginHistoryEntity l WHERE l.userID = :userId AND l.loginDate < :today LIMIT :limit")
})
public class LoginHistoryEntity {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long ID;

    @Column(name = "USER_ID")
    private String userID;

    @Column(name = "DATE")
    private Long loginDate;

    public LoginHistoryEntity() {
    }

    public LoginHistoryEntity(String userID, Long loginDate) {
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

    public Long getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Long loginDate) {
        this.loginDate = loginDate;
    }
}
