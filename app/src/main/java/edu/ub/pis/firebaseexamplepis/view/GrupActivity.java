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
import edu.ub.pis.firebaseexamplepis.model.Chat;
import edu.ub.pis.firebaseexamplepis.model.Grup;
import edu.ub.pis.firebaseexamplepis.viewmodel.ActiveData;
import edu.ub.pis.firebaseexamplepis.viewmodel.GrupActivityViewModel;

public class GrupActivity extends AppCompatActivity {
    private final String TAG = "HomeActivity";

    /** ViewModel del HomeActivity */
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
    private ImageView mMatchMaking;
    private TextView mFriendsButton;

    private ImageView mChatButton;
    /** Adapter de la RecyclerView */
    private GrupCardAdapter mGrupCardRVAdapter;

    /** Autenticació de Firebase */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /** Foto de perfil de l'usuari */
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
            mMatchMaking = findViewById(R.id.matchMakingButton);

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
            mMatchMaking.setOnClickListener(view -> {
                Intent intent = new Intent(GrupActivity.this, MatchMakingActivity.class);
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

        mGrupCardRVAdapter.setOnClickEnterListener(new GrupCardAdapter.OnClickEnterListener() {
            // Listener que escoltarà quan interactuem amb un item en una posició donada
            // dins de la recicler view. En aquest cas, quan es faci clic al botó d'amagar
            // l'usuari.
            @Override
            public void OnClickEnter(int position, Grup grup) {
                ActiveData.getInstance().setCurrentGrup(grup);
                Intent intent = new Intent(GrupActivity.this, GrupInsideActivity.class);
                startActivity(intent);
            }
        });

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
            mGrupActivityViewModel.getPictureUrl().observe(this, observerPictureUrl);

            mGrupActivityViewModel.loadPictureOfUser(mAuth.getCurrentUser().getEmail());
        }
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