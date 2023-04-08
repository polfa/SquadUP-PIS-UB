package edu.ub.pis.firebaseexamplepis.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.viewmodel.HomeActivityViewModel;
import edu.ub.pis.firebaseexamplepis.model.User;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = "HomeActivity";

    /** ViewModel del HomeActivity */
    private HomeActivityViewModel mHomeActivityViewModel; //nuestro viewModel

    /* Elements de la vista de la HomeActivity */
    private TextView mLoggedAsText;
    private ImageButton mModifyPersonalInfoButton;
    private ImageView mLoggedPictureImageView;
    private ImageButton mTakePictureButton;
    private ImageButton mChoosePictureButton; // [Exercici 2: crea aquest botó al layout i implementa
                                              // correctament setChoosePictureListener()]
    private Button mLogoutButton;
    private ViewGroup loggedLayout;
    private RecyclerView mUserCardsRV; // RecyclerView

    /** Adapter de la RecyclerView */
    private UserCardAdapter mUserCardRVAdapter;

    /** Autenticació de Firebase */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /** Foto de perfil de l'usuari */
    private Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicialitza el ViewModel d'aquesta activity (HomeActivity)
        mHomeActivityViewModel = new ViewModelProvider(this)
            .get(HomeActivityViewModel.class);

        // Elements del ViewGroup inferior (email, botó logout, etc),
        // que només mostrarem si hi ha usuari logat.
        loggedLayout = findViewById(R.id.loggedLayout);

        if (mAuth.getCurrentUser() != null) {  // Si hi ha usuari logat...
            // Obté elements de loggedLayout
            mLoggedAsText = findViewById(R.id.loggedAsText);
            mModifyPersonalInfoButton = findViewById(R.id.modifyPersonalInfoButton);
            mLoggedPictureImageView = findViewById(R.id.loggedPictureImageView);
            mTakePictureButton = findViewById(R.id.takePictureButton);
            mLogoutButton = findViewById(R.id.logoutButton);

            // Mostrar usuari logat
            mLoggedAsText.setText(mAuth.getCurrentUser().getEmail());

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

            setTakeCameraPictureListener(mTakePictureButton);
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

            mHomeActivityViewModel.loadPictureOfUser(mAuth.getCurrentUser().getEmail());
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
    private void setTakeCameraPictureListener(@NonNull View takePictureView) {
        // Codi que s'encarrega de rebre el resultat de l'intent de fer foto des de càmera
        // i que es llençarà des del listener que definirem a baix.
        ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        mHomeActivityViewModel.setPictureUrlOfUser(
                            mAuth.getCurrentUser().getEmail(), mPhotoUri
                        );
                    }
                }
            }
        );

        // Listener del botó de fer foto, que llençarà l'intent amb l'ActivityResultLauncher.
        takePictureView.setOnClickListener(view -> {
            // Crearem un nom de fitxer d'imatge temporal amb una data i hora i format JPEG
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            // Anem a buscar el directori extern (del sistema) especificat per la variable
            // d'entorn Environment.DIRECTORY_PICTURES (pren per valor "Pictures").
            // Se li afageix, com a sufix, el directori del sistema on es guarden els fitxers.
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            // Creem el fitxer
            File image = null;
            try {
                image = File.createTempFile(
                        imageFileName,  /* Prefix */
                        ".jpg",         /* Sufix */
                        storageDir      /* Directori on es guarda la imatge */
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Recuperem la Uri definitiva del fitxer amb FileProvider (obligatori per seguretat)
            // Per a fer-ho:
            // 1. Especifiquem a res>xml>paths.xml el directori on es guardarà la imatge
            //    de manera definitiva.
            // 2. Afegir al manifest un provider que apunti a paths.xml del pas 1
            Uri photoUri = FileProvider.getUriForFile(this,
                    "edu.ub.pis.firebaseexamplepis.fileprovider",
                    image);

            // Per tenir accés a la URI de la foto quan es llenci l'intent de la camara.
            // Perquè encara que li passem la photoUri com a dades extra a l'intent, aquestes
            // no tornen com a resultat de l'Intent.
            mPhotoUri = photoUri;

            // Llancem l'intent amb el launcher declarat al començament d'aquest mateix mètode
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mPhotoUri);
            takePictureLauncher.launch(intent);
        });
    }

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