package com.example.kamil.judo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Kamil on 2017-05-31.
 */
public class databaseHelper extends SQLiteOpenHelper {

    private static databaseHelper instance;

    public static final String DATABASE_NAME = "macsup.db";
    public static final String TABLE_NAME = "contestants";
    public static final String COL_1_1 = "ContestantID";
    public static final String COL_1_2 = "WeightClass";
    public static final String COL_1_3 = "AgeClass";
    public static final String COL_1_4 = "TournamentID";
    public static final String COL_1_5 = "FirstName";
    public static final String COL_1_6 = "LastName";
    public static final String COL_1_7 = "DateOfBirth";
    public static final String COL_1_8 = "Height";
    public static final String COL_1_9 = "Weight";
    public static final String COL_1_10 = "Gender";
    public static final String COL_1_11 = "MatchesCount";
    public static final String COL_1_12 = "MatchesWon";
    public static final String COL_1_13 = "MatchesDraw";


    public static final String TABLE_NAME_2 = "matches";
    public static final String COL_2_1 = "MatchID";
    public static final String COL_2_2 = "TournamentID";
    public static final String COL_2_3 = "Contestant1ID";
    public static final String COL_2_4 = "Contestant2ID";
    public static final String COL_2_5 = "Contestant1Result";
    public static final String COL_2_6 = "Contestant2Result";
    public static final String COL_2_7 = "StartDate";
    public static final String COL_2_8 = "EndDate";
    public static final String COL_2_9 = "Duration";

    public static final String TABLE_NAME_3 = "tournaments";
    public static final String COL_3_1 = "TournamentID";
    public static final String COL_3_2 = "Name";
    public static final String COL_3_3 = "Location";
    public static final String COL_3_4 = "StartDate";
    public static final String COL_3_5 = "EndDate";
    public static final String COL_3_6 = "Info";

    Globals g;

    public static synchronized databaseHelper getInstance(Context context) {
        if (instance == null)
            instance = new databaseHelper(context.getApplicationContext());

        return instance;
    }

