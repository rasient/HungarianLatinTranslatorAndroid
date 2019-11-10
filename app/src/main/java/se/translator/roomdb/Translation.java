package se.translator.roomdb;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Translation {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String wordHu;
    private String wordLa;
    private String suffixLa;

    @Ignore
    Translation(String wordHu, String wordLa, String suffixLa) {
        this.wordHu = wordHu;
        this.wordLa = wordLa;
        this.suffixLa = suffixLa;
    }

}