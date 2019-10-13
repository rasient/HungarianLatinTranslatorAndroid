package org.alexander.berg.hungarianlatintranslator.roomdb;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

@Database(entities = {Translation.class}, version = 1, exportSchema = false)
public abstract class TranslationDatabase extends RoomDatabase {
    private static TranslationDatabase dbInstance;
    private static final Object LOCK = new Object();

    public static TranslationDatabase getInstance(Context context) {
        if (dbInstance == null) {
            synchronized (LOCK) {
                dbInstance = buildDatabase(context.getApplicationContext());
            }
        }
        return dbInstance;
    }

    private static TranslationDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, TranslationDatabase.class,"translation1db")
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            TranslationDao translationDao = getInstance(context).translationDao();
                            translationDao.insertAll(Translation.populateTranslationsA());
                            translationDao.insertAll(Translation.populateTranslationsB());
                            translationDao.insertAll(Translation.populateTranslationsC());
                            translationDao.insertAll(Translation.populateTranslationsD());
                            translationDao.insertAll(Translation.populateTranslationsE());
                        });
                    }
                }).build();
    }

    public abstract TranslationDao translationDao();
}