package org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;
import org.hibernate.annotations.Entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@ExoEntity
@Table(name = "login_history")
public class LoginHistoryEntity {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long ID;

    @OneToMany(fetch =FetchType.LAZY)
    @JoinColumn(name = "SESSION_ID")
    private long sessionID;

    @Column(name = "ACTION")
    private String action;

    @Column(name = "DATE")
    private Timestamp dateTime;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }
}
