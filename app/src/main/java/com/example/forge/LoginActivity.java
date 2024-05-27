package com.example.forge;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.forge.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private String userRole = "Athlete";

    @Override
    protected void onStart(){
        super.onStart();
        if(auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EditText et_email = findViewById(R.id.login_email);
        EditText et_password = findViewById(R.id.login_password);
        Button loginButton = findViewById(R.id.logIn);
        ToggleButton toggleButton = findViewById(R.id.toggleButton);
        Button signupButton = findViewById(R.id.signLink);

        auth = FirebaseAuth.getInstance();

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userRole = "Coach";
                } else {
                    userRole = "Athlete";
                }
            }
        });


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                if (TextUtils.isEmpty(email)){
                    et_email.setError("Email is required");
                    et_email.requestFocus();
                } else if (TextUtils.isEmpty(password)){
                    et_password.setError("Password is required");
                    et_password.requestFocus();
                } else {
                    loginUser(email, password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    if(firebaseUser.isEmailVerified()){
                        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("UserRole", userRole);
                        editor.apply();

                        finishPreviousActivities(LoginActivity.this);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else if (email.equals("sictst1@gmail.com") || email.equals("sictst2@gmail.com")
                            || email.equals("sictst3@gmail.com") || email.equals("sictst4@gmail.com")){
                        SharedPreferences preferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("UserRole", userRole);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Email Is Not Verified. Login in 'Test Mode'.", Toast.LENGTH_SHORT).show();
                        finishPreviousActivities(LoginActivity.this);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Dialog dialog = new Dialog(LoginActivity.this);
                        dialog.showDialog("Email Is Not Verified", "Please check your email and click on the verification link to complete the registration process.");
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect email or password. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void finishPreviousActivities(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();
        for (ActivityManager.AppTask task : tasks) {
            task.finishAndRemoveTask();
        }
    }
}
