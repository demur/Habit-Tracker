package tech.demur.habittracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import tech.demur.habittracker.database.LiveRealmData;
import tech.demur.habittracker.database.TestData;
import tech.demur.habittracker.model.Habit;
import tech.demur.habittracker.model.Period;
import tech.demur.habittracker.model.Record;

import static tech.demur.habittracker.database.RealmUtils.habitModel;
import static tech.demur.habittracker.database.RealmUtils.periodModel;
import static tech.demur.habittracker.database.RealmUtils.recordModel;

public class DbViewModel extends AndroidViewModel {
    private static final String TAG = DbViewModel.class.getSimpleName();
    private Realm mDb;
    private LiveRealmData<Habit> mLiveHabits = null;

    public DbViewModel(@NonNull Application application) {
        super(application);
        mDb = Realm.getDefaultInstance();
    }

    public LiveRealmData<Habit> getLiveHabits() {
        if (null == mLiveHabits) {
            mLiveHabits = habitModel(mDb).loadAllLive();
        }
        return mLiveHabits;
    }

    public RealmResults<Habit> getHabits() {
        return habitModel(mDb).loadAll();
    }

    public Habit getHabitById(String id) {
        return habitModel(mDb).loadById(id);
    }

    public Habit getHabitCopyById(String id) {
        return mDb.copyFromRealm(getHabitById(id));
    }

    public Period getPeriodById(String id) {
        return periodModel(mDb).loadById(id);
    }

    public Period getPeriodCopyById(String id) {
        return mDb.copyFromRealm(getPeriodById(id));
    }

    public Record getRecordById(String id) {
        return recordModel(mDb).loadById(id);
    }

    public Record getRecordCopyById(String id) {
        return mDb.copyFromRealm(getRecordById(id));
    }

    public void createOrUpdateHabit(Habit habit) {
        habitModel(mDb).createOrUpdate(habit);
    }

    public void createOrUpdatePeriod(Period period) {
        periodModel(mDb).createOrUpdate(period);
    }

    public void createOrUpdateRecord(Record record) {
        recordModel(mDb).createOrUpdate(record);
    }

    public Period loadPeriodByStart(Habit habit, Date start) {
        return periodModel(mDb).loadByStart(habit, start);
    }

    public void deleteHabitById(String id) {
        habitModel(mDb).deleteById(id);
    }

    public void recalcPeriod(String id) {
        recalcPeriod(getPeriodById(id));
    }

    public void recalcPeriod(Period period) {
        if (null != period) {
            Period copy = mDb.copyFromRealm(period);
            Habit habit = period.parentHabit;
            if (habit.goal_type.equalsIgnoreCase("Number")) {
                Integer sum = 0;
                for (Record record : period.records) {
                    sum += Integer.parseInt(record.value);
                }
                copy.total = sum.toString();
                switch (habit.goal_condition) {
                    case "=":
                        copy.achieved = sum == Integer.parseInt(habit.goal);
                        break;
                    case "≠":
                        copy.achieved = sum != Integer.parseInt(habit.goal);
                        break;
                    case "≤":
                        copy.achieved = sum <= Integer.parseInt(habit.goal);
                        break;
                    case "in range":
                        if (habit.goal.contains("..")) {
                            String[] parts = habit.goal.split("\\.\\.");
                            copy.achieved = sum >= Integer.parseInt(parts[0])
                                    && sum <= Integer.parseInt(parts[1]);
                        }
                        break;
                    case "not in range":
                        if (habit.goal.contains("..")) {
                            String[] parts = habit.goal.split("\\.\\.");
                            copy.achieved = sum < Integer.parseInt(parts[0])
                                    || sum > Integer.parseInt(parts[1]);
                        }
                        break;
                    case "≥":
                    default:
                        copy.achieved = sum >= Integer.parseInt(habit.goal);
                }
            } else if (habit.goal_type.equalsIgnoreCase("Time")) {
                String[] part = habit.goal.split("\\.\\.");
                int goal = Integer.parseInt(part[0].replaceAll("(\\d+).+", "$1"));
                if (part[0].contains("PM")) {
                    goal += 12;
                }
                for (Record record : period.records) {
                    int value = Integer.parseInt(record.value.replaceAll("(\\d+).+", "$1"));
                    if (record.value.contains("PM")) {
                        value += 12;
                    }
                    switch (habit.goal_condition) {
                        case "=":
                            copy.achieved = value == goal;
                            break;
                        case "≠":
                            copy.achieved = value != goal;
                            break;
                        case "≤":
                            copy.achieved = value <= goal;
                            break;
                        case "in range":
                            if (habit.goal.contains("..")) {
                                int bottomLimit = goal;
                                int topLimit = Integer.parseInt(part[1].replaceAll("(\\d+).+", "$1"));
                                if (part[1].contains("PM")) {
                                    topLimit += 12;
                                }
                                copy.achieved = value >= bottomLimit
                                        && value <= topLimit;
                            }
                            break;
                        case "not in range":
                            if (habit.goal.contains("..")) {
                                int bottomLimit = goal;
                                int topLimit = Integer.parseInt(part[1].replaceAll("(\\d+).+", "$1"));
                                if (part[1].contains("PM")) {
                                    topLimit += 12;
                                }
                                copy.achieved = value < bottomLimit
                                        || value > topLimit;
                            }
                            break;
                        case "≥":
                        default:
                            copy.achieved = value >= goal;
                    }
                    if (copy.achieved) {
                        break;
                    }
                }
            } else if (habit.goal_type.equalsIgnoreCase("Yes/No")) {
                boolean goal = habit.goal.equalsIgnoreCase("Yes")
                        || habit.goal.equalsIgnoreCase("True")
                        || habit.goal.equalsIgnoreCase("1")
                        || habit.goal.equalsIgnoreCase("+");
                if (!habit.goal_condition.equalsIgnoreCase("=")) {
                    goal = !goal;
                }
                for (Record record : period.records) {
                    if (goal == record.value.equalsIgnoreCase("Yes")
                            || record.value.equalsIgnoreCase("True")
                            || record.value.equalsIgnoreCase("1")
                            || record.value.equalsIgnoreCase("+")) {
                        copy.achieved = true;
                        break;
                    }
                }
            }
            createOrUpdatePeriod(copy);
        }
    }

    public void clearDb() {
        mDb.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
    }

    public void populateDb() {
        TestData.populateDb(this);
    }

    public Realm getDb() {
        return mDb;
    }

    @Override
    protected void onCleared() {
        mDb.removeAllChangeListeners();
        mDb.close();
        super.onCleared();
    }
}