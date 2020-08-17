package tech.demur.habittracker.model;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Habit extends RealmObject {
    @PrimaryKey
    public String _id = UUID.randomUUID().toString();
    @Required
    public String name;
    @Required
    public String interval;
    @Required
    public String goal;
    @Required
    public String goal_type;
    @Required
    public String goal_label;
    @Required
    public String goal_condition;
    public Date created = new Date();
    public Date lastEdited = null;
    @LinkingObjects("parentHabit")
    public final RealmResults<Period> periods = null;

    public Habit() {
    }

    public Habit(String name, String interval, String goal, String goal_type, String goal_label, String goal_condition) {
        this.name = name;
        this.interval = interval;
        this.goal = goal;
        this.goal_type = goal_type;
        this.goal_label = goal_label;
        this.goal_condition = goal_condition;
    }
}