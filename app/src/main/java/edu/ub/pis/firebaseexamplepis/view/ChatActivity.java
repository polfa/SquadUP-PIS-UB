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

    /** ViewModel del HomeActivity */
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


    /** Adapter de la RecyclerView */
    private ChatCardAdapter mChatCardRVAdapter;

    /** Autenticació de Firebase */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /** Foto de perfil de l'usuari */
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
            mChatActivityViewModel.getChats().getValue(),mChatActivityViewModel // Passem-li referencia llista usuaris
        );
        mChatCardsRV.setAdapter(mChatCardRVAdapter); // Associa l'adapter amb la ReciclerView
        mChatCardRVAdapter.setOnClickEnterListener(new ChatCardAdapter.OnClickEnterListener() {
            // Listener que escoltarà quan interactuem amb un item en una posició donada
            // dins de la recicler view. En aquest cas, quan es faci clic al botó d'amagar
            // l'usuari.
            @Override
            public void OnClickEnter(int position,Chat chat) {
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
            mChatActivityViewModel.getPictureUrl().observe(this, observerPictureUrl);

            mChatActivityViewModel.loadPictureOfUser(mAuth.getCurrentUser().getEmail());
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