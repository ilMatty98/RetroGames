package com.nullpointerexception.retrogames.Hole;


import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.nullpointerexception.retrogames.App;
import com.nullpointerexception.retrogames.R;


public class MainActivityHole extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private CanvasView contentView;
    private TextView textViewTopScore, textViewScore, textViewLife, mPause;
    private long topScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_hole);

        textViewTopScore = findViewById(R.id.textViewTopScore);
        textViewScore = findViewById(R.id.textViewScore);
        textViewLife = findViewById(R.id.textViewLife);
        mPause = findViewById(R.id.pause_tv);

        contentView = findViewById(R.id.fullscreen_content);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        SharedPreferences prefs = getSharedPreferences(App.APP_VARIABLES, MODE_PRIVATE);
        if(prefs.getBoolean(App.HOLE_TUTORIAL, true))
        {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.hole_welcome)
                    .setMessage(R.string.hole_tutorial)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialog_ok, (a, b) ->
                            prefs.edit().putBoolean(App.HOLE_TUTORIAL, false).apply())
                    .show();
        }



        //Prendo il topscore dal database locale
        if(App.scoreboardDao.getGame(App.HOLE) != null) //Controllo se già esiste un topscore
            //Esiste già un topscore
            topScore = App.scoreboardDao.getScore(App.HOLE); //Leggo il vecchio topscore
        else
            //Non esiste un topscore
            topScore = 0;

        textViewTopScore.setText(getResources().getString(R.string.high_score_) + topScore);
        textViewScore.setText(getResources().getString(R.string.score_0));
        textViewLife.setText(getResources().getString(R.string.life_3));

        contentView.setOnChangeScoreListener(new CanvasView.OnChangeScoreListener() {
            @Override
            public void onChangeScore(long score, int life) {
                if(score > topScore)
                {
                    topScore = score;
                    textViewTopScore.setText(getResources().getString(R.string.high_score) + topScore);
                }
                textViewScore.setText(getResources().getString(R.string.score) + score);

                String itemsFound = getResources().getQuantityString(R.plurals.life, life);
                textViewLife.setText(itemsFound+ life);
            }
        });

        //Listener textview pausa
        mPause.setOnClickListener(view -> {
            if(contentView.isInPause()) {   //se il gioco è in pausa...
                mPause.setVisibility(View.GONE);   //La textview diventa invisibile
                contentView.setInPause(false);     //il gioco riprende
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Registrare questa classe come listener per il sensore accelerometro
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        //Se il gioco è in pause, setta la textView in visibile
        if(contentView.isInPause())
            mPause.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Mette in pausa il gioco
        contentView.setInPause(true);
    }

    @Override
    protected void onStop() {
        // Annulla la registrazione del listener
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    /**
     * Acquisisce i dati dal sensore accelerometro ed eventualmente giroscopio
     * @param sensorEvent evento del sensore
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];    //coordinata x
            float y = sensorEvent.values[1];    //coordinata y
            contentView.changeVelocity(y, x); //Invia i dati
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


}
