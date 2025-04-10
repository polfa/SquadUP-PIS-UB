package edu.ub.pis.firebaseexamplepis.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.viewmodel.ChatActivityViewModel;

public class CrearChatActivity extends AppCompatActivity {
    private static final String TAG = "UpdatePersonalInfoActivity";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText mTxt;
    private Button mCrearChatBtn;
    private ImageView mExitButton;

    private ChatActivityViewModel mChatActivityViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_chat);
        mChatActivityViewModel = new ViewModelProvider(this)
                .get(ChatActivityViewModel.class);
        mTxt = findViewById(R.id.username_txt);
        mCrearChatBtn = findViewById(R.id.create_chat_button);
        mExitButton = findViewById(R.id.crear_chat_exit_btn);
        mCrearChatBtn.setOnClickListener(view ->{
            mChatActivityViewModel.addChat(mAuth.getCurrentUser().getEmail(),mTxt.getText().toString());
            Intent intent = new Intent(CrearChatActivity.this, ChatActivity.class);
            startActivity(intent);
        });
        mExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrearChatActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }


}
