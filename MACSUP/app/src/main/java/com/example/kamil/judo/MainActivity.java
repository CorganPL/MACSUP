package com.example.kamil.judo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    String playersUrl = "http://192.168.0.3/macsupscripts/showPlayers.php";
    String tournamentsUrl = "http://192.168.0.3/macsupscripts/showTournament.php";
    String allBattlesUrl = "http://192.168.0.3/macsupscripts/showAllBattles.php";

    Context context;
    RelativeLayout layout;
    databaseHelper myDb;
    Globals g;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout =(RelativeLayout)findViewById(R.id.layout);
        layout.setBackgroundResource(R.drawable.background);

        context = getApplicationContext();
        myDb = databaseHelper.getInstance(context);
        g = Globals.getInstance();
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        downloadPlayers();
        downloadTournaments();
        downloadMatches();
    }

    public void switchLayoutPlayersList(View v) {
        Intent intent = new Intent(context,playersListActivity.class);
        startActivity(intent);
    }

    public void switchLayoutBattle(View v) {
        Intent intent = new Intent(context,battleActivity.class);
        startActivity(intent);

    }

    public void switchLayoutTournament(View v) {
        Intent intent = new Intent(context,tournamentsActivity.class);
        startActivity(intent);
    }

    public void switchLayoutRules(View v) {
        Intent intent = new Intent(context,rulesActivity.class);
        startActivity(intent);
    }

    public void downloadPlayers() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, playersUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray playersJA = response.getJSONArray("players");

                            for (int i = 0; i < playersJA.length(); i++) {
                                JSONObject player = playersJA.getJSONObject(i);

                                String contestant_id = player.getString("ContestantID");
                                String weightClass = player.getString("WeightClass");
                                String ageClass = player.getString("AgeClass");
                                String tournament_id = player.getString("TournamentID");
                                String firstName = player.getString("FirstName");
                                String lastName = player.getString("LastName");
                                String dateOfBirth = player.getString("DateOfBirth");
                                String height = player.getString("Height");
                                String weight = player.getString("Weight");
                                String gender = player.getString("Gender");

                                boolean isInserted = myDb.insertContestants(contestant_id, weightClass, ageClass, tournament_id, firstName,
                                        lastName, dateOfBirth, height, weight, gender);

                                if(isInserted)
                                    Toast.makeText(MainActivity.this, "Pobrano zawodnikow", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        requestQueue.add(jsonObjectRequest);
        g.setPlayersState(true);
    }

    public void downloadTournaments() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, tournamentsUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray tournamentJA = response.getJSONArray("tournaments");

                            for (int i = 0; i < tournamentJA.length(); i++) {
                                JSONObject tournament = tournamentJA.getJSONObject(i);

                                String tournamentID = tournament.getString("TournamentID");
                                String name = tournament.getString("Name");
                                String location = tournament.getString("Location");
                                String startDate = tournament.getString("StartDate");
                                String endDate = tournament.getString("EndDate");
                                String info = tournament.getString("Info");

                                boolean isInserted = myDb.insertTournaments(tournamentID, name, location, startDate, endDate, info);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        requestQueue.add(jsonObjectRequest);
        g.setTournamentsState(true);
    }

    public void downloadMatches() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, allBattlesUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("battles");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject battle = jsonArray.getJSONObject(i);

                        String match_id = battle.getString("MatchID");
                        String tournament_id = battle.getString("TournamentID");
                        String contestant_1ID = battle.getString("Contestant1ID");
                        String contestant_2ID = battle.getString("Contestant2ID");
                        String contestant1Res = battle.getString("Contestant1Result");
                        String contestant2Res = battle.getString("Contestant2Result");
                        String startDate = battle.getString("StartDate");
                        String endDate = battle.getString("EndDate");
                        String duration = battle.getString("Duration");

                        myDb.insertMatches(match_id, tournament_id, contestant_1ID, contestant_2ID, contestant1Res, contestant2Res,
                                startDate, endDate, duration);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    public void exitApp(View view) {
        finish();
        System.exit(0);
    }
}
