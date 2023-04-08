package edu.ub.pis.firebaseexamplepis.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.ub.pis.firebaseexamplepis.R;

public class AuthenticationActivity extends AppCompatActivity {
    /* Elements de la vista de AuthenticationActivity */
    private EditText mEmailText;
    private EditText mPasswordText;
    private Button mLoginButton;
    private TextView mSignUpText;
    private TextView mSkipSignInText;

    /* Mòdul autenticació de Firebase */
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mEmailText = findViewById(R.id.emailText);
        mPasswordText = findViewById(R.id.passwordText);
        mLoginButton = findViewById(R.id.loginButton);
        mSignUpText = findViewById(R.id.signUpText);
        mSkipSignInText = findViewById(R.id.skipSignInText);

        mLoginButton.setOnClickListener(view -> {
            // Prova de fer sign-in (aka login)
            mAuth.signInWithEmailAndPassword(mEmailText.getText().toString(), mPasswordText.getText().toString())
                .addOnCompleteListener(AuthenticationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // Si es pot loguejar, passa a la Home
                            Intent intent = new Intent(AuthenticationActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            // Si falla el logueig, fes un Toast
                            Toast.makeText(getApplicationContext(), "Login failed.",
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        });

        // Obrir activity de sign up
        mSignUpText.setOnClickListener(view -> {
            Intent intent = new Intent(AuthenticationActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Passar al HomeActivity sense loguejar-se
        mSkipSignInText.setOnClickListener(view -> {
            Intent intent = new Intent(AuthenticationActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        // Si encara estessim loguejats, podem anar directament a HomeActivity
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(AuthenticationActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }
}