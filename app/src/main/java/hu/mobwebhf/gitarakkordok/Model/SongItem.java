package hu.mobwebhf.gitarakkordok.Model;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin on 2017. 10. 23..
 */

public class SongItem extends SugarRecord {
    public String name;
    public String datum;
    public ArrayList<SongUnit> songUnits;
    /*public String chords;
    public String lyrics;*/
    public boolean isFavorite;
}
