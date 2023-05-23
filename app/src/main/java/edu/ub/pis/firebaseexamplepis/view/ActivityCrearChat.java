package edu.ub.pis.firebaseexamplepis.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.viewmodel.HomeEventsActivityViewModel;

public class ActivityCrearChat extends AppCompatActivity {
    private static final String TAG = "UpdatePersonalInfoActivity";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText mTxt;
    private Button mCrearChatBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_chat);

        mTxt = findViewById(R.id.username_txt);
        mCrearChatBtn = findViewById(R.id.create_chat_button);

        mCrearChatBtn.setOnClickListener(view ->{

        });
    }


}
