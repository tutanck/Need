package com.aj.need.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aj.need.R;
import com.aj.need.db.IO;
import com.aj.need.db.colls.USERS;
import com.aj.need.domain.components.needs.UserNeedSaveActivity;
import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.utils.Avail;
import com.aj.need.tools.utils.KeyboardServices;
import com.aj.need.tools.utils.__;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private ProgressBarFragment progressBarFragment;
    private Button btnSignup, btnLogin, btnReset;

    private FirebaseAuth auth;


    public static void start(Activity context) {
        context.startActivity(new Intent(context, LoginActivity.class));
        context.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBarFragment = (ProgressBarFragment) getSupportFragmentManager().findFragmentById(R.id.waiter_modal_fragment);
        btnSignup = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        btnReset = findViewById(R.id.btn_reset_password);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (!validateForm(email, password)) return;

                KeyboardServices.dismiss(LoginActivity.this, inputPassword);
                progressBarFragment.show();
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (!IO.getCurrentUser().isEmailVerified()) {
                                        progressBarFragment.hide();
                                        
                                        //// TODO: 12/11/2017 set timer to not send to email  
                                        Snackbar.make(btnLogin, "Votre email n'est pas vérifié!", Snackbar.LENGTH_LONG)
                                                .setAction(R.string.resend_email, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        IO.getCurrentUser().sendEmailVerification();
                                                        __.showLongToast(LoginActivity.this, getString(R.string.check_email_has_been_sent));
                                                    }
                                                }).show();
                                        return;
                                    }

                                    letsGo();

                                } else {
                                    progressBarFragment.hide();
                                    __.showShortToast(LoginActivity.this, getString(R.string.signin_auth_failed));
                                    Log.d("FirebaseAuth", "" + task.getException());//// TODO: 01/10/2017 check what exc and swow the right msg error
                                }
                            }
                        });
            }
        });
    }


    private void letsGo() {
        USERS.getCurrentUserRef().update(USERS.availabilityKey, Avail.AVAILABLE);
        USERS.getCurrentUserRef().update(USERS.instanceIDTokenKey, IO.getInstanceIDToken());
        MainActivity.start(LoginActivity.this);
    }


    private boolean validateForm(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Email obligatoire!");
            return false;
        } else
            inputEmail.setError(null);

        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Mot de passe obligatoire!");
            return false;
        } else
            inputPassword.setError(null);

        return true;
    }
}
