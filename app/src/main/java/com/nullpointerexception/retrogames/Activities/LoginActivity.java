package com.nullpointerexception.retrogames.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.nullpointerexception.retrogames.Components.AuthenticationManager;
import com.nullpointerexception.retrogames.Components.Blocker;
import com.nullpointerexception.retrogames.Components.User;
import com.nullpointerexception.retrogames.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mLogin;
    private TextView mSignin;
    private View googleSignInButton;

    private FirebaseAuth auth;
    private User currentUser;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();
        AuthenticationManager.LoginAttempt loginAttempt = AuthenticationManager.get().initialize(this);

        if(loginAttempt != null)
        {
            loginAttempt.addOnLoginResultListener(new AuthenticationManager.LoginAttempt.OnLoginResultListener() {
                @Override
                public void onLoginResult(boolean result) {
                    if(result)
                        Log.i("claudio", "inizializzazione fatta");
                }
            });
        }

        //Inizializzo l'UI
        initUI();

        //Rende l'activity transparente e a schermo intero
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        //Listener login
        mLogin.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View view) {
                if (!mBlocker.block()) {
                    if(checkFields()) {
                        AuthenticationManager.get().login(mEmail.getText().toString()
                                , mPassword.getText().toString())
                                .addOnLoginResultListener(new AuthenticationManager
                                        .LoginAttempt
                                        .OnLoginResultListener() {
                                    @Override
                                    public void onLoginResult(boolean result) {
                                        if(result){
                                            Log.i("claudio", "Loggato con successo");
                                            currentUser = AuthenticationManager.get().getUserLogged();
                                        Toast.makeText(getApplicationContext(), currentUser.getEmail()
                                        + currentUser.getId(), Toast.LENGTH_LONG).show();
                                            //showUserInfo();
                                        }else{
                                            Log.i("claudio", "errore nel log in");
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "Campi errati", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Listener registrazione
        mSignin.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View view) {
                if (!mBlocker.block()) {

                }
            }
        });

        //Listener log in con Google
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            private Blocker mBlocker = new Blocker();

            @Override
            public void onClick(View view) {
                if(!mBlocker.block()) {
                    AuthenticationManager.get().requestLoginWithGoogle(LoginActivity.this)
                            .addOnLoginResultListener(new AuthenticationManager.LoginAttempt.OnLoginResultListener() {
                                @Override
                                public void onLoginResult(boolean result) {
                                    if(result){
                                        Log.i("claudio", "Loggato con successo");
                                        currentUser = AuthenticationManager.get().getUserLogged();
                                    }else
                                        Log.i("claudio", "errore nel log in");
                                }
                            });
                }
            }
        });
    }

    private boolean checkFields() {
        boolean allRight = true;

        String email = mEmail.getText().toString();
        if(!emailCheck(email) || email.isEmpty())
            allRight = false;

        String psw = mPassword.getText().toString();
        if(psw.isEmpty() || psw.length() > 8)
            allRight = false;

        return allRight;
    }

    private boolean emailCheck(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);

        if(mat.matches())
            return true;
        else
            return false;
    }

    private void initUI() {
        mEmail = findViewById(R.id.emailTextField);
        mPassword = findViewById(R.id.passwordTextField);
        mLogin = findViewById(R.id.login_btn);
        mSignin = findViewById(R.id.signIn_tv);
        googleSignInButton = findViewById(R.id.googleSignInButton);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AuthenticationManager.get().loginWithGoogle(requestCode, data);
    }
}
