package com.example.forge;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText et_username, et_email, et_password1, et_password2;
    private static final String TAG = "SignUpActivity";
    private FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button loginButton = findViewById(R.id.loginLink);
        Button signupButton = findViewById(R.id.signUp);
        et_username = findViewById(R.id.signup_username);
        et_email = findViewById(R.id.signup_email);
        et_password1 = findViewById(R.id.signup_password);
        et_password2 = findViewById(R.id.signup_password_conf);

        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = et_username.getText().toString();
                String email = et_email.getText().toString();
                String password1 = et_password1.getText().toString();
                String password2 = et_password2.getText().toString();

                if (TextUtils.isEmpty(username)){
                    et_username.setError("Username is required");
                    et_username.requestFocus();
                } else if (TextUtils.isEmpty(email)){
                    et_email.setError("Email is required");
                    et_email.requestFocus();
                } else  if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    et_email.setError("Valid email is required");
                    et_email.requestFocus();
                } else if (password1.length()<6){
                    et_password1.setError("Password must be >5 characters");
                    et_password1.requestFocus();
                } else if (!password1.equals(password2)) {
                    et_password2.setError("The Password Doesn't Match");
                    et_password2.requestFocus();
                } else {
                    registerUser(username, email, password1);
                }
            }
        });
    }

    private void registerUser(String username, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();

                            database = FirebaseDatabase.getInstance();
                            reference = database.getReference("Registered Users");

                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(username, email);
                            reference.child(username).setValue(writeUserDetails);

                            user.sendEmailVerification();

                            Dialog dialog = new Dialog(SignUpActivity.this);
                            dialog.showDialog("Verify Your Email", "Please check your email and click on the verification link to complete the registration process.");
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e){
                                et_email.setError("Email address already in use. Please use a different email.");
                                et_email.requestFocus();
                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
