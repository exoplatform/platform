package org.exoplatform.platform.gadget.services.LoginHistory.jpa.entity;

import org.exoplatform.commons.api.persistence.ExoEntity;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@ExoEntity
@Table(name = "session_history")
public class SessionHistoryEntity {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private long ID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private long userID;

    @Column(name = "DATE")
    private Calendar Date;

    @Column(name = "fromDate")
    private Date fromDate;

    @Column(name = "toDate")
    private Date toDate;

    @Column(name = "Duration")
    private long Duration;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public Calendar getDate() {
        return Date;
    }

    public void setDate(Calendar date) {
        Date = date;
    }

    public java.util.Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(java.util.Date fromDate) {
        this.fromDate = fromDate;
    }

    public java.util.Date getToDate() {
        return toDate;
    }

    public void setToDate(java.util.Date toDate) {
        this.toDate = toDate;
    }

    public long getDuration() {
        return Duration;
    }

    public void setDuration(long duration) {
        Duration = duration;
    }
}
