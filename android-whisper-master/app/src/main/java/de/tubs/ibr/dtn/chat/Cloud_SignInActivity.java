package de.tubs.ibr.dtn.chat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Cloud_SignInActivity extends Activity implements View.OnClickListener {

    private Button buttonSignin;
    private EditText editTextEmailSignIn;
    private EditText editTextPasswordSignIn;
    private static final String TAG = "Cloud_SignInActivity";
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloud_signin);
        Log.d(TAG, "Inside SignIn activity");
        buttonSignin  = (Button) findViewById(R.id.buttonSignIn);
        editTextEmailSignIn = (EditText) findViewById(R.id.editTextEmailSignIn);
        editTextPasswordSignIn = (EditText) findViewById(R.id.editTextPasswordSignIn);

        auth = FirebaseAuth.getInstance();

        buttonSignin.setOnClickListener(this);
    }


    private void loginUser() {
        Log.d(TAG, "Inside loginUser() in SignIn activity");
        String email = editTextEmailSignIn.getText().toString().trim();
        String password = editTextPasswordSignIn.getText().toString().trim();

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
        // Now let user login to his account.
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Cloud_SignInActivity.this, "User Login successful", Toast.LENGTH_SHORT).show();

                            SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("hasLoggedIn", true);
                            editor.commit();

                            Intent MergeIntent = new Intent(Cloud_SignInActivity.this, MergeActivity.class);
                            startActivity(MergeIntent);

                        }
                        else {
                            Toast.makeText(Cloud_SignInActivity.this, "Could not login. Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if(view == buttonSignin) {
            loginUser();
        }
    }
}
