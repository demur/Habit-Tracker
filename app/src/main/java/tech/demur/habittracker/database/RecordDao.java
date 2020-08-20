package tech.demur.habittracker.database;

import io.realm.Realm;
import tech.demur.habittracker.model.Period;
import tech.demur.habittracker.model.Record;

import static tech.demur.habittracker.database.RealmUtils.asLiveData;

public class RecordDao {
    private Realm mRealm;

    public RecordDao(Realm realm) {
        this.mRealm = realm;
    }

    public void createOrUpdate(Record record) {
        if (null != record) {
            mRealm.executeTransaction(r -> {
                mRealm.insertOrUpdate(record);
            });
        }
    }

    public Record loadById(String id) {
        return mRealm.where(Record.class).equalTo("_id", id).findFirst();
    }

    public LiveRealmData<Record> findPeriodRecords(Period period) {
        return asLiveData(period.records);
    }
}