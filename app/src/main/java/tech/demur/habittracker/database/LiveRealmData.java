package tech.demur.habittracker.database;

import androidx.lifecycle.LiveData;

import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Class connecting the Realm lifecycle to that of LiveData objects.
 */
public class LiveRealmData<T extends RealmModel> extends LiveData<List<T>> {

    private RealmResults<T> results;
    private final RealmChangeListener<RealmResults<T>> listener = new RealmChangeListener<RealmResults<T>>() {
        @Override
        public void onChange(RealmResults<T> results) {
            setValue(results);
        }
    };

    public LiveRealmData(RealmResults<T> realmResults) {
        results = realmResults;
    }

    @Override
    protected void onActive() {
        super.onActive();
        results.addChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        results.removeChangeListener(listener);
    }
}