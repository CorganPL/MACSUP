package com.example.kamil.judo;

import android.content.DialogInterface;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Scanner;

/**
 * Created by Kamil on 2017-05-30.
 */
public class rulesActivity extends AppCompatActivity {

    RelativeLayout layout;
    View view;

    int judoPath, jjPath, karatePath, tkwPath, aikidoPath;
    String judoUrl, jjUrl, karateUrl, tkwUrl, aikidoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules_layout);
        layout = (RelativeLayout) findViewById(R.id.layout);
        layout.setBackgroundResource(R.drawable.background);

        judoPath = this.getResources().getIdentifier("judo_rules", "raw", this.getPackageName());
        jjPath = this.getResources().getIdentifier("jj_rules", "raw", this.getPackageName());
        karatePath = this.getResources().getIdentifier("karate_rules", "raw", this.getPackageName());
        tkwPath = this.getResources().getIdentifier("taekwondo_rules", "raw", this.getPackageName());

        judoUrl = "http://pzjudo.pl/docs/reg/regulamin.pdf";
        jjUrl = "http://judonadarzyn.pl/uploads/images/do_pobrania/przepisy(1).pdf";
        karateUrl = "http://www.dojotorun.pl/dodatki_do_strony/pdf/WKF_przepisy.pdf";
        tkwUrl = "http://www.put.org.pl/wp-content/uploads/2011/09/regulamin-4.8.pdf";
        aikidoUrl = "http://aikido.org.pl/informacje-o-aikido/";
    }

    public void showJudoInfo(View view) {
            showAlertDialog(view, "Judo", readFromFile(judoPath), judoUrl);
        }

    public void showJJInfo(View view) {
        showAlertDialog(view, "Jiu-Jitsu", readFromFile(jjPath), jjUrl);
    }

    public void showKarateInfo(View view) {
        showAlertDialog(view, "Karate", readFromFile(karatePath), karateUrl);
    }

    public void showTkwInfo(View view) {
        showAlertDialog(view, "Taekwondo", readFromFile(tkwPath), tkwUrl);
    }

    public void showAikidoInfo(View view) {
        showAlertDialog(view, "Taekwondo", "", aikidoUrl);
    }


    public String readFromFile (int pathId)  {
        Scanner s = new Scanner(getResources().openRawResource(pathId));
        s.useDelimiter(" ");
        String text = "";
        try {
            while (s.hasNext()) {
                text += s.nextLine();
            }
            return text;
        } finally {
            s.close();
        }
    }

    public void showAlertDialog(View view, final String title,  String info, final String url) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(info);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.setNeutralButton("Zobacz wiecej", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
        alertDialog.show();
    }

    public void back(View view) {
        this.finish();
    }


}
