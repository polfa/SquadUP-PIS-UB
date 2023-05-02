package edu.ub.pis.firebaseexamplepis.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.model.EventRepository;
import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.model.UserRepository;
import edu.ub.pis.firebaseexamplepis.view.imageURLs.VideogameLogos;

public class CrearEventActivity extends AppCompatActivity {
    private static final String TAG = "UpdatePersonalInfoActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private Spinner mGameRankSpinner;
    private EditText mDescription;
    private Spinner mMaxMembersSpinner;
    private EditText mStartTime;

    private Button mUpdateButton;

    private Button mSetGameButton;

    private Spinner mGameNameSpinner;


    AutoCompleteTextView autoCompleteTxt;

    ArrayAdapter<String> adapterItems;

    private String[] listGames = {"Rocket_League", "Valorant", "CSGO"};

    private String[] listRanks = {};

    private int max_members = 10;

    private String[] listMaxMembers = new String[max_members];


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_events);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        /*

        autoCompleteTxt = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(this,R.layout.activity_crear_events, items);
        autoCompleteTxt.setAdapter(adapterItems);

        autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(),"Item: "+ item, Toast.LENGTH_SHORT).show();
            }
        });

         */

        mGameNameSpinner = findViewById(R.id.game_name_spinner);
        ArrayAdapter<String> adapterGameNames = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, listGames);
        mGameNameSpinner.setAdapter(adapterGameNames);

        mGameRankSpinner = findViewById(R.id.game_rank_spinner);
        mGameRankSpinner.setAdapter(getGameAdapter());

        mDescription = findViewById(R.id.description_txt);

        for (int i = 0; i < max_members; i++){
            listMaxMembers[i] = String.valueOf(i);
        }
        mMaxMembersSpinner = findViewById(R.id.max_members_spinner);
        ArrayAdapter<String> adapterMaxMembers = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, listMaxMembers);
        mMaxMembersSpinner.setAdapter(adapterMaxMembers);

        mStartTime = findViewById(R.id.date_txt);
        mUpdateButton = findViewById(R.id.create_event_button);
        mSetGameButton = findViewById(R.id.set_game_button);

        UserRepository userRepository = UserRepository.getInstance();
        EventRepository eventRepository = EventRepository.getInstance();
        User currentUser = userRepository.getUserById(mAuth.getCurrentUser().getEmail());

        mSetGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGameRankSpinner.setAdapter(getGameAdapter());
            }
        });
        // Update after clicking on button
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateString = mStartTime.getText().toString();
                Date date = new Date();
                eventRepository.addEvent(currentUser.getID(),mDescription.getText().toString(), mGameNameSpinner.getSelectedItem().toString().toUpperCase(), mGameRankSpinner.getSelectedItem().toString(), date, Integer.parseInt(mMaxMembersSpinner.getSelectedItem().toString()));
                Intent intent = new Intent(CrearEventActivity.this, HomeEventsActivity.class);
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


}