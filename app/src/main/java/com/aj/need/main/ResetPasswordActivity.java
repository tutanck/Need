package com.aj.need.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aj.need.R;
import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.utils.KeyboardServices;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnReset, btnBack;
    private FirebaseAuth auth;
    private ProgressBarFragment progressBarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        inputEmail = findViewById(R.id.email);
        btnReset = findViewById(R.id.btn_reset_password);
        btnBack = findViewById(R.id.btn_back);
        progressBarFragment = (ProgressBarFragment) getSupportFragmentManager().findFragmentById(R.id.waiter_modal_fragment);


        auth = FirebaseAuth.getInstance();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                if (!validateForm(email)) return;

                KeyboardServices.dismiss(ResetPasswordActivity.this, inputEmail);
                progressBarFragment.show();
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBarFragment.hide();
                                if (!task.isSuccessful()) {
                                    __.showLongToast(ResetPasswordActivity.this, getString(R.string.failed_to_reset_password));
                                    Log.d("FirebaseAuth", "" + task.getException());//// TODO: 01/10/2017 check what exc and swow the right msg error
                                } else {
                                    __.showLongToast(ResetPasswordActivity.this, getString(R.string.reset_password_request_email_has_been_sent));
                                    finish();
                                }
                            }
                        });
            }
        });
    }


    private boolean validateForm(String email) {
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.email_is_required));
            return false;
        } else if (!email.contains("@")) {
            inputEmail.setError(getString(R.string.enter_registration_email));
            return false;
        } else
            inputEmail.setError(null);

        return true;
    }

}