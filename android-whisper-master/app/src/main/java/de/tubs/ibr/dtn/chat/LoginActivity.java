package de.tubs.ibr.dtn.chat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends Activity implements View.OnClickListener{

    SignInButton googleButton;
    Button emailButton;
    private FirebaseUser user;
    FirebaseAuth mAuth;
    private static int RC_SIGN_IN = 2;
    GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        googleButton = (SignInButton) findViewById(R.id.googleSignInButton);
        emailButton = (Button) findViewById(R.id.emailButton);
        emailButton.setBackgroundColor(Color.RED);
        emailButton.setTextColor(Color.WHITE);
        emailButton.setOnClickListener(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        googleButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();


        // Hide the action and status bar.
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        // Change text of google sign in button
        for (int i = 0; i < googleButton.getChildCount(); i++) {
            View v = googleButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText("Sign in with Google");
            }
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);




    }

    @Override
    public void onClick(View view) {
        if(view == emailButton) {
            if (user != null) {
                // user is already created in database
                // Load signIn activity
                Intent intentSignIn = new Intent(LoginActivity.this, Cloud_SignInActivity.class);
                startActivity(intentSignIn);
            } else {
                // user is not created in database
                // call sign up activity
                Intent intentSignUp = new Intent(LoginActivity.this, Cloud_SignUpActivity.class);
                startActivity(intentSignUp);
            }
        }


        if(view == googleButton) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if(account!=null) {
                // Already sign in with google. Go to maps activity.
                Intent mergeIntent = new Intent(LoginActivity.this,MergeActivity.class);
                startActivity(mergeIntent);
            }

            else {
                // Sign In with google.
                signIn();
            }

        }
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully.
            // Authenticate google account
            firebaseAuthWithGoogle(account);


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("LoginActivity", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginActivity.this, "Something went wrong! Try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("loginActivity", "signInWithCredential:success");
                            Intent mergeIntent = new Intent(LoginActivity.this,MergeActivity.class);
                            startActivity(mergeIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("loginActivity", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}
