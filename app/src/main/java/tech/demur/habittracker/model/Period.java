package tech.demur.habittracker.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Index;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Period extends RealmObject {
    @PrimaryKey
    public String _id = UUID.randomUUID().toString();
    public boolean achieved;
    @Required
    public String total = "";
    public Date created = new Date();
    public Date lastEdited = null;
    @Index
    @Required
    public Date periodStart;
    public Habit parentHabit;
    @LinkingObjects("parentPeriod")
    public final RealmResults<Record> records = null;

    public Period() {
    }

    public Period(Date periodStart, Habit parentHabit) {
        this.periodStart = periodStart;
        this.parentHabit = parentHabit;
    }

    public Period(boolean achieved, String total, Date periodStart, Habit parentHabit) {
        this.achieved = achieved;
        this.total = total;
        this.periodStart = periodStart;
        this.parentHabit = parentHabit;
    }
}