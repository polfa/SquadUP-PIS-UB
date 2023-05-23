package edu.ub.pis.firebaseexamplepis.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.model.Chat;
import edu.ub.pis.firebaseexamplepis.viewmodel.ActiveData;
import edu.ub.pis.firebaseexamplepis.viewmodel.ChatActivityViewModel;
import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.model.Message;

public class ChatInsideActivity extends AppCompatActivity {
    private final String TAG = "ChatInsideActivity";

    /**
     * ViewModel del ChatActivity
     */
    private ChatActivityViewModel mChatActivityViewModel; //nuestro viewModel

    /* Elements de la vista de la ChatActivity */
    private ImageView mExitButton;

    private TextView mUsernameTxt;
    private ImageView mUserPicture;

    private Button mSendButton;
    private EditText mTypeTxt; // [Exercici 2: crea aquest botó al layout i implementa
    // correctament setChoosePictureListener()]
    private Button mLogoutButton;
    private ViewGroup loggedLayout;
    private RecyclerView mChatCardsRV; // RecyclerView

    /**
     * Adapter de la RecyclerView
     */
    private ChatInsideCardAdapter mChatCardRVAdapter;

    /**
     * Autenticació de Firebase
     */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Foto de perfil de l'usuari
     */
    private Uri mPhotoUri;

    private ActiveData activeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activeData = ActiveData.getInstance();
        setContentView(R.layout.activity_chat_inside);
        mExitButton = findViewById(R.id.exit_btn);
        mUsernameTxt = findViewById(R.id.user_name_txt);
        mUserPicture = findViewById(R.id.user_image);
        mSendButton = findViewById(R.id.buttonSend);
        mTypeTxt = findViewById(R.id.type_message_txt);

        // Inicialitza el ViewModel d'aquesta activity (ChatActivity)
        mChatActivityViewModel = new ViewModelProvider(this)
                .get(ChatActivityViewModel.class);

        // Elements del ViewGroup inferior (email, botó logout, etc),
        // que només mostrarem si hi ha usuari logat.

        if (mAuth.getCurrentUser() != null) {  // Si hi ha usuari logat...
            // Obté elements de loggedLayout

            // Mostrar usuari logat

            // Defineix listeners
            mExitButton.setOnClickListener(view -> {
                mAuth.signOut();
                activeData.setCurrentChat(null);
                Intent intent = new Intent(ChatInsideActivity.this, ChatActivity.class);
                startActivity(intent);
            });

            // Anem a buscar la RecyclerView i fem dues coses:
            mChatCardsRV = findViewById(R.id.recyclerViewChat);

            // (1) Li assignem un layout manager.
            LinearLayoutManager manager = new LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL, false
            );
            mChatCardsRV.setLayoutManager(manager);


            // (2) Inicialitza el RecyclerViewAdapter i li assignem a la RecyclerView.
            mChatCardRVAdapter = new ChatInsideCardAdapter(
                    activeData.getCurrentChat().getMessages(), mChatActivityViewModel // Passem-li referencia llista usuaris
            );
            mChatCardsRV.setAdapter(mChatCardRVAdapter); // Associa l'adapter amb la ReciclerView

            final Observer<ArrayList<Chat>> observerChats = new Observer<ArrayList<Chat>>() {
                @Override
                public void onChanged(ArrayList<Chat> chats) {
                    mChatCardRVAdapter.notifyDataSetChanged();
                }
            };
            mChatActivityViewModel.getChats().observe(this, observerChats);

            mSendButton.setOnClickListener(view -> {
                String text = mTypeTxt.getText().toString();
                activeData.getCurrentChat().addMessage(mAuth.getCurrentUser().getEmail(),text);
                mChatActivityViewModel.updateChat(activeData.getCurrentChat());
                mChatCardRVAdapter.updateMessages(activeData.getCurrentChat().getMessages());
                mChatCardRVAdapter.notifyDataSetChanged();
            });




        }
    }
}
