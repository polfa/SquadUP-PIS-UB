package edu.ub.pis.firebaseexamplepis.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.model.UserRepository;

public class SignUpActivity extends AppCompatActivity {
    private final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;

    private EditText mEmailText;
    private EditText mPasswordText;

    private EditText mFirstNameText;
    private EditText mLastNameText;
    private EditText mHobbiesText;

    private Button mSignUpButton;

    private UserRepository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mRepository = UserRepository.getInstance();

        mEmailText = (EditText) findViewById(R.id.signUpEmailText);
        mPasswordText = (EditText) findViewById(R.id.signUpPasswordText);

        mFirstNameText = (EditText) findViewById(R.id.updateFirstNameText);
        mLastNameText = (EditText) findViewById(R.id.updateLastNameText);
        mHobbiesText = (EditText) findViewById(R.id.updateHobbiesText);

        mSignUpButton = (Button) findViewById(R.id.updateButton);
        mSignUpButton.setOnClickListener(view -> {
            if (!mEmailText.getText().toString().isEmpty() && !mPasswordText.getText().toString().isEmpty()) {
                signUp(mEmailText.getText().toString(), mPasswordText.getText().toString());
            }else if (mEmailText.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Email is required. Please enter your email to continue.",
                        Toast.LENGTH_SHORT).show();
            }else if (mPasswordText.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Password is required. Please enter your password to continue.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful() && !mFirstNameText.getText().toString().isEmpty() && !mLastNameText.getText().toString().isEmpty()) {
                            String txtHobbies;
                            if (!mHobbiesText.getText().toString().isEmpty()) {
                                txtHobbies = mHobbiesText.getText().toString();
                            }else{
                                txtHobbies = "";
                            }
                            mRepository.addUser(
                                email,
                                mFirstNameText.getText().toString(),
                                mLastNameText.getText().toString(),
                                    txtHobbies

                            );
                            // Anar a la pantalla home de l'usuari autenticat
                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "Sign up create user succeeded");
                             if (mFirstNameText.getText().toString().isEmpty()){
                                Toast.makeText(getApplicationContext(), "First name is required. Please enter your first name to continue.",
                                        Toast.LENGTH_SHORT).show();
                             }else if (mLastNameText.getText().toString().isEmpty()) {
                                 Toast.makeText(getApplicationContext(), "Last name is required. Please enter your last name to continue.",
                                         Toast.LENGTH_SHORT).show();
                             }
                        }
                    }
                });
    }

}