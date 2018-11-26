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

    @Column(name = "USER_ID")
    private String userID;

    @Column(name = "DATE")
    private Timestamp dateTime;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }
}
