package edu.ub.pis.firebaseexamplepis.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.viewmodel.ChatActivityViewModel;
import edu.ub.pis.firebaseexamplepis.viewmodel.HomeActivityViewModel;

public class MatchMakingActivity extends AppCompatActivity {
    private final String TAG = "MatchMakingActivity";

    private String[] listGames = {"Rocket_League", "Valorant", "CSGO"};
    /* Elements de la vista de la HomeActivity */
    private ImageView mUserPhoto;
    private Spinner mGameSpinner;
    private Button mFindPartner;
    private ImageView mModifyPersonalInfoButton;
    private ImageView mChatBtn;
    private ImageView mHomeBtn;
    private ImageView mLoggedPictureImageView;
    private ViewGroup loggedLayout;
    private TextView mFoundPartner;
    private TextView mPartnerName;


    /**
     * Autenticació de Firebase
     */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private HomeActivityViewModel mHomeActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHomeActivityViewModel = new ViewModelProvider(this)
                .get(HomeActivityViewModel.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_making_partner);
        mHomeBtn = findViewById(R.id.home_btn);
        mChatBtn = findViewById(R.id.messages_btn);
        mFindPartner = findViewById(R.id.findPartnerBtn);
        mUserPhoto = findViewById(R.id.partnerImage);
        mGameSpinner = findViewById(R.id.game_name_spinner3);
        ArrayAdapter<String> adapterGameNames = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, listGames);
        mGameSpinner.setAdapter(adapterGameNames);
        mFoundPartner = findViewById(R.id.textView15);
        mPartnerName = findViewById(R.id.partnerName);

        mFoundPartner.setVisibility(View.GONE);
        mPartnerName.setVisibility(View.GONE);
        mUserPhoto.setVisibility(View.GONE);

        // Elements del ViewGroup inferior (email, botó logout, etc),
        // que només mostrarem si hi ha usuari logat.
        loggedLayout = findViewById(R.id.loggedLayout);

        if (mAuth.getCurrentUser() != null) {  // Si hi ha usuari logat...
            // Obté elements de loggedLayout
            mModifyPersonalInfoButton = findViewById(R.id.modifyPersonalInfoButton);
            mLoggedPictureImageView = findViewById(R.id.loggedPictureImageView);

            // Mostrar usuari logat

            // Defineix listeners
            mModifyPersonalInfoButton.setOnClickListener(view -> {
                Intent intent = new Intent(MatchMakingActivity.this, UpdateInfoActivity.class);
                startActivity(intent);
            });
            mHomeBtn.setOnClickListener(view -> {
                Intent intent = new Intent(MatchMakingActivity.this, HomeActivity.class);
                startActivity(intent);
            });

            mChatBtn.setOnClickListener(view -> {
                Intent intent = new Intent(MatchMakingActivity.this, ChatActivity.class);
                startActivity(intent);
            });

        } else { // Si no ho està, ...
            //
            loggedLayout.setVisibility(View.GONE); // No mostris cap element del layout inferior
        }
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

        // Anem a buscar la RecyclerView i fem dues coses:
    }
}