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
import android.widget.TextView;

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
import edu.ub.pis.firebaseexamplepis.model.Grup;
import edu.ub.pis.firebaseexamplepis.viewmodel.GrupActivityViewModel;

public class GrupActivity extends AppCompatActivity {
    private final String TAG = "HomeActivity";

    /**
     * ViewModel del HomeActivity
     */
    private GrupActivityViewModel mGrupActivityViewModel; //nuestro viewModel

    /* Elements de la vista de la HomeActivity */
    private ImageView mModifyPersonalInfoButton;
    private ImageView mLoggedPictureImageView;
    private ImageButton mTakePictureButton;
    private ImageButton mChoosePictureButton; // [Exercici 2: crea aquest botó al layout i implementa
    // correctament setChoosePictureListener()]
    private Button mLogoutButton;

    private Button mNewGrupButton;

    private TextView mEventButton;
    private ViewGroup loggedLayout;
    private RecyclerView mGrupCardRv; // RecyclerView

    private TextView mFriendsButton;

    private ImageView mChatButton;
    /**
     * Adapter de la RecyclerView
     */
    private GrupCardAdapter mGrupCardRVAdapter;

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
        setContentView(R.layout.activity_home_grups);

        // Inicialitza el ViewModel d'aquesta activity (HomeGrupsActivity)
        mGrupActivityViewModel = new ViewModelProvider(this)
                .get(GrupActivityViewModel.class);

        // Elements del ViewGroup inferior (email, botó logout, etc),
        // que només mostrarem si hi ha usuari logat.
        loggedLayout = findViewById(R.id.loggedLayout);

        if (mAuth.getCurrentUser() != null) {  // Si hi ha usuari logat...
            // Obté elements de loggedLayout
            mModifyPersonalInfoButton = findViewById(R.id.modifyPersonalInfoButton);
            mLoggedPictureImageView = findViewById(R.id.loggedPictureImageView);
            mLogoutButton = findViewById(R.id.logoutButton);
            mFriendsButton = findViewById(R.id.friendsBtn);
            mChatButton = findViewById(R.id.messages_btn);
            mNewGrupButton = findViewById(R.id.newGroupBtn);
            mEventButton = findViewById(R.id.eventBtn);

            // Mostrar usuari logat

            // Defineix listeners
            mModifyPersonalInfoButton.setOnClickListener(view -> {
                Intent intent = new Intent(GrupActivity.this, UpdateInfoActivity.class);
                startActivity(intent);
            });
            mLogoutButton.setOnClickListener(view -> {
                mAuth.signOut();
                Intent intent = new Intent(GrupActivity.this, AuthenticationActivity.class);
                startActivity(intent);
            });

            mFriendsButton.setOnClickListener(view -> {
                Intent intent = new Intent(GrupActivity.this, HomeActivity.class);
                startActivity(intent);
            });

            mChatButton.setOnClickListener(view -> {
                Intent intent = new Intent(GrupActivity.this, ChatActivity.class);
                startActivity(intent);
            });

            mEventButton.setOnClickListener(view -> {
                Intent intent = new Intent(GrupActivity.this, HomeEventsActivity.class);
                startActivity(intent);
            });

            /*mNewGrupButton.setOnClickListener(view -> {
                Intent intent = new Intent(GrupActivity.this, CrearGrupActivity.class);
                startActivity(intent);
            });*/
        } else { // Si no ho està, ...
            //
            loggedLayout.setVisibility(View.GONE); // No mostris cap element del layout inferior
        }

        // Anem a buscar la RecyclerView i fem dues coses:
        mGrupCardRv = findViewById(R.id.grupCardRv);

        // (1) Li assignem un layout manager.
        LinearLayoutManager manager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false
        );
        mGrupCardRv.setLayoutManager(manager);

        // (2) Inicialitza el RecyclerViewAdapter i li assignem a la RecyclerView.
        mGrupCardRVAdapter = new GrupCardAdapter(
                mGrupActivityViewModel.getGrups().getValue(), mGrupActivityViewModel // Passem-li referencia llista usuaris
        );
        /*
        mGrupCardRVAdapter.setOnClickJoinListener(new GrupCardAdapter.OnClickJoinListener() {
            // Listener que escoltarà quan interactuem amb un item en una posició donada
            // dins de la recicler view. En aquest cas, quan es faci clic al botó d'amagar
            // l'usuari.
            @Override
            public void OnClickJoin(int position) {
                mGrupActivityViewModel.removeGrupFromHome(position);
            }
        });*/
        mGrupCardRv.setAdapter(mGrupCardRVAdapter); // Associa l'adapter amb la ReciclerView

        // Observer a HomeActivity per veure si la llista de Grup (observable MutableLiveData)
        // a HomeActivityViewModel ha canviat.
        final Observer<ArrayList<Grup>> observerGrups = new Observer<ArrayList<Grup>>() {
            @Override
            public void onChanged(ArrayList<Grup> Grups) {
                mGrupCardRVAdapter.notifyDataSetChanged();
            }
        };
        mGrupActivityViewModel.getGrups().observe(this, observerGrups);

        // A partir d'aquí, en cas que es faci cap canvi a la llista d'usuaris, HomeActivity ho sabrá
        mGrupActivityViewModel.loadGrupsFromRepository(mAuth.getCurrentUser().getEmail());  // Internament pobla els usuaris de la BBDD
    }
}