package edu.ub.pis.firebaseexamplepis.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class UpdateInfoActivity extends AppCompatActivity {
    private static final String TAG = "UpdatePersonalInfoActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    private EditText mFirstNameText;
    private EditText mLastNameText;
    private EditText mHobbiesText;

    private Button mUpdateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        mFirstNameText = (EditText) findViewById(R.id.updateFirstNameText);
        mLastNameText = (EditText) findViewById(R.id.updateLastNameText);
        mHobbiesText = (EditText) findViewById(R.id.updateHobbiesText);
        mUpdateButton = (Button) findViewById(R.id.updateButton);

        mUpdateButton.setVisibility(View.INVISIBLE);

        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = mDb.collection("users").document(user.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mFirstNameText.setText(document.get("first").toString());
                        mLastNameText.setText(document.get("last").toString());
                        mFirstNameText.setEnabled(false);
                        mLastNameText.setEnabled(false);
                        mHobbiesText.setText(document.get("hobbies").toString());
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
    }

    protected void updateCompletion(String email) {
        // Obtenir informació personal de l'usuari
        Map<String, Object> signedUpUser = new HashMap<>();
        signedUpUser.put("hobbies", mHobbiesText.getText().toString());

        // Actualitzar-la a la base de dades
        mDb.collection("users").document(email).update(signedUpUser);
    }
}