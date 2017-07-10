package com.example.kamil.judo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kamil on 2017-05-04.
 */
public class battleActivity extends Activity {

    String battleURL = "http://192.168.56.1/php1/showBattle.php";
    EditText idEditText;
    Button OK;
    TextView yourName, weightTv, ageTv, battleStart, matTv, enemyNameTv;
    RequestQueue queue;
    String id;
    databaseHelper myDb;
    RelativeLayout layout;
    String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battle_layout);

        layout =(RelativeLayout)findViewById(R.id.layout);
        layout.setBackgroundResource(R.drawable.background);

        myDb = databaseHelper.getInstance(this.getApplicationContext());

        idEditText = (EditText) findViewById(R.id.editText);
        Intent intent = getIntent();
        String recId = intent.getStringExtra(playersListActivity.RECEIVED_ID);
        idEditText.setText(recId);

        OK = (Button) findViewById(R.id.okBtn);
        yourName = (TextView)findViewById(R.id.nameTv);
        weightTv = (TextView)findViewById(R.id.weightTv);
        ageTv = (TextView)findViewById(R.id.ageTv);
        battleStart = (TextView)findViewById(R.id.timeTv);
        enemyNameTv = (TextView)findViewById(R.id.enemyTv);

        queue = Volley.newRequestQueue(getApplicationContext());

        OK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myId = idEditText.getText().toString();
                showInfo(myId);
            }
        });
    }

    public void showInfo(String pID) {

        Cursor res = myDb.getUpcomingMatch(pID);
        Cursor contestantRes;

        if(res.getCount() == 0) {
            Toast.makeText(battleActivity.this, "Nie znaleziono zawodnika o podanym ID", Toast.LENGTH_SHORT).show();
            return;
        }

            String matchID = res.getString(0);
            String tournamentID = res.getString(1);
            String contestant1ID = res.getString(2);
            String contestant2ID = res.getString(3);
            String contestant1res = res.getString(4);
            String contestant2res = res.getString(5);
            String startDate = res.getString(6);
            String endDate = res.getString(7);
            String duration = res.getString(8);


            if (contestant1ID == pID)
                contestantRes = myDb.getContestantInfo(contestant2ID);
            else
                contestantRes = myDb.getContestantInfo(contestant1ID);

            String enemyFirstName = contestantRes.getString(0);
            String enemyLastName = contestantRes.getString(1);

        if (contestant1ID == pID)
            contestantRes = myDb.getContestantInfo(contestant1ID);
        else
            contestantRes = myDb.getContestantInfo(contestant2ID);

        String myFirstName = contestantRes.getString(0);
        String myLastName = contestantRes.getString(1);
        String weightClass = contestantRes.getString(2);
        String ageClass = contestantRes.getString(3);

            yourName.setText(myFirstName + " " + myLastName);
            weightTv.setText(weightClass);
            ageTv.setText(ageClass);
            battleStart.setText(startDate);
            enemyNameTv.setText(enemyFirstName + " " + enemyLastName);
        }


    public void downloadMatch() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, battleURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("battle");
                    JSONObject data = jsonArray.getJSONObject(0);

                    String match_id = data.getString("MatchID");
                    String tournament_id = data.getString("TournamentID");
                    String contestant_1ID = data.getString("Contestant1ID");
                    String contestant_2ID = data.getString("Contestant2ID");
                    String contestant1Res = data.getString("Contestant1Result");
                    String contestant2Res = data.getString("Contestant2Result");
                    String startDate = data.getString("StartDate");
                    String endDate = data.getString("EndDate");
                    String duration = data.getString("Duration");

                    boolean isInserted = myDb.insertMatches(match_id, tournament_id, contestant_1ID, contestant_2ID, contestant1Res, contestant2Res, startDate, endDate, duration);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(battleActivity.this, "Cos poszlo nie tak. Sprawdz polaczenie.",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parameters = new HashMap<String, String>();
                parameters.put("zawodnik_id", idEditText.getText().toString());
                return parameters;
            }
        };
        queue.add(stringRequest);
    }

    public void back(View view) {
        this.finish();
    }
}