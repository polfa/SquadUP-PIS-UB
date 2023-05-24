package edu.ub.pis.firebaseexamplepis.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

public class GrupInsideActivity extends AppCompatActivity {
    private final String TAG = "GrupInsideActivity";

    /**
     * ViewModel del GrupActivity
     */
    private GrupActivityViewModel mGrupActivityViewModel; //nuestro viewModel

    /* Elements de la vista de la GrupActivity */
    private ImageView mExitButton;

    private TextView mGrupNameTxt;
    private ImageView mGrupPicture;

    private Button mSendButton;
    private EditText mTypeTxt; // [Exercici 2: crea aquest botó al layout i implementa
    // correctament setChoosePictureListener()]
    private Button mLogoutButton;
    private ViewGroup loggedLayout;
    private RecyclerView mGrupCardsRV; // RecyclerView

    /**
     * Adapter de la RecyclerView
     */
    private GrupInsideCardAdapter mGrupCardRVAdapter;

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
        setContentView(R.layout.activity_grup_inside);
        mExitButton = findViewById(R.id.exit_btn);
        mGrupNameTxt = findViewById(R.id.grup_name_txt);
        mGrupPicture = findViewById(R.id.grup_image);
        mSendButton = findViewById(R.id.buttonSend);
        mTypeTxt = findViewById(R.id.type_message_txt);

        // Inicialitza el ViewModel d'aquesta activity (GrupActivity)
        mGrupActivityViewModel = new ViewModelProvider(this)
                .get(GrupActivityViewModel.class);

        // Elements del ViewGroup inferior (email, botó logout, etc),
        // que només mostrarem si hi ha usuari logat.

        if (mAuth.getCurrentUser() != null) {  // Si hi ha usuari logat...
            // Obté elements de loggedLayout

            // Mostrar usuari logat

            // Defineix listeners
            mExitButton.setOnClickListener(view -> {;
                mGrupActivityViewModel.updateGrup(activeData.getCurrentGrup());
                activeData.setCurrentGrup(null);
                Intent intent = new Intent(GrupInsideActivity.this, GrupActivity.class);
                startActivity(intent);
            });

            mGrupNameTxt.setText(activeData.getCurrentGrup().getGroupName());

            Picasso.get().load(activeData.getCurrentGrup().getImageURL()).into(mGrupPicture);

            // Anem a buscar la RecyclerView i fem dues coses:
            mGrupCardsRV = findViewById(R.id.recyclerViewChat);

            // (1) Li assignem un layout manager.
            LinearLayoutManager manager = new LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL, false
            );
            mGrupCardsRV.setLayoutManager(manager);


            // (2) Inicialitza el RecyclerViewAdapter i li assignem a la RecyclerView.
            mGrupCardRVAdapter = new GrupInsideCardAdapter(
                    activeData.getCurrentGrup().getMessages(), mGrupActivityViewModel // Passem-li referencia llista usuaris
            );
            mGrupCardsRV.setAdapter(mGrupCardRVAdapter); // Associa l'adapter amb la ReciclerView

            final Observer<ArrayList<Grup>> observerGrups = new Observer<ArrayList<Grup>>() {
                @Override
                public void onChanged(ArrayList<Grup> grups) {
                    mGrupCardRVAdapter.notifyDataSetChanged();
                }
            };
            mGrupActivityViewModel.getGrups().observe(this, observerGrups);

            mSendButton.setOnClickListener(view -> {
                String text = mTypeTxt.getText().toString();
                mTypeTxt.setText("");
                activeData.getCurrentGrup().addMessage(mAuth.getCurrentUser().getEmail(),text);
                mGrupActivityViewModel.updateGrup(activeData.getCurrentGrup());
                mGrupCardRVAdapter.updateMessages(activeData.getCurrentGrup().getMessages());
                mGrupCardRVAdapter.notifyDataSetChanged();
            });
        }
    }
}
