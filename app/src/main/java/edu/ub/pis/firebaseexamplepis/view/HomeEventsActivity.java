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
import edu.ub.pis.firebaseexamplepis.model.Event;
import edu.ub.pis.firebaseexamplepis.viewmodel.HomeEventsActivityViewModel;

public class HomeEventsActivity extends AppCompatActivity {
    private final String TAG = "HomeActivity";

    /** ViewModel del HomeActivity */
    private HomeEventsActivityViewModel mHomeEventsActivityViewModel; //nuestro viewModel

    /* Elements de la vista de la HomeActivity */
    private ImageView mModifyPersonalInfoButton;
    private ImageView mLoggedPictureImageView;
    private ImageButton mTakePictureButton;
    private ImageButton mChoosePictureButton; // [Exercici 2: crea aquest botó al layout i implementa
                                              // correctament setChoosePictureListener()]
    private Button mLogoutButton;

    private Button mNewEventButton;
    private ViewGroup loggedLayout;
    private RecyclerView mEventCardsRV; // RecyclerView

    private TextView mFriendsButton;

    private ImageView mChatButton;
    /** Adapter de la RecyclerView */
    private EventCardAdapter mEventCardRVAdapter;

    /** Autenticació de Firebase */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /** Foto de perfil de l'usuari */
    private Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_events);

        // Inicialitza el ViewModel d'aquesta activity (HomeEventsActivity)
        mHomeEventsActivityViewModel = new ViewModelProvider(this)
            .get(HomeEventsActivityViewModel.class);

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
            mNewEventButton = findViewById(R.id.button);

            // Mostrar usuari logat

            // Defineix listeners
            mModifyPersonalInfoButton.setOnClickListener(view -> {
                Intent intent = new Intent(HomeEventsActivity.this, UpdateInfoActivity.class);
                startActivity(intent);
            });
            mLogoutButton.setOnClickListener(view -> {
                mAuth.signOut();
                Intent intent = new Intent(HomeEventsActivity.this, AuthenticationActivity.class);
                startActivity(intent);
            });

            mFriendsButton.setOnClickListener(view -> {
                Intent intent = new Intent(HomeEventsActivity.this, HomeActivity.class);
                startActivity(intent);
            });

            mChatButton.setOnClickListener(view -> {
                Intent intent = new Intent(HomeEventsActivity.this, ChatActivity.class);
                startActivity(intent);
            });

            mNewEventButton.setOnClickListener(view -> {
                Intent intent = new Intent(HomeEventsActivity.this, CrearEventActivity.class);
                startActivity(intent);
            });
        } else { // Si no ho està, ...
            //
            loggedLayout.setVisibility(View.GONE); // No mostris cap element del layout inferior
        }

        // Anem a buscar la RecyclerView i fem dues coses:
        mEventCardsRV = findViewById(R.id.eventCardRv);

        // (1) Li assignem un layout manager.
        LinearLayoutManager manager = new LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false
        );
        mEventCardsRV.setLayoutManager(manager);

        // (2) Inicialitza el RecyclerViewAdapter i li assignem a la RecyclerView.
        mEventCardRVAdapter = new EventCardAdapter(
            mHomeEventsActivityViewModel.getEvents().getValue() // Passem-li referencia llista usuaris
        );
        mEventCardRVAdapter.setOnClickJoinListener(new EventCardAdapter.OnClickJoinListener() {
            // Listener que escoltarà quan interactuem amb un item en una posició donada
            // dins de la recicler view. En aquest cas, quan es faci clic al botó d'amagar
            // l'usuari.
            @Override
            public void OnClickJoin(int position) {
                mHomeEventsActivityViewModel.removeEventFromHome(position);
                mEventCardRVAdapter.JoinEvent(position);
            }
        });
        mEventCardsRV.setAdapter(mEventCardRVAdapter); // Associa l'adapter amb la ReciclerView

        // Observer a HomeActivity per veure si la llista de Event (observable MutableLiveData)
        // a HomeActivityViewModel ha canviat.
        final Observer<ArrayList<Event>> observerEvents = new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> Events) {
                mEventCardRVAdapter.notifyDataSetChanged();
            }
        };
        mHomeEventsActivityViewModel.getEvents().observe(this, observerEvents);

        // A partir d'aquí, en cas que es faci cap canvi a la llista d'usuaris, HomeActivity ho sabrá
        mHomeEventsActivityViewModel.loadEventsFromRepository();  // Internament pobla els usuaris de la BBDD

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
            mHomeEventsActivityViewModel.getPictureUrl().observe(this, observerPictureUrl);

            mHomeEventsActivityViewModel.loadPictureOfUser(mAuth.getCurrentUser().getEmail());
        }
    }

    public void ClickJoin(int position){
        mEventCardRVAdapter.ClickJoin(position);
    }

    /**
     * Bloc que seteja un listener al botó de fer foto i que la posarà com a imatge de perfil.
     * Això inclou:
     * 1. Preparar un launcher per llençar l'intent implicit que fa una captura des de la càmera.
     * 2. Crear el listener del botó, que prepararà un lloc on guardar temporalment la foto i
     *    seguidament llençarà l'intent.
     *
     * Un cop retorni l'Intent (onActivityResult), li demanarem al HomeActivityViewModel que faci coses,
     * incloent-hi: pujar la foto a FireStore per obtenir una Url d'Internet i assignar-la
     * a una variable observable, que HomeActivity està observant. Quan HomeActivity detecti
     * el canvi, la pintarà al seu l'ImageView loggedPictureImageView.
     */


    /**
     * Bloc que seteja un listener al botó de seleccionar foto i que la posarà com a imatge de perfil.
     * [Exercici 2: completar linies que hi manquen]
     */
    private void setChoosePictureListener(@NonNull View choosePicture) {
        // Codi que s'encarrega de rebre el resultat de l'intent de seleccionar foto de galeria
        // i que es llençarà des del listener que definirem a baix.
        ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                Uri contentUri = data.getData(); // En aquest intent, sí que hi arriba la URI
                /*
                 * [Exercici 2: Aquí hi manca 1 línia de codi]
                 */
            }
        });

        // Listener del botó de seleccionar imatge, que llençarà l'intent amb l'ActivityResultLauncher.
        choosePicture.setOnClickListener(view -> {
            Intent data = new Intent(Intent.ACTION_GET_CONTENT);
            data.addCategory(Intent.CATEGORY_OPENABLE);
            data.setType("image/*");
            Intent intent = Intent.createChooser(data, "Choose a file");
            /*
             * [Exercici 2: Aquí hi manca 1 línia de codi]
             */
        });
    }
}