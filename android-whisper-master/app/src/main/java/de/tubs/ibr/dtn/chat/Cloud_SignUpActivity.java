package de.tubs.ibr.dtn.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Cloud_SignUpActivity extends Activity implements View.OnClickListener {

    private Button buttonSignup;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView TextViewSignIn;

    private FirebaseAuth firebaseAuth;
    private static final String TAG = "Cloud_SignUpActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloud_signup);
        Log.d(TAG, "Inside SignUp activity");
        buttonSignup  = (Button) findViewById(R.id.buttonSignUp);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        TextViewSignIn = (TextView) findViewById(R.id.TextViewSignin);

        // Initialize firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();

        // Add listener to button and textview
        buttonSignup.setOnClickListener(this);
        TextViewSignIn.setOnClickListener(this);
    }

    private void registerUser() {
        Log.d(TAG, "Inside registerUser() in SignUp activity");
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            // email empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            // Stop function from executing further
            return;
        }

        if(TextUtils.isEmpty(password)) {
            //password empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            // stop function from executing further
            return;
        }
        Task<AuthResult> task;
        // we are here means all fields are appropriate
        // create new user (details will be stored in cloud)
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // user is successfully registered
                            Toast.makeText(Cloud_SignUpActivity.this, "User registration successful", Toast.LENGTH_SHORT).show();
                            // Here start SignIn activity.
                            Log.d(TAG, "signIn activity called from SignUp activity");
                            Intent intentSignIn = new Intent(Cloud_SignUpActivity.this, Cloud_SignInActivity.class);
                            startActivity(intentSignIn);
                        }
                        else {
                            Toast.makeText(Cloud_SignUpActivity.this, "Could not register. Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if(view == buttonSignup) {
            registerUser();
        }
        if(view == TextViewSignIn) {
            // Load LogInActivity
            Intent intent = new Intent(Cloud_SignUpActivity.this, Cloud_SignInActivity.class);
            startActivity(intent);
        }
    }
}