package edu.ub.pis.firebaseexamplepis.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.model.EventRepository;
import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.model.UserRepository;

public class CrearEventActivity extends AppCompatActivity {
    private static final String TAG = "UpdatePersonalInfoActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    private EditText mGameName;
    private EditText mGameRank;
    private EditText mDescription;
    private EditText mMaxMembers;
    private EditText mStartTime;

    private Button mUpdateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_events);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        mGameName = (EditText) findViewById(R.id.game_name_txt);
        mGameRank = (EditText) findViewById(R.id.game_rank_txt);
        mDescription = (EditText) findViewById(R.id.description_txt);
        mMaxMembers = findViewById(R.id.max_members);
        mStartTime = findViewById(R.id.date_txt);
        mUpdateButton = (Button) findViewById(R.id.create_event_button);

        UserRepository userRepository = UserRepository.getInstance();
        EventRepository eventRepository = EventRepository.getInstance();
        User currentUser = userRepository.getUserById(mAuth.getCurrentUser().getEmail());


        // Update after clicking on button
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateString = mStartTime.getText().toString();
                Date date = new Date();
                eventRepository.addEvent(currentUser.getID(),mDescription.getText().toString(),mGameName.getText().toString(),mGameRank.getText().toString(), date, Integer.parseInt(mMaxMembers.getText().toString()));
                Intent intent = new Intent(CrearEventActivity.this, HomeEventsActivity.class);
                startActivity(intent);
            }
        });
    }


}