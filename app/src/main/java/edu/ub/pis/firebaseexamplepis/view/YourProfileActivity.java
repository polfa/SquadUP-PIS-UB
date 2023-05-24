package edu.ub.pis.firebaseexamplepis.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.viewmodel.HomeActivityViewModel;

public class YourProfileActivity extends AppCompatActivity {
    private final String TAG = "YourProfileActivity";


    /* Elements de la vista de la YourProfileActivity */
    private ImageView mUserPhoto;
    private ImageView mFavouriteGame;
    private ImageView mRankInGame;
    private ImageView mModifyPersonalInfoButton;
    private ImageView mChatBtn;
    private ImageView mMatchMaking;
    private ImageView mHomeBtn;
    private ImageView mLoggedPictureImageView;
    private ViewGroup loggedLayout;
    private TextView mDescription;
    private TextView mProfileName;
    private HomeActivityViewModel mHomeActivityViewModel;


    /**
     * Autenticació de Firebase
     */
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mHomeActivityViewModel = new ViewModelProvider(this)
                .get(HomeActivityViewModel.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mHomeBtn = findViewById(R.id.home_btn);
        mChatBtn = findViewById(R.id.messages_btn);
        mFavouriteGame = findViewById(R.id.game_Image);
        mUserPhoto = findViewById(R.id.profilePicture);
        mRankInGame = findViewById(R.id.rank_Image);
        mDescription = findViewById(R.id.description_profile);
        mProfileName = findViewById(R.id.userNameTxt);
        mMatchMaking = findViewById(R.id.matchMakingButton);
        currentUser = mHomeActivityViewModel.getUserbyId(mAuth.getCurrentUser().getEmail());
        // Elements del ViewGroup inferior (email, botó logout, etc),
        // que només mostrarem si hi ha usuari logat.
        loggedLayout = findViewById(R.id.loggedLayout);

        mProfileName.setText(currentUser.getNickname());
        mDescription.setText(currentUser.getDescripcio());
        Picasso.get().load(currentUser.getGameImage()).into(mFavouriteGame);
        // Carrega foto de l'usuari de la llista directament des d'una Url
        // d'Internet.
        Picasso.get().load(currentUser.getRankImage()).into(mRankInGame);
        Picasso.get().load(currentUser.getURL()).into(mUserPhoto);
        if (mAuth.getCurrentUser() != null) {  // Si hi ha usuari logat...
            // Obté elements de loggedLayout
            mModifyPersonalInfoButton = findViewById(R.id.modifyPersonalInfoButton);
            mLoggedPictureImageView = findViewById(R.id.loggedPictureImageView);

            // Mostrar usuari logat

            // Defineix listeners
            mModifyPersonalInfoButton.setOnClickListener(view -> {
                Intent intent = new Intent(YourProfileActivity.this, UpdateInfoActivity.class);
                startActivity(intent);
            });
            mHomeBtn.setOnClickListener(view -> {
                Intent intent = new Intent(YourProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            });

            mChatBtn.setOnClickListener(view -> {
                Intent intent = new Intent(YourProfileActivity.this, ChatActivity.class);
                startActivity(intent);
            });
            mMatchMaking.setOnClickListener(view -> {
                Intent intent = new Intent(YourProfileActivity.this, MatchMakingActivity.class);
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