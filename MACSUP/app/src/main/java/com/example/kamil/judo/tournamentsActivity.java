package com.example.kamil.judo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;

/**
 * Created by Kamil on 2017-05-25.
 */
public class tournamentsActivity extends AppCompatActivity {

    Context context;
    RequestQueue requestQueue;
    String tournamentsUrl = "http://192.168.56.1/php1/showTournament.php";
    ArrayList<String> tournaments;
    ArrayList<String> additionalInfo;
    ArrayList<String> tIDs;

    ListView lv;
    ArrayAdapter<String> adapter;
    databaseHelper myDb;
    Globals g;
    View view;

    String tournamentID;
    String name;
    String location;
    String startDate;
    String endDate;
    String info;
    String completeInfo;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tournaments_layout);
        layout =(RelativeLayout)findViewById(R.id.layout);
        layout.setBackgroundResource(R.drawable.background);

        g = Globals.getInstance();
        myDb = databaseHelper.getInstance(this.getApplicationContext());
        lv = (ListView) findViewById(R.id.tournament_lv);
        context = getApplicationContext();

        tournaments = new ArrayList<>();
        additionalInfo = new ArrayList<>();
        tIDs = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tournaments);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        if (!g.getTournamentsState()) {
      //      Toast.makeText(tournamentsActivity.this, "Downloading", Toast.LENGTH_LONG).show();
            downloadData(view);
        }
        else {
            selectDataFromDB();
       //     Toast.makeText(tournamentsActivity.this, "Selecting from db", Toast.LENGTH_LONG).show();
        }
    }

    public void downloadData(final View view) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, tournamentsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray tournamentJA = response.getJSONArray("tournaments");
                    lv.setAdapter(adapter);
                    tournaments.clear();
                    additionalInfo.clear();
                    tIDs.clear();

                    for (int i = 0; i < tournamentJA.length(); i++) {
                        JSONObject tournament = tournamentJA.getJSONObject(i);

                         tournamentID = tournament.getString("TournamentID");
                         name = tournament.getString("Name");
                         location = tournament.getString("Location");
                         startDate = tournament.getString("StartDate");
                         endDate = tournament.getString("EndDate");
                         info = tournament.getString("Info");

                        tournaments.add(name);
                        completeInfo = ("Lokalizacja: \n" + location + "\n\nStart: \n" + startDate + "\n\nDodatkowe informacje: \n" + info);

                        additionalInfo.add(completeInfo);
                        tIDs.add(tournamentID);

                        boolean isInserted = myDb.insertTournaments(tournamentID, name, location, startDate, endDate, info);

                   //     if(isInserted)
                    //        Toast.makeText(tournamentsActivity.this, "Pobrano dane", Toast.LENGTH_LONG).show();

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                showAlertDialog(view, tournaments.get(position), additionalInfo.get(position), tIDs.get(position));
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
                        Toast.makeText(tournamentsActivity.this, "Cos poszlo nie tak. Sprawdz polaczenie.", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
        });
        requestQueue.add(jsonObjectRequest);
        g.setTournamentsState(true);

    }

    public void selectDataFromDB() {
        Cursor res = myDb.getAllTournaments();
        if(res.getCount() == 0) {
            return;
        }

        lv.setAdapter(adapter);
        tournaments.clear();
        additionalInfo.clear();
        tIDs.clear();

        while (res.moveToNext()) {

            String tournamentID = res.getString(0);
            String name = res.getString(1);
            String location = res.getString(2);
            String startDate = res.getString(3);
            String endDate = res.getString(4);
            String info = res.getString(5);

            tournaments.add(name + "\n" + location + "\n" + startDate);
            additionalInfo.add(info);
            tIDs.add(tournamentID);
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                showAlertDialog(view, tournaments.get(position), additionalInfo.get(position), tIDs.get(position));
            }
        });
    }

    public void showAlertDialog(View view, final String title,  String info, final String tID) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(info);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                g.setTournamentID(tID);
                Toast.makeText(tournamentsActivity.this, "Wybrano " + title, Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();
    }

    public void back(View view) {
        this.finish();
    }

    public void switchLayoutPlayersList(View v) {
        Intent intent = new Intent(context,playersListActivity.class);
        startActivity(intent);
    }




}