    private databaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        SQLiteDatabase db = this.getWritableDatabase();
        g = Globals.getInstance();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ContestantID INTEGER PRIMARY KEY, " +
                "WeightClass TEXT, AgeClass TEXT, TournamentID INTEGER, FirstName TEXT, LastName TEXT," +
                "DateOfBirth TEXT, Height TEXT, Weight TEXT, Gender TEXT, MatchesCount TEXT, MatchesWon TEXT, MatchesDraw TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_NAME_2 + "(MatchID INTEGER PRIMARY KEY, TournamentID INTEGER, Contestant1ID INTEGER, " +
                "Contestant2ID INTEGER, Contestant1Result INTEGER, Contestant2Result INTEGER, StartDate TEXT, EndDate TEXT, Duration TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_NAME_3 + " (TournamentID INTEGER PRIMARY KEY, Name TEXT, Location TEXT, " +
                " StartDate TEXT, EndDate TEXT, Info TEXT) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void getMatchesResults(String playerID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor cursor;


        cursor = db.rawQuery("SELECT COUNT(matches.MatchID) AS 'Counter' FROM matches, contestants WHERE (contestants.ContestantID = matches.Contestant1ID OR contestants.ContestantID = matches.Contestant2ID) AND contestants.ContestantID = " + playerID + " UNION ALL SELECT COUNT(matches.MatchID) FROM matches, contestants WHERE (contestants.ContestantID = matches.Contestant1ID OR contestants.ContestantID = matches.Contestant2ID) AND contestants.ContestantID = " + playerID + " AND matches.Contestant1Result = matches.Contestant2Result UNION ALL SELECT COUNT(matches.MatchID) FROM matches, contestants WHERE contestants.ContestantID = matches.Contestant1ID AND contestants.ContestantID = 5 AND matches.Contestant1Result > matches.Contestant2Result UNION ALL SELECT COUNT(matches.MatchID) FROM matches, contestants WHERE contestants.ContestantID = matches.Contestant2ID AND contestants.ContestantID = " + playerID + " AND matches.Contestant1Result < matches.Contestant2Result", null);
        //cursor = db.rawQuery("SELECT COUNT(matches.MatchID) AS 'Counter' FROM matches, contestants WHERE (contestants.ContestantID = matches.Contestant1ID OR contestants.ContestantID = matches.Contestant2ID) AND contestants.ContestantID = 5", null);

        cursor.moveToFirst();
        String matchesAll = cursor.getString(0);
        cursor.moveToNext();
        String matchesDraw = cursor.getString(0);
        cursor.moveToNext();
        int matchesWon1 = Integer.parseInt(cursor.getString(0));
        cursor.moveToNext();
        int matchesWon2 = Integer.parseInt(cursor.getString(0));

        int matchesWon = matchesWon1 + matchesWon2;

        contentValues.put(COL_1_11, matchesAll);
        contentValues.put(COL_1_12, matchesWon);
        contentValues.put(COL_1_13, matchesDraw);
        db.update(TABLE_NAME, contentValues, "ContestantID = ?", new String[] {playerID});
    }

    public boolean insertContestants(String ContestantID, String WeightClass, String AgeClass, String TournamentID,
                                     String FirstName, String LastName, String DateOfBirth, String Height, String Weight, String Gender) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1_1,ContestantID);
        contentValues.put(COL_1_2,WeightClass);
        contentValues.put(COL_1_3,AgeClass);
        contentValues.put(COL_1_4,TournamentID);
        contentValues.put(COL_1_5,FirstName);
        contentValues.put(COL_1_6,LastName);
        contentValues.put(COL_1_7,DateOfBirth);
        contentValues.put(COL_1_8,Height);
        contentValues.put(COL_1_9,Weight);
        contentValues.put(COL_1_10,Gender);
        contentValues.put(COL_1_11,"0");
        contentValues.put(COL_1_12,"0");
        contentValues.put(COL_1_13,"0");
        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        else
            return true;
    }

    public Cursor getAllPlayers() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;

        if (g.getTournamentID() == null)
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        else
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE TournamentID = " + g.getTournamentID(), null);

        return res;
    }

    public boolean insertMatches(String MatchID, String TournamentID, String Contestant1ID, String Contestant2ID, String Contestant1Result, String Contestant2Result,
                                 String StartDate, String EndDate, String Duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2_1,MatchID);
        contentValues.put(COL_2_2,TournamentID);
        contentValues.put(COL_2_3,Contestant1ID);
        contentValues.put(COL_2_4,Contestant2ID);
        contentValues.put(COL_2_5,Contestant1Result);
        contentValues.put(COL_2_6,Contestant2Result);
        contentValues.put(COL_2_7,StartDate);
        contentValues.put(COL_2_8,EndDate);
        contentValues.put(COL_2_9,Duration);
        long result = db.insert(TABLE_NAME_2, null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean insertTournaments(String TournamentID, String Name, String Location, String StartDate, String EndDate, String Info){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_3_1,TournamentID);
        contentValues.put(COL_3_2,Name);
        contentValues.put(COL_3_3,Location);
        contentValues.put(COL_3_4,StartDate);
        contentValues.put(COL_3_5,EndDate);
        contentValues.put(COL_3_6,Info);
        long result = db.insert(TABLE_NAME_3, null, contentValues);

        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllTournaments() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        res = db.rawQuery("SELECT * FROM " + TABLE_NAME_3, null);
        return res;
    }

    public Cursor getAllMatches(String playerID){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        res = db.rawQuery("SELECT * FROM " + TABLE_NAME_2 + " WHERE Contestant1ID = " + playerID + " OR Contestant2ID = " + playerID, null);
        return res;
    }

    public Cursor getContestantInfo(String playerID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        res = db.rawQuery("SELECT FirstName, LastName, WeightClass, AgeClass FROM contestants WHERE ContestantID = " + playerID, null);
        res.moveToFirst();
        return res;
    }

    public Cursor getMatchesInfo(String matchID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        res = db.rawQuery("SELECT Contestant1Result, Contestant2Result,StartDate, EndDate, Duration FROM matches WHERE MatchID = " + matchID, null);
        res.moveToFirst();
        return res;
    }

    public Cursor getUpcomingMatch(String playerID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        res = db.rawQuery("SELECT * FROM Matches " +
                "WHERE (Contestant1ID = " + playerID + " OR Contestant2ID = " + playerID + ") " +
                "AND ((Contestant1Result = 0 AND Contestant2Result = 0) OR (Contestant1Result IS NULL AND Contestant2Result IS NULL)) " +
                "ORDER BY StartDate DESC LIMIT 1", null);

        res.moveToFirst();
        return res;
    }

}
