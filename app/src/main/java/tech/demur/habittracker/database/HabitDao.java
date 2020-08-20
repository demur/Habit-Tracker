package tech.demur.habittracker.database;

import io.realm.Realm;
import io.realm.RealmResults;
import tech.demur.habittracker.model.Habit;
import tech.demur.habittracker.model.Period;

import static tech.demur.habittracker.database.RealmUtils.asLiveData;

public class HabitDao {
    private Realm mRealm;

    public HabitDao(Realm realm) {
        this.mRealm = realm;
    }

    public void createOrUpdate(Habit habit) {
        if (null != habit) {
            mRealm.executeTransaction(r -> {
                mRealm.insertOrUpdate(habit);
            });
        }
    }

    public LiveRealmData<Habit> loadAllLive() {
        return asLiveData(loadAll());
    }

    public RealmResults<Habit> loadAll() {
        return mRealm.where(Habit.class).sort("created").findAll();
    }

    public void deleteById(String id) {
        Habit habit = loadById(id);
        delete(habit);
    }

    public void delete(Habit habit) {
        if (null != habit) {
            mRealm.executeTransaction(r -> {
                for (Period period : habit.periods) {
                    period.records.deleteAllFromRealm();
                }
                habit.periods.deleteAllFromRealm();
                habit.deleteFromRealm();
            });
        }
    }

    public Habit loadById(String id) {
        return mRealm.where(Habit.class).equalTo("_id", id).findFirst();
    }

    public RealmResults<Habit> findByName(String name) {
        return mRealm.where(Habit.class)
                .equalTo("name", name)
                .findAll();
    }
}