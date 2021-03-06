package com.nullpointerexception.retrogames.Components;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nullpointerexception.retrogames.App;

import java.util.List;
import java.util.Vector;

public class BackEndInterface
{

    private static BackEndInterface instance = new BackEndInterface();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef;
    private static Context context;

    private BackEndInterface() { }

    public static void initialize(Context ctx)
    {
        context = ctx;

        SharedPreferences prefs = context.getSharedPreferences(App.APP_VARIABLES, Context.MODE_PRIVATE);
        if(prefs.getBoolean(App.PREFS_INVALIDATE_FIREBASE_SCORES, false))
            invalidateFirebaseScores();
    }

    public static synchronized BackEndInterface get() { return instance; }

    /**
     * Scrive sul database di Firebase lo score e il totalscore ottenuto dall'utente in un determinato gioco
     * @param game stringa conentente il nome del gioco
     * @param nickname stringa contenente il nome del giocatore
     * @param score intero contenente il punteggio ottenuto dal giocatore
     * @param totalscore intero conente il punteggio complessivo ottenuto dal giocatore
     */
    public void writeScoreFirebase(String game, String nickname, long score, long totalscore)
    {
        if( ! isNetworkAvailable())
        {
            SharedPreferences prefs = context.getSharedPreferences(App.APP_VARIABLES, Context.MODE_PRIVATE);
            prefs.edit().putBoolean(App.PREFS_INVALIDATE_FIREBASE_SCORES, true).apply();
        }

        //Scrittura dello score di un singolo gioco
        myRef = database.getReference(game).child(nickname);
        myRef.setValue(score)
                .addOnSuccessListener(aVoid ->
                {

                })
                .addOnFailureListener(e ->
                {
                    // Write failed
                    Log.e("writeScoreFirebase", "Elemento non scritto");
                });

        //Scrittura del nuovo totalscore
        myRef = database.getReference(App.TOTALSCORE).child(nickname);
        myRef.setValue(totalscore)
                .addOnSuccessListener(aVoid ->
                {

                })
                .addOnFailureListener(e ->
                {
                    // Write failed
                    Log.e("writeScoreFirebase", "Elemento non scritto");
                });
    }

    /**
     * Scrive sul database di Firebase lo score ottenuto dall'utente in un determinato gioco
     * @param game stringa conentente il nome del gioco
     * @param nickname stringa contenente il nome del giocatore
     * @param score intero contenente il punteggio ottenuto dal giocatore
     */
    public void writeScoreFirebase(String game, String nickname, long score)
    {
        if( ! isNetworkAvailable())
        {
            SharedPreferences prefs = context.getSharedPreferences(App.APP_VARIABLES, Context.MODE_PRIVATE);
            prefs.edit().putBoolean(App.PREFS_INVALIDATE_FIREBASE_SCORES, true).apply();
        }

        //Scrittura dello score di un singolo gioco
        myRef = database.getReference(game).child(nickname);
        myRef.setValue(score)
                .addOnSuccessListener(aVoid ->
                {

                })
                .addOnFailureListener(e ->
                {
                    // Write failed
                    Log.e("writeScoreFirebase", "Elemento non scritto");
                });

    }

    /**
     * Scrive sul database di Firebase tutti gli score ottenuti dall'utente
     * @param nickname stringa contenente il nome del giocatore
     */
    public void writeScoreFirebase(String nickname)
    {
        if( ! isNetworkAvailable())
        {
            SharedPreferences prefs = context.getSharedPreferences(App.APP_VARIABLES, Context.MODE_PRIVATE);
            prefs.edit().putBoolean(App.PREFS_INVALIDATE_FIREBASE_SCORES, true).apply();
        }
        long score = 0;

        //Scrittura dello score di un tetris
        score = App.scoreboardDao.getScore(App.TETRIS);
        if(score != 0)
        {
            myRef = database.getReference(App.TETRIS).child(nickname);
            myRef.setValue(score)
                    .addOnSuccessListener(aVoid ->
                    {

                    })
                    .addOnFailureListener(e ->
                    {
                        // Write failed
                        Log.e("writeScoreFirebase", "Elemento non scritto");
                    });
        }

        //Scrittura dello score di un snake
        score = App.scoreboardDao.getScore(App.SNAKE);
        if(score != 0)
        {
            myRef = database.getReference(App.SNAKE).child(nickname);
            myRef.setValue(score)
                    .addOnSuccessListener(aVoid ->
                    {

                    })
                    .addOnFailureListener(e ->
                    {
                        // Write failed
                        Log.e("writeScoreFirebase", "Elemento non scritto");
                    });
        }

        //Scrittura dello score di un pong
        score = App.scoreboardDao.getScore(App.PONG);
        if(score != 0)
        {
            myRef = database.getReference(App.PONG).child(nickname);
            myRef.setValue(score)
                    .addOnSuccessListener(aVoid ->
                    {

                    })
                    .addOnFailureListener(e ->
                    {
                        // Write failed
                        Log.e("writeScoreFirebase", "Elemento non scritto");
                    });
        }

        //Scrittura dello score di un breakout
        score = App.scoreboardDao.getScore(App.BREAKOUT);
        if(score != 0)
        {
            myRef = database.getReference(App.BREAKOUT).child(nickname);
            myRef.setValue(score)
                    .addOnSuccessListener(aVoid ->
                    {

                    })
                    .addOnFailureListener(e ->
                    {
                        // Write failed
                        Log.e("writeScoreFirebase", "Elemento non scritto");
                    });
        }

        //Scrittura dello score di un hole
        score = App.scoreboardDao.getScore(App.HOLE);
        if(score != 0)
        {
            myRef = database.getReference(App.HOLE).child(nickname);
            myRef.setValue(score)
                    .addOnSuccessListener(aVoid ->
                    {

                    })
                    .addOnFailureListener(e ->
                    {
                        // Write failed
                        Log.e("writeScoreFirebase", "Elemento non scritto");
                    });
        }

    }

    /**
     * Legge dal database lo score ottenuto dall'utente in un determinato gioco o nella classifica globale
     * @param child stringa contenente il nodo dell'albero a cui si fa riferimento
     * @param nickname stringa contente il nome dell'utente da cui si vuole ricavare lo score
     * @param listener definizione delle operazioni da compiere una volta ricevuto il dato
     */
    public void readScoreFirebase(String child, String nickname, final OnDataReceivedListener listener)
    {
        myRef = database.getReference(child).child(nickname);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue() != null)
                {
                    long value = dataSnapshot.getValue(Long.class);
                    if(listener != null)
                        listener.onDataReceived(true, String.valueOf(value));
                }
                else if(listener != null)
                    listener.onDataReceived(false, null);

                if(myRef != null)
                    myRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                if(listener != null)
                    listener.onDataReceived(false, String.valueOf(-1));

