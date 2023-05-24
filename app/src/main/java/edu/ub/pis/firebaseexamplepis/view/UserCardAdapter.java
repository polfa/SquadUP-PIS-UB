package edu.ub.pis.firebaseexamplepis.view;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.model.User;

public class UserCardAdapter extends RecyclerView.Adapter<UserCardAdapter.ViewHolder> {

    /** Definició de listener (interficie)
     *  per a quan algú vulgui escoltar un event de OnClickHide, és a dir,
     *  quan l'usuari faci clic en la creu (amagar) algún dels items de la RecyclerView
     */
    public interface OnClickHideListener {
        void OnClickHide(int position);
    }

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private ArrayList<User> mUsers; // Referència a la llista d'usuaris
    private OnClickHideListener mOnClickHideListener; // Qui hagi de repintar la ReciclerView
                                                      // quan s'amagui
    // Constructor
    public UserCardAdapter(ArrayList<User> userList) {
        mUsers = userList;


    }

    public void setOnClickHideListener(OnClickHideListener listener) {
        this.mOnClickHideListener = listener;
    }

    @NonNull
    @Override
    public UserCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate crea una view genèrica definida pel layout que l'hi passem (l'user_card_layout)
        View view = inflater.inflate(R.layout.user_card_layout, parent, false);

        // La classe ViewHolder farà de pont entre la classe User del model i la view (UserCard).
        return new UserCardAdapter.ViewHolder(view);
    }

    /* Mètode cridat per cada ViewHolder de la RecyclerView */
    @Override
    public void onBindViewHolder(@NonNull UserCardAdapter.ViewHolder holder, int position) {
        // El ViewHolder té el mètode que s'encarrega de llegir els atributs del User (1r parametre),
        // i assignar-los a les variables del ViewHolder.
        // Qualsevol listener que volguem posar a un item, ha d'entrar com a paràmetre extra (2n).

        holder.bind(mUsers.get(position), this.mOnClickHideListener);

    }

    /**
     * Retorna el número d'elements a la llista.
     * @return
     */
    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    /**
     * Mètode que seteja de nou la llista d'usuaris si s'hi han fet canvis de manera externa.
     * @param users
     */
    public void setUsers(ArrayList<User> users) {
        this.mUsers = users; // no recicla/repinta res
    }

    /**
     * Mètode que repinta la RecyclerView sencera.
     */
    public void updateUsers() {
        notifyDataSetChanged();
    }

    /**
     * Mètode que repinta només posició indicada
     * @param position
     */
    public void hideUser(int position) {
        notifyItemRemoved(position);
    }

    /**
     * Classe ViewHolder. No és més que un placeholder de la vista (user_card_list.xml)
     * dels items de la RecyclerView. Podem implementar-ho fora de RecyclerViewAdapter,
     * però es pot fer dins.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mCardPictureUrl;
        private final TextView mCardNickname;
        private final ImageView mFavouriteGame;
        private final TextView mCardDescription;
        private final ImageView mHideButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mFavouriteGame = itemView.findViewById(R.id.favourite_game_image);
            mCardPictureUrl = itemView.findViewById(R.id.avatar);
            mCardNickname = itemView.findViewById(R.id.fullname);
            mCardDescription = itemView.findViewById(R.id.description_event);
            mHideButton = itemView.findViewById(R.id.hideButton);
        }

        public void bind(final User user, OnClickHideListener listener) {
            mCardNickname.setText(user.getNickname());
            mCardDescription.setText(user.getDescripcio());
            Picasso.get().load(user.getGameImage()).into(mFavouriteGame);
            // Carrega foto de l'usuari de la llista directament des d'una Url
            // d'Internet.
            Picasso.get().load(user.getURL()).into(mCardPictureUrl);
            // Seteja el listener onClick del botó d'amagar (hide), que alhora
            // cridi el mètode OnClickHide que implementen els nostres propis
            // listeners de tipus OnClickHideListener.
            mHideButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   listener.OnClickHide(getAdapterPosition());
               }
           });
        }
    }

}