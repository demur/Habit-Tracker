package tech.demur.habittracker;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class HabitTrackerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder().build());
    }
}