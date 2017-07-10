package com.example.kamil.judo;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kamil on 2017-04-28.
 */
public class battlesListActivity extends AppCompatActivity {

    public static final String RECEIVED_ID = "Received id";
    TextView playersTv;
    RequestQueue requestQueue;
    String showUrl = "http://192.168.56.1/php1/showBattle.php";
    ArrayList<String> matches;
    ArrayList<String> additionalInfo;
    ArrayList<String> playersIds;
    ListView lv;
    ArrayAdapter<String> adapter;
    Context ctx;
    databaseHelper myDb;
    Globals g;
    View view;
    String completeInfo;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battleslist_layout);
        layout =(RelativeLayout)findViewById(R.id.layout);
        layout.setBackgroundResource(R.drawable.background);

        myDb = databaseHelper.getInstance(this.getApplicationContext());
        g = Globals.getInstance();

        ctx = getApplicationContext();


        playersTv = (TextView) findViewById(R.id.textView2);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        matches = new ArrayList<>();
        additionalInfo = new ArrayList<>();
        playersIds = new ArrayList<>();
        lv = (ListView) findViewById(R.id.battlesList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, matches);

        //downloadData(view);
        selectDataFromDB();

    }

    public void downloadData(View view) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, showUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("battles");

                            lv.setAdapter(adapter);
                            matches.clear();
                            additionalInfo.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                               JSONObject player = jsonArray.getJSONObject(i);

                                String match_id = player.getString("MatchID");
                                String tournament_id = player.getString("TournamentID");
                                String contestant_1ID = player.getString("Contestant1ID");
                                String contestant_2ID = player.getString("Contestant2ID");
                                String contestant1Res = player.getString("Contestant1Result");
                                String contestant2Res = player.getString("Contestant2Result");
                                String startDate = player.getString("StartDate");
                                String endDate = player.getString("EndDate");
                                String duration = player.getString("Duration");

                            myDb.insertMatches(match_id, tournament_id, contestant_1ID, contestant_2ID, contestant1Res, contestant2Res,
                                    startDate, endDate, duration);


                                matches.add(match_id);
                                completeInfo = (contestant_1ID + " vs. " + contestant_2ID);

                                additionalInfo.add(completeInfo);


                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                        showAlertDialog(view, matches.get(position), additionalInfo.get(position));
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(battlesListActivity.this, "Cos poszlo nie tak. Sprawdz polaczenie.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("zawodnik_id", g.getPlayerID());
                        return parameters;

            }
        };
        requestQueue.add(stringRequest);
        g.setPlayersState(true);
    }


    public void selectDataFromDB() {
        Cursor res = myDb.getAllMatches(g.getPlayerID());
        Cursor contestantRes;
        Cursor matchRes;

        if(res.getCount() == 0) {
            Toast.makeText(battlesListActivity.this, "Nie znaleziono", Toast.LENGTH_SHORT).show();
            return;
        }


        lv.setAdapter(adapter);
        matches.clear();
        additionalInfo.clear();

        while (res.moveToNext()) {

            String matchID = res.getString(0);
            String tournamentID = res.getString(1);
            String contestant1ID = res.getString(2);
            String contestant2ID = res.getString(3);
            String contestant1res = res.getString(4);
            String contestant2res = res.getString(5);
            String startDate = res.getString(6);
            String endDate = res.getString(7);
            String duration = res.getString(8);

            contestantRes = myDb.getContestantInfo(contestant1ID);
                String firstName = contestantRes.getString(0);
                String lastName = contestantRes.getString(1);
            contestantRes = myDb.getContestantInfo(contestant2ID);
            contestantRes.moveToFirst();
                String firstName2 = contestantRes.getString(0);
                String lastName2 = contestantRes.getString(1);
        /*    matchRes = myDb.getMatchesInfo(matchID);
                String result1 = matchRes.getString(0);
                String result2 = matchRes.getString(1);
                String start = matchRes.getString(2);
                String end = matchRes.getString(3);
                String duration = matchRes.getString(4);
                */

                matches.add(firstName + " " + lastName + " vs. " + firstName2 + " " + lastName2);
                completeInfo = ("Wynik: " + contestant1res + " - " + contestant2res +
                "\n\nRozpoczeto: " + startDate + "\nZakonczono: " + endDate + "\nWalka trwala: " + duration);

                additionalInfo.add(completeInfo);

        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                showAlertDialog(view, matches.get(position), additionalInfo.get(position));
            }
        });
    }

    public void showAlertDialog(View view, final String name,  String info) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(name);
        alertDialog.setMessage(info);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    public void back(View view) {
        this.finish();
    }


}
