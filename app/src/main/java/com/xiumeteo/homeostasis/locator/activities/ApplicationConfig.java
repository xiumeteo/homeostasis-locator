package com.xiumeteo.homeostasis.locator.activities;

import android.app.Application;

import com.xiumeteo.homeostasis.model.migration.MigrationManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by xiumeteo on 8/12/15.
 */
public class ApplicationConfig extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .schemaVersion(1)
                .migration(new MigrationManager())
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
