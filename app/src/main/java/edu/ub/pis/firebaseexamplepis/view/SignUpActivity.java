package edu.ub.pis.firebaseexamplepis.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.model.UserRepository;
import edu.ub.pis.firebaseexamplepis.view.imageURLs.VideogameLogos;

public class SignUpActivity extends AppCompatActivity {
    private final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;

    private EditText mEmailText;
    private EditText mPasswordText;
    private EditText mNicknameText;
    private EditText mDescripcioText;
    private Spinner mGameRankSpinner;
    private Spinner mGameNameSpinner;
    private String[] listGames = {"Rocket_League", "Valorant", "CSGO"};

    private String[] listRanks = {};
    private Button mSignUpButton;
    private Button mSetGameButton;
    private UserRepository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mRepository = UserRepository.getInstance();

        mEmailText = (EditText) findViewById(R.id.signUpEmailText);
        mPasswordText = (EditText) findViewById(R.id.signUpPasswordText);
        mNicknameText = (EditText) findViewById(R.id.updateNicknameText);
        mDescripcioText = (EditText) findViewById(R.id.updateDescriptionText);
        mGameNameSpinner = findViewById(R.id.game_name_spinner2);
        ArrayAdapter<String> adapterGameNames = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, listGames);
        mGameNameSpinner.setAdapter(adapterGameNames);
        mGameRankSpinner = findViewById(R.id.game_rank_spinner2);
        mGameRankSpinner.setAdapter(getGameAdapter());
        mSignUpButton = findViewById(R.id.updateButton);
        mSetGameButton = findViewById(R.id.set_game_button3);
        mSetGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGameRankSpinner.setAdapter(getGameAdapter());
            }
        });
        mSignUpButton.setOnClickListener(view -> {
            if (!mEmailText.getText().toString().isEmpty() && !mPasswordText.getText().toString().isEmpty()) {
                signUp(mEmailText.getText().toString(), mPasswordText.getText().toString());
            }else if (mEmailText.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Email is required. Please enter your email to continue.",
                        Toast.LENGTH_SHORT).show();
            }else if (mPasswordText.getText().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Password is required. Please enter your password to continue.",
                        Toast.LENGTH_SHORT).show();
            }else if (mGameNameSpinner.getSelectedItem().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Favourite game is required. Please enter your favourite game to continue.",
                        Toast.LENGTH_SHORT).show();
            }else if (mGameRankSpinner.getSelectedItem().toString().isEmpty()){
                Toast.makeText(getApplicationContext(), "Game rank is required. Please enter your rank to continue.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayAdapter<String> getGameAdapter() {
        for (String s: listGames){
            if (mGameNameSpinner.getSelectedItem().toString().equalsIgnoreCase(s)) {
                ArrayAdapter<String> adapterGameRanks = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, VideogameLogos.valueOf(s.toUpperCase()).getRankNames());
                return adapterGameRanks;
            }
        }
        return null;
    }

    protected void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful() && !mNicknameText.getText().toString().isEmpty()) {
                            String txtDescripcio;
                            if (!mDescripcioText.getText().toString().isEmpty()) {
                                txtDescripcio = mDescripcioText.getText().toString();
                            }else{
                                txtDescripcio = "";
                            }
                            mRepository.addUser(
                                email,
                                mNicknameText.getText().toString(),
                                mDescripcioText.getText().toString(),
                                mGameNameSpinner.getSelectedItem().toString().toUpperCase(),
                                mGameRankSpinner.getSelectedItem().toString().toLowerCase(),
                                    new ArrayList<String>()

                            );
                            // Anar a la pantalla home de l'usuari autenticat
                            Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d(TAG, "Sign up create user succeeded");
                             if (mNicknameText.getText().toString().isEmpty()){
                                Toast.makeText(getApplicationContext(), "First name is required. Please enter your first name to continue.",
                                        Toast.LENGTH_SHORT).show();
                             }
                        }
                    }
                });
    }

}