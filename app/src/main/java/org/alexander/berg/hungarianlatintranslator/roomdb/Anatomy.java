package org.alexander.berg.hungarianlatintranslator.roomdb;

public class Anatomy {

    static Translation[] populateAnatomy1() {
        return new Translation[]{
                new Translation("csonttan", "osteologia", ""),
                new Translation("ízülettan", "syndesmologia", ""),
                new Translation("izomtan", "myologia", ""),
                new Translation("értan", "angiologia", ""),
                new Translation("zsigertan", "spanchnologia", ""),
                new Translation("idegtan", "neurologia", ""),
                new Translation("sejttan", "cytologia", ""),
                new Translation("szövettan", "hystologia", ""),

                new Translation("bilateralis", "kétoldali", ""),
                new Translation("metameria", "szelvényezettség", ""),
        };
    }
}
