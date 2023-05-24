package edu.ub.pis.firebaseexamplepis.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.model.Chat;
import edu.ub.pis.firebaseexamplepis.viewmodel.ActiveData;
import edu.ub.pis.firebaseexamplepis.viewmodel.ChatActivityViewModel;

public class ChatActivity extends AppCompatActivity {
    private final String TAG = "ChatActivity";

    /**
     * ViewModel del HomeActivity
     */
    private ChatActivityViewModel mChatActivityViewModel; //nuestro viewModel

    /* Elements de la vista de la HomeActivity */
    private ImageView mModifyPersonalInfoButton;
    private ImageView mLoggedPictureImageView;
    private ImageButton mTakePictureButton;
    private ImageButton mChoosePictureButton; // [Exercici 2: crea aquest botó al layout i implementa
    private ViewGroup loggedLayout;          // correctament setChoosePictureListener()]

    private ImageView mHomeButton;
    private Button mLogoutButton;
    private RecyclerView mChatCardsRV; // RecyclerView

    private Button mNewChatBtn;


    /**
     * Adapter de la RecyclerView
     */
    private ChatCardAdapter mChatCardRVAdapter;

    /**
     * Autenticació de Firebase
     */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * Foto de perfil de l'usuari
     */
    private Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Inicialitza el ViewModel d'aquesta activity (activity_chat)
        mChatActivityViewModel = new ViewModelProvider(this)
                .get(ChatActivityViewModel.class);

        // Elements del ViewGroup inferior (email, botó logout, etc),
        // que només mostrarem si hi ha usuari logat.
        mHomeButton = findViewById(R.id.home_btn);
        loggedLayout = findViewById(R.id.loggedLayout);
        mNewChatBtn = findViewById(R.id.new_chat_btn);

        if (mAuth.getCurrentUser() != null) {  // Si hi ha usuari logat...
            // Obté elements de loggedLayout
            mModifyPersonalInfoButton = findViewById(R.id.modifyPersonalInfoButton);
            mLoggedPictureImageView = findViewById(R.id.loggedPictureImageView);
            mLogoutButton = findViewById(R.id.logoutButton);

            // Mostrar usuari logat

            // Defineix listeners
            mModifyPersonalInfoButton.setOnClickListener(view -> {
                Intent intent = new Intent(ChatActivity.this, UpdateInfoActivity.class);
                startActivity(intent);
            });
            mLogoutButton.setOnClickListener(view -> {
                mAuth.signOut();
                Intent intent = new Intent(ChatActivity.this, AuthenticationActivity.class);
                startActivity(intent);
            });

            mHomeButton.setOnClickListener(view -> {
                Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
                startActivity(intent);
            });

            mNewChatBtn.setOnClickListener(view -> {
                Intent intent = new Intent(ChatActivity.this, CrearChatActivity.class);
                startActivity(intent);
            });

        } else { // Si no ho està, ...
            //
            loggedLayout.setVisibility(View.GONE); // No mostris cap element del layout inferior
        }

        // Anem a buscar la RecyclerView i fem dues coses:
        mChatCardsRV = findViewById(R.id.chatCardRv);

        // (1) Li assignem un layout manager.
        LinearLayoutManager manager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        );
        mChatCardsRV.setLayoutManager(manager);

        // (2) Inicialitza el RecyclerViewAdapter i li assignem a la RecyclerView.
        mChatCardRVAdapter = new ChatCardAdapter(
                mChatActivityViewModel.getChats().getValue(), mChatActivityViewModel // Passem-li referencia llista usuaris
        );
        mChatCardsRV.setAdapter(mChatCardRVAdapter); // Associa l'adapter amb la ReciclerView
        mChatCardRVAdapter.setOnClickEnterListener(new ChatCardAdapter.OnClickEnterListener() {
            // Listener que escoltarà quan interactuem amb un item en una posició donada
            // dins de la recicler view. En aquest cas, quan es faci clic al botó d'amagar
            // l'usuari.
            @Override
            public void OnClickEnter(int position, Chat chat) {
                ActiveData.getInstance().setCurrentChat(chat);
                Intent intent = new Intent(ChatActivity.this, ChatInsideActivity.class);
                startActivity(intent);
            }
        });


        // Observer a HomeActivity per veure si la llista de Event (observable MutableLiveData)
        // a HomeActivityViewModel ha canviat.
        final Observer<ArrayList<Chat>> observerChats = new Observer<ArrayList<Chat>>() {
            @Override
            public void onChanged(ArrayList<Chat> chats) {
                mChatCardRVAdapter.notifyDataSetChanged();
            }
        };
        mChatActivityViewModel.getChats().observe(this, observerChats);

        // A partir d'aquí, en cas que es faci cap canvi a la llista d'usuaris, HomeActivity ho sabrá
        mChatActivityViewModel.loadChatsFromRepository(mAuth.getCurrentUser().getEmail());  // Internament pobla els usuaris de la BBDD
    }
}
