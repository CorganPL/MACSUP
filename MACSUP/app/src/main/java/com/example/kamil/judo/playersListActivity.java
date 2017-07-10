package com.example.kamil.judo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
 * Created by Kamil on 2017-04-28.
 */
public class playersListActivity extends AppCompatActivity {

    public static final String RECEIVED_ID = "Received id";
    TextView playersTv;
    RequestQueue requestQueue;
    String showUrl = "http://192.168.56.1/php1/showInfo.php";
    ImageButton refreshBtn;
    ArrayList<String> players;
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
        setContentView(R.layout.playerslist_layout);
        layout =(RelativeLayout)findViewById(R.id.layout);
        layout.setBackgroundResource(R.drawable.background);

        g = Globals.getInstance();
        ctx = getApplicationContext();

        myDb = databaseHelper.getInstance(ctx);

        playersTv = (TextView) findViewById(R.id.textView);
        refreshBtn = (ImageButton) findViewById(R.id.refBtn);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        players = new ArrayList<>();
        additionalInfo = new ArrayList<>();
        playersIds = new ArrayList<>();
        lv = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, players);


        if (g.getPlayersState() == false) {
    //        Toast.makeText(playersListActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();
            downloadData(view);
        } else {
    //        Toast.makeText(playersListActivity.this, "Selecting data from DB...", Toast.LENGTH_SHORT).show();
            selectDataFromDB();
        }
    }

    public void downloadData(View view) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, showUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray playersJA = response.getJSONArray("players");
                            lv.setAdapter(adapter);
                            players.clear();
                            additionalInfo.clear();

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

                                players.add(firstName + " " + lastName + " (id: " + contestant_id + ") ");
                                completeInfo = ("Masa: \n" + weight + " (klasa wagowa: " + weightClass + ")\n\n" + "Data urodzenia: \n" + dateOfBirth +
                                        " (przedział wiekowy: " + ageClass);

                                additionalInfo.add(completeInfo);
                                playersIds.add(contestant_id);

                                boolean isInserted = myDb.insertContestants(contestant_id, weightClass, ageClass, tournament_id, firstName,
                                        lastName, dateOfBirth, height, weight, gender);
                            //    myDb.insertMatchesResult(contestant_id);

                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                        showAlertDialog(view, players.get(position), additionalInfo.get(position), playersIds.get(position));

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
                        Toast.makeText(playersListActivity.this, "Cos poszlo nie tak. Sprawdz polaczenie.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        requestQueue.add(jsonObjectRequest);
        g.setPlayersState(true);
    }


    public void selectDataFromDB() {
        Cursor res = myDb.getAllPlayers();

        if(res.getCount() == 0)
                Toast.makeText(playersListActivity.this, "Nie znaleziono", Toast.LENGTH_SHORT).show();

        lv.setAdapter(adapter);
        players.clear();
        additionalInfo.clear();

        while (res.moveToNext()) {

            String contestant_id = res.getString(0);

            myDb.getMatchesResults(contestant_id);

            String weightClass = res.getString(1);
            String ageClass = res.getString(2);
            String tournament_id = res.getString(3);
            String firstName = res.getString(4);
            String lastName = res.getString(5);
            String dateOfBirth = res.getString(6);
            String height = res.getString(7);
            String weight = res.getString(8);
            String gender = res.getString(9);

            String battlesCount = res.getString(10);
            String battlesWon = res.getString(11);
            String battlesDraw = res.getString(12);


            players.add(firstName + " " + lastName + " (id: " + contestant_id + ") ");
            completeInfo = ("Masa: \n" + weight + " (przedział: " + weightClass + ")\n\n" + "Data urodzenia: \n" + dateOfBirth +
                    "\n(przedział: " + ageClass + ")\n\nStoczone walki: " + battlesCount + "\nWygrane: " + battlesWon + "\nZremisowane: " + battlesDraw);

            additionalInfo.add(completeInfo);
            playersIds.add(contestant_id);
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                showAlertDialog(view, players.get(position), additionalInfo.get(position), playersIds.get(position));
            }
        });
    }

    public void showAlertDialog(View view, final String name,  String info, final String pID) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(name);
        alertDialog.setMessage(info);

        alertDialog.setPositiveButton("Najblizsza walka", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                g.setPlayerID(pID);
                Intent intent = new Intent(ctx, battleActivity.class);
                intent.putExtra(RECEIVED_ID, pID);
                startActivity(intent);
            }
        });
        alertDialog.setNeutralButton("Stoczone walki", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                g.setPlayerID(pID);
                Intent intent = new Intent(ctx, battlesListActivity.class);
                intent.putExtra(RECEIVED_ID, pID);
                startActivity(intent);

            }
        });

        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<String> templist = new ArrayList<String>();

                for (String temp : players) {
                    if (temp.toLowerCase().contains(newText.toLowerCase())) {
                        templist.add(temp);
                    }
                }
                ArrayAdapter<String> adapterTemp = new ArrayAdapter<String>(playersListActivity.this, android.R.layout.simple_list_item_1, templist);
                lv.setAdapter(adapterTemp);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }


        });

        return super.onCreateOptionsMenu(menu);
    }

    public void back(View view) {
        this.finish();
    }

    public void switchLayoutTournament(View v) {
        Intent intent = new Intent(ctx,tournamentsActivity.class);
        startActivity(intent);
    }
}
