package com.example.kamil.judo;

import android.content.Context;

/**
 * Created by Kamil on 2017-05-24.
 */
public class Globals {
    private static Globals instance;

    private static boolean isPlayersDownloaded;
    private static boolean isTournamentsDownloaded;
    private static boolean isTournamentChecked;

    private static String tournamentID;
    private static String playerID;

    private Globals(){
        isPlayersDownloaded = false;
        isTournamentsDownloaded = false;
        isTournamentChecked = false;
    }

    public void setPlayersState(boolean t) {
        Globals.isPlayersDownloaded = t;
    }

    public boolean getPlayersState() {
        return Globals.isPlayersDownloaded;
    }




    public void setIsTournamentChecked(boolean t) {
        Globals.isTournamentChecked = t;
    }

    public boolean getIsTournamentChecked() {
        return Globals.isTournamentChecked;
    }


    public void setTournamentsState(boolean t) {
        Globals.isTournamentsDownloaded = t;
    }

    public boolean getTournamentsState() {
        return Globals.isTournamentsDownloaded;
    }



    public void setTournamentID(String id) {
        this.tournamentID = id;
    }

    public String getTournamentID() {
        return this.tournamentID;
    }


    public void setPlayerID(String id) {
        this.playerID = id;
    }

    public String getPlayerID() {
        return this.playerID;
    }

    public static synchronized Globals getInstance() {
        if (instance == null) {
            instance = new Globals();
        }
        return instance;
    }
}
