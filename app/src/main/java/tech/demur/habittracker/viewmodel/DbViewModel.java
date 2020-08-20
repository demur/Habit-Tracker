package tech.demur.habittracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import tech.demur.habittracker.database.LiveRealmData;
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

    public void clearDb() {
        mDb.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
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