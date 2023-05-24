package edu.ub.pis.firebaseexamplepis.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.view.imageURLs.VideogameLogos;

public class UpdateInfoActivity extends AppCompatActivity {
    private static final String TAG = "UpdatePersonalInfoActivity";
    private String[] listGames = {"Rocket_League", "Valorant", "CSGO"};
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private EditText mPictureText;
    private EditText mNicknameText;
    private EditText mDescriptionText;
    private Spinner mGameRankSpinner;
    private Button mSetGameButton;
    private ImageView mExtitButton;
    private Spinner mGameNameSpinner;
    private Button mUpdateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        mExtitButton = findViewById(R.id.update_exit_btn);
        mNicknameText = findViewById(R.id.updateNicknameTxt);
        mDescriptionText = (EditText) findViewById(R.id.updateDescriptionTxt);
        mPictureText = findViewById(R.id.updatePhotoTxt);
        mUpdateButton = (Button) findViewById(R.id.updateButton);
        mGameNameSpinner = findViewById(R.id.update_game_spinner);
        ArrayAdapter<String> adapterGameNames = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, listGames);
        mGameNameSpinner.setAdapter(adapterGameNames);
        mGameRankSpinner = findViewById(R.id.update_rank_spinner);
        mGameRankSpinner.setAdapter(getGameAdapter());
        mSetGameButton = findViewById(R.id.update_game_button);
        mSetGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGameRankSpinner.setAdapter(getGameAdapter());
            }
        });
        mUpdateButton.setVisibility(View.INVISIBLE);

        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = mDb.collection("users").document(user.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mNicknameText.setText(document.get("nickname").toString());
                        mDescriptionText.setText(document.get("descripcio").toString());
                        mPictureText.setText(document.get("picture_url").toString());
                        mUpdateButton.setVisibility(View.VISIBLE);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        // Update after clicking on button
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCompletion(user.getEmail());
                Intent intent = new Intent(UpdateInfoActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        mExtitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateInfoActivity.this, HomeActivity.class);
                startActivity(intent);
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

    protected void updateCompletion(String email) {
        // Obtenir informació personal de l'usuari
        Map<String, Object> signedUpUser = new HashMap<>();
        if(!mDescriptionText.getText().toString().isEmpty()){
            signedUpUser.put("descripcio", mDescriptionText.getText().toString());
        }
        if(!mNicknameText.getText().toString().isEmpty()){
            signedUpUser.put("nickname", mNicknameText.getText().toString());
        }
        signedUpUser.put("picture_url",mPictureText.getText().toString());
        signedUpUser.put("gameImageId",mGameNameSpinner.getSelectedItem().toString().toUpperCase());
        signedUpUser.put("rankImageId",mGameRankSpinner.getSelectedItem().toString().toLowerCase());


        // Actualitzar-la a la base de dades
        mDb.collection("users").document(email).update(signedUpUser);
    }
}