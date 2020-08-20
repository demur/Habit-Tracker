package tech.demur.habittracker.database;

import java.util.Date;

import io.realm.Realm;
import tech.demur.habittracker.model.Habit;
import tech.demur.habittracker.model.Period;

import static tech.demur.habittracker.database.RealmUtils.asLiveData;

public class PeriodDao {
    private Realm mRealm;

    public PeriodDao(Realm realm) {
        this.mRealm = realm;
    }

    public void createOrUpdate(Period period) {
        if (null != period) {
            mRealm.executeTransaction(r -> {
                mRealm.insertOrUpdate(period);
            });
        }
    }

    public void deleteById(String id) {
        delete(loadById(id));
    }

    public void delete(Period period) {
        if (null != period) {
            mRealm.executeTransaction(r -> {
                period.records.deleteAllFromRealm();
                period.deleteFromRealm();
            });
        }
    }

    public Period loadById(String id) {
        return mRealm.where(Period.class).equalTo("_id", id).findFirst();
    }

    public Period loadByStart(Habit habit, Date start) {
        return loadByStart(habit._id, start);
    }

    public Period loadByStart(String habitId, Date start) {
        return mRealm.where(Period.class).equalTo("parentHabit._id", habitId)
                .and().equalTo("periodStart", start).findFirst();
    }

    public LiveRealmData<Period> findByHabitLive(Habit habit) {
        return asLiveData(habit.periods);
    }
}