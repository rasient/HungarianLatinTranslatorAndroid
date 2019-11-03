package org.alexander.berg.hungarianlatintranslator.roomdb;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

@Database(entities = {Translation.class}, version = 2, exportSchema = false)
public abstract class TranslationDatabase extends RoomDatabase {
    private static TranslationDatabase dbInstance;
    private static final Object LOCK = new Object();
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('egyenes', 'rectus', '3')");
            database.execSQL("update Translation set suffixLa = 'bicipitis' where wordHu = 'bicepsz'");
            database.execSQL("update Translation set suffixLa = 'bicipitis' where wordHu = 'kétfejű'");
            database.execSQL("update Translation set suffixLa = 'tricipitis' where wordHu = 'háromfejű'");
            database.execSQL("update Translation set suffixLa = 'quadricipitis' where wordHu = 'kvadricepsz'");
        }
    };

    public static TranslationDatabase getInstance(Context context) {
        if (dbInstance == null) {
            synchronized (LOCK) {
                dbInstance = buildDatabase(context.getApplicationContext());
            }
        }
        return dbInstance;
    }

    private static TranslationDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, TranslationDatabase.class,"translation")
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            TranslationDao translationDao = getInstance(context).translationDao();
                            translationDao.insertAll(Dictionary.populateTranslationsA());
                            translationDao.insertAll(Dictionary.populateTranslationsB());
                            translationDao.insertAll(Dictionary.populateTranslationsC());
                            translationDao.insertAll(Dictionary.populateTranslationsD());
                            translationDao.insertAll(Dictionary.populateTranslationsE());
                            translationDao.insertAll(Dictionary.populateTranslationsF());
                            translationDao.insertAll(Dictionary.populateTranslationsG());
                            translationDao.insertAll(Dictionary.populateTranslationsH());
                            translationDao.insertAll(Dictionary.populateTranslationsI());
                            translationDao.insertAll(Dictionary.populateTranslationsJ());
                            translationDao.insertAll(Dictionary.populateTranslationsK());
                            translationDao.insertAll(Dictionary.populateTranslationsL());
                            translationDao.insertAll(Dictionary.populateTranslationsM());
                            translationDao.insertAll(Dictionary.populateTranslationsN());
                            translationDao.insertAll(Dictionary.populateTranslationsO());
                            translationDao.insertAll(Dictionary.populateTranslationsP());
                            translationDao.insertAll(Dictionary.populateTranslationsR());
                            translationDao.insertAll(Dictionary.populateTranslationsS());
                            translationDao.insertAll(Dictionary.populateTranslationsT());
                            translationDao.insertAll(Dictionary.populateTranslationsU());
                            translationDao.insertAll(Dictionary.populateTranslationsV());
                            translationDao.insertAll(Dictionary.populateTranslationsY());
                            translationDao.insertAll(Dictionary.populateTranslationsZ());
                        });
                    }
                }).addMigrations(MIGRATION_1_2).build();
    }

    public abstract TranslationDao translationDao();
}