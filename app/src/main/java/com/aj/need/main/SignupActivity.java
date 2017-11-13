package com.aj.need.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aj.need.R;
import com.aj.need.db.IO;
import com.aj.need.db.colls.USERS;
import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.utils.Avail;
import com.aj.need.tools.utils.KeyboardServices;
import com.aj.need.tools.utils.PatternsHolder;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputUsername;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBarFragment progressBarFragment;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        btnSignIn = findViewById(R.id.sign_in_button);
        btnSignUp = findViewById(R.id.sign_up_button);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        inputUsername = findViewById(R.id.username);

        progressBarFragment = (ProgressBarFragment) getSupportFragmentManager().findFragmentById(R.id.waiter_modal_fragment);

        btnResetPassword = findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.start(SignupActivity.this);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();
                final String username = inputUsername.getText().toString().trim();

                if (!validateForm(email, password, username)) return;

                KeyboardServices.dismiss(SignupActivity.this, inputPassword);
                progressBarFragment.show();
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                    USERS.getCurrentUserRef().set(new User(username, Avail.AVAILABLE, ((App) getApplication()).getLastLocalKnownLocation()))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    IO.getCurrentUser().sendEmailVerification();
                                                    __.showLongToast(SignupActivity.this, getString(R.string.check_email_has_been_sent));
                                                    LoginActivity.start(SignupActivity.this);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressBarFragment.hide();
                                                    __.showShortToast(SignupActivity.this, getString(R.string.an_error_occured_try_again));
                                                    e.printStackTrace();
                                                }
                                            });
                                else {
                                    progressBarFragment.hide();
                                    //// TODO: 01/10/2017 check what exc and swow the right msg error
                                    __.showShortToast(SignupActivity.this, getString(R.string.singup_auth_failed));
                                    Log.d("FirebaseAuth", "" + task.getException());
                                }
                            }
                        });
            }
        });
    }


    private boolean validateForm(String email, String password, String username) {
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.email_is_required));
            return false;
        } else if (!email.contains("@")) {
            inputEmail.setError(getString(R.string.invalid_email));
            return false;
        } else
            inputEmail.setError(null);

        if (TextUtils.isEmpty(password)) {
            inputPassword.setError(getString(R.string.password_is_required));
            return false;
        } else if (password.length() < 6) {
            inputPassword.setError(getString(R.string.minimum_password));
            return false;
        } else
            inputPassword.setError(null);

        if (TextUtils.isEmpty(username)) {
            inputUsername.setError(getString(R.string.username_is_required));
            return false;
        } else if (username.length() < 3) {
            inputUsername.setError(getString(R.string.minimum_username));
            return false;
        } else if (!PatternsHolder.isValidUsername(username)) {
            inputUsername.setError(getString(R.string.invalid_username));
            return false;
        } else
            inputUsername.setError(null);

        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBarFragment.hide();
    }


}