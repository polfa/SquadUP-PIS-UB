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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.viewmodel.HomeActivityViewModel;
import edu.ub.pis.firebaseexamplepis.model.User;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = "HomeActivity";

    /**
     * ViewModel del HomeActivity
     */
    private HomeActivityViewModel mHomeActivityViewModel; //nuestro viewModel

    /* Elements de la vista de la HomeActivity */
    private ImageView mModifyPersonalInfoButton;
    private TextView grupBtn;
    private TextView eventBtn;
    private ImageView mChatBtn;
    private ImageView mLoggedPictureImageView;
    private ImageButton mTakePictureButton;
    private ImageButton mChoosePictureButton; // [Exercici 2: crea aquest botó al layout i implementa
    // correctament setChoosePictureListener()]
    private Button mLogoutButton;
    private ViewGroup loggedLayout;
    private RecyclerView mUserCardsRV; // RecyclerView a

    /**
     * Adapter de la RecyclerView
     */
    private UserCardAdapter mUserCardRVAdapter;

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
        setContentView(R.layout.activity_home);
        eventBtn = findViewById(R.id.eventBtn);
        mChatBtn = findViewById(R.id.messages_btn);
        grupBtn = findViewById(R.id.groupsBtn);

        // Inicialitza el ViewModel d'aquesta activity (HomeActivity)
        mHomeActivityViewModel = new ViewModelProvider(this)
                .get(HomeActivityViewModel.class);

        // Elements del ViewGroup inferior (email, botó logout, etc),
        // que només mostrarem si hi ha usuari logat.
        loggedLayout = findViewById(R.id.loggedLayout);

        if (mAuth.getCurrentUser() != null) {  // Si hi ha usuari logat...
            // Obté elements de loggedLayout
            mModifyPersonalInfoButton = findViewById(R.id.modifyPersonalInfoButton);
            mLoggedPictureImageView = findViewById(R.id.loggedPictureImageView);
            mLogoutButton = findViewById(R.id.logoutButton);

            // Mostrar usuari logat

            // Defineix listeners
            mModifyPersonalInfoButton.setOnClickListener(view -> {
                Intent intent = new Intent(HomeActivity.this, UpdateInfoActivity.class);
                startActivity(intent);
            });
            mLogoutButton.setOnClickListener(view -> {
                mAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, AuthenticationActivity.class);
                startActivity(intent);
            });
            eventBtn.setOnClickListener(view -> {
                Intent intent = new Intent(HomeActivity.this, HomeEventsActivity.class);
                startActivity(intent);
            });

            mChatBtn.setOnClickListener(view -> {
                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                startActivity(intent);
            });

            grupBtn.setOnClickListener(view -> {
                Intent intent = new Intent(HomeActivity.this, GrupActivity.class);
                startActivity(intent);
            });

        } else { // Si no ho està, ...
            //
            loggedLayout.setVisibility(View.GONE); // No mostris cap element del layout inferior
        }

        // Anem a buscar la RecyclerView i fem dues coses:
        mUserCardsRV = findViewById(R.id.userCardRv);

        // (1) Li assignem un layout manager.
        LinearLayoutManager manager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        );
        mUserCardsRV.setLayoutManager(manager);

        // (2) Inicialitza el RecyclerViewAdapter i li assignem a la RecyclerView.
        mUserCardRVAdapter = new UserCardAdapter(
                mHomeActivityViewModel.getUsers().getValue() // Passem-li referencia llista usuaris
        );
        mUserCardRVAdapter.setOnClickHideListener(new UserCardAdapter.OnClickHideListener() {
            // Listener que escoltarà quan interactuem amb un item en una posició donada
            // dins de la recicler view. En aquest cas, quan es faci clic al botó d'amagar
            // l'usuari.
            @Override
            public void OnClickHide(int position) {
                mHomeActivityViewModel.removeUserFromHome(position);
                mUserCardRVAdapter.hideUser(position);
            }
        });
        mUserCardsRV.setAdapter(mUserCardRVAdapter); // Associa l'adapter amb la ReciclerView

        // Observer a HomeActivity per veure si la llista de User (observable MutableLiveData)
        // a HomeActivityViewModel ha canviat.
        final Observer<ArrayList<User>> observerUsers = new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                mUserCardRVAdapter.notifyDataSetChanged();
            }
        };
        mHomeActivityViewModel.getUsers().observe(this, observerUsers);

        // A partir d'aquí, en cas que es faci cap canvi a la llista d'usuaris, HomeActivity ho sabrá
        mHomeActivityViewModel.loadUsersFromRepository();  // Internament pobla els usuaris de la BBDD

        // Si hi ha usuari logat i seteja una foto de perfil, mostra-la.
        if (mAuth.getCurrentUser() != null) {
            final Observer<String> observerPictureUrl = new Observer<String>() {
                @Override
                public void onChanged(String pictureUrl) {
                    Picasso.get()
                            .load(pictureUrl)
                            .resize(mLoggedPictureImageView.getWidth(), mLoggedPictureImageView.getHeight())
                            .centerCrop()
                            .into(mLoggedPictureImageView);
                }
            };
            mHomeActivityViewModel.getPictureUrl().observe(this, observerPictureUrl);
        }
    }
}

