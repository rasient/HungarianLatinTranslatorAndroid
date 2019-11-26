package se.translator.roomdb;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

@Database(entities = {Translation.class}, version = 4, exportSchema = false)
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
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('vesekelyhek', 'calices renales', '')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('öreglyuk', 'foramen magnum', '')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('homloküreg', 'sinus frontalis', '')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('csigolyatest', 'corpus vertebrae', '')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('csigolyaív', 'corpus vertebrae', '')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('csigolyanyúlvány', 'processus vertebralis', '')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('csigolyalyuk', 'foramen vertebrale', '')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('hallócsontocska', 'ossiculum auditus', '')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('érzékszerv', 'organa sensuum', '')");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('őrlő', 'molaris', '3')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('mély', 'profundus', '3')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('mélyen lévő', 'profundus', '3')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('ájulás', 'collapsus', '-us,m')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('égető', 'imminens', '-ntis')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('ülőgumó', 'tuber ischiadicum', '')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('pajzsmirigy artéria', 'arteria thyroidea', '')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('mellkasnak harántnyúlványok közti izmai', 'musculi intertransversarii thoracis', '')");
            database.execSQL("update Translation set wordLa = 'letalis' where wordLa = 'lethalis'");
        }
    };
    private static final Migration MIGRATION_4_5 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('testi betegésgeket beképzelő', 'hypohonder', 'i,m')");
            database.execSQL("insert into Translation (wordHu, wordLa, suffixLa) values ('lelki betegésgeket beképzelő', 'psychohonder', 'i,m')");
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
                }).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5).build();
    }

    public abstract TranslationDao translationDao();
}