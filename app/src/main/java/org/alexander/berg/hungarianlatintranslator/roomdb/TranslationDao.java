package org.alexander.berg.hungarianlatintranslator.roomdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TranslationDao {

    @Insert
    void insertAll(Translation... translationEntities);

    @Query("SELECT * FROM Translation WHERE wordHu LIKE :wordHu")
    List<Translation> findByWordHuLa(String wordHu);

    @Query("SELECT * FROM Translation WHERE wordLa LIKE :wordLa")
    List<Translation> findByWordLaHu(String wordLa);

    @Query("SELECT DISTINCT wordHu FROM Translation")
    String[] getAllWordHu();

    @Query("SELECT DISTINCT wordLa FROM Translation")
    String[] getAllWordLa();

    @Query("SELECT suffixLa FROM Translation WHERE wordLa LIKE :wordLa LIMIT 1")
    String findSuffixByLa(String wordLa);
}