                if(myRef != null)
                    myRef.removeEventListener(this);
            }
        });
    }

    /**
     * Legge dal database lo score ottenuto dall'utente in un determinato gioco o nella classifica globale
     * @param child stringa contenente il nodo dell'albero a cui si fa riferimento
     * @param listener definizione delle operazioni da compiere una volta ricevuto il dato
     */
    public void readAllScoresFirebase(String child, final OnQueryResultListener listener)
    {
        Query query = database.getReference(child).orderByValue();

        List<Scoreboard> scoresList = new Vector<>();
        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren())
                {
                    Scoreboard scoreboard = new Scoreboard();
                    scoreboard.setGame(child);
                    scoreboard.setNickname(childSnapshot.getKey());

                    long value = childSnapshot.getValue(Long.class);
                    scoreboard.setScore(value);

                    scoresList.add(0, scoreboard);

                    if(scoresList.size() == dataSnapshot.getChildrenCount() && listener != null)
                        listener.onQueryResult(true, scoresList);
                }

                if(myRef != null)
                    myRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                if(listener != null)
                    listener.onQueryResult(false, null);

                if(myRef != null)
                    myRef.removeEventListener(this);
            }
        });

    }

    /**
     * Scrivi sul database il nickname dell'utente
     * @param email stringa contenente l'email che l'utente ha utilizzato per la registrazione
     * @param nickname stringa contenente il nickname scelto dall'utente durante la registrazione
     */
    public void writeUser(String email, String nickname) {
        String newEmail = changeChars(email,'.', '%');
        myRef = database.getReference(App.USER).child(newEmail);
        myRef.setValue(nickname)
                .addOnSuccessListener(aVoid ->
                {

                })
                .addOnFailureListener(e ->
                {
                    // Write failed
                    Log.e("writeUserFirebase", "Elemento non scritto");
                });
    }

    /**
     * Legge sul database il nickname di un giocatore
     * @param email stringa contenente l'email dell'utente da cui si vuole recuperare la stringa
     * @param listener definizione delle operazioni da compiere una volta ricevuto il dato
     */
    public void readUser(String email, final OnDataReceivedListener listener) {
        String newEmail = changeChars(email,'.', '%');
        myRef = database.getReference(App.USER).child(newEmail);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nickname = dataSnapshot.getValue(String.class);
                if(listener != null)
                    listener.onDataReceived(true, nickname);

                if(myRef != null)
                    myRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                if(listener != null)
                    listener.onDataReceived(true, "");

                if(myRef != null)
                    myRef.removeEventListener(this);
            }
        });
    }

    /**
     * Legge sul database tutti i nickname presenti
     * @param listener definizione delle operazioni da compiere una volta ricevuto il dato
     */
    public void readAllNickname(final OnQueryResultListener2 listener){
        List<String> nicknames = new Vector<>();

        Query query = database.getReference(App.USER);

        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren())
                {
                    String nickname = childSnapshot.getValue(String.class);

                    nicknames.add(nickname);

                    if(nicknames.size() == dataSnapshot.getChildrenCount() && listener != null)
                        listener.onQueryResult(true, nicknames);
                }

                if(myRef != null)
                    myRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError error)
            {
                // Failed to read value
                if(listener != null)
                    listener.onQueryResult(false, null);

                if(myRef != null)
                    myRef.removeEventListener(this);
            }
        });
    }


    /**
     * Sostituisce un carattere della stringa con uno nuovo
     * @param input stringa in input da modificare
     * @param toRemove carattere da togliere
     * @param nuovo carattere nuovo da inserire al posto di quello da rimuovere
     * @return restiruisce la nuova stringa con i caratteri sostituiti
     */
    private String changeChars(String input, char toRemove, char nuovo) {
        String output = "";
        for(int i = 0; i < input.length(); i++){
            if(input.charAt(i) == toRemove)
                output += nuovo;
            else
                output += input.charAt(i);
        }
        return output;
    }

    /**
     * Interfaccia usata per gestire le azioni da compiere
     * una volta ricevuto il dato da Firebase
     */
    public interface OnDataReceivedListener {
        void onDataReceived(boolean success, String value);
    }

    /**
     * Interfaccia usata per gestire le azioni da compiere
     * una volta ricevuto il risultato di una query assegnata a Firebase
     */
    public interface OnQueryResultListener {
        void onQueryResult(boolean success, List<Scoreboard> scoreboardList);
    }

    /**
     * Interfaccia usata per gestire le azioni da compiere
     * una volta ricevuto il risultato di una query assegnata a Firebase
     */
    public interface OnQueryResultListener2 {
        void onQueryResult(boolean success, List<String> nicknames);
    }

    public static boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static void invalidateFirebaseScores()
    {
        SharedPreferences prefsNickname = context.getSharedPreferences(App.USER, Context.MODE_PRIVATE);

        String nickname = prefsNickname.getString(App.NICKNAME, "");
        long totalScore = App.scoreboardDao.getScore(App.TOTALSCORE);

        List<Scoreboard> scoreboardList = App.scoreboardDao.getAll();
        for(Scoreboard score : scoreboardList)
            if( ! score.getGame().equals(App.TOTALSCORE))
                instance.writeScoreFirebase(score.getGame(), nickname, score.getScore(), totalScore);

        SharedPreferences prefs = context.getSharedPreferences(App.APP_VARIABLES, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(App.PREFS_INVALIDATE_FIREBASE_SCORES, false).apply();
    }

}
