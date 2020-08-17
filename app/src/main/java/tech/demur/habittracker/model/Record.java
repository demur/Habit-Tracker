package tech.demur.habittracker.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Record extends RealmObject {
    @PrimaryKey
    public String _id = UUID.randomUUID().toString();
    @Required
    public String value;
    public String notes;
    public Date created = new Date();
    public Date lastEdited = null;
    public Period parentPeriod;

    public Record() {
    }

    public Record(String value, Period parentPeriod) {
        this.value = value;
        this.parentPeriod = parentPeriod;
    }
}