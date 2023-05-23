package edu.ub.pis.firebaseexamplepis.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.model.Grup;
import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.model.UserRepository;
import edu.ub.pis.firebaseexamplepis.viewmodel.GrupActivityViewModel;

public class GrupCardAdapter extends RecyclerView.Adapter<GrupCardAdapter.ViewHolder> {

    /** Definició de listener (interficie)
     *  per a quan algú vulgui escoltar un event de OnClickHide, és a dir,
     *  quan l'usuari faci clic en la creu (amagar) algún dels items de la RecyclerView
     */
    public interface OnClickHideListener {
        void OnClickHide(int position);
    }
    private GrupActivityViewModel mGrupActivityViewModel; //nuestro viewModel

    private ArrayList<Grup> mGrups;

    private FirebaseAuth mAuth;
    private User currentUser;
    private OnClickHideListener mOnClickHideListener; // Qui hagi de repintar la ReciclerView
                                                      // quan s'amagui
    // Constructor
    public GrupCardAdapter(ArrayList<Grup> grupList, GrupActivityViewModel viewModelGrup) {
        mAuth = FirebaseAuth.getInstance();
        mGrupActivityViewModel = viewModelGrup;
        currentUser = mGrupActivityViewModel.getUserById();
        this.mGrups = grupList;
    }


    public ArrayList<Grup> getCuerrentUserGrups(ArrayList<Grup> grupList){
        ArrayList<Grup> aux = new ArrayList<>();
        for (Grup c: grupList){
            System.out.println("------------------------");
            if(c.userInGrup(currentUser)){
                System.out.println(c.getUser1().getID() + c.getUser2().getID() + currentUser.getID());
                aux.add(c);
            }
        }
        return aux;
    }

    public void setOnClickHideListener(OnClickHideListener listener) {
        this.mOnClickHideListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate crea una view genèrica definida pel layout que l'hi passem (l'event_card_layout)
        View view = inflater.inflate(R.layout.grup_card_layout, parent, false);

        // La classe ViewHolder farà de pont entre la classe Event del model i la view (EventCard).
        return new ViewHolder(view);
    }

    /* Mètode cridat per cada ViewHolder de la RecyclerView */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // El ViewHolder té el mètode que s'encarrega de llegir els atributs del Event (1r parametre),
        // i assignar-los a les variables del ViewHolder.
        // Qualsevol listener que volguem posar a un item, ha d'entrar com a paràmetre extra (2n).
        if (mGrups.get(position).userInGrup(currentUser)) {
            holder.bind(mGrups.get(position), this.mOnClickHideListener);
        }
    }

    @Override
    public int getItemCount() {
        return mGrups.size();
    }

    public void setGrups(ArrayList<Grup> grups) {
        this.mGrups = grups; // no recicla/repinta res
    }

    /**
     * Retorna el número d'elements a la llista.
     * @return
     */
    /**
    @Override
    public int getItemCount() {
        return mEvents.size();
    }

     * Mètode que seteja de nou la llista d'usuaris si s'hi han fet canvis de manera externa.
     * @param events

    public void setEvents(ArrayList<Event> events) {
        this.mEvents = events; // no recicla/repinta res
    }
    */
    /**
     * Mètode que repinta la RecyclerView sencera.
     */
    public void updateGrups() {
        notifyDataSetChanged();
    }

    /**
     * Mètode que repinta només posició indicada
     * @param position
     */
    public void hideGrup(int position) {
        notifyItemRemoved(position);
    }

    /**
     * Classe ViewHolder. No és més que un placeholder de la vista (event_card_list.xml)
     * dels items de la RecyclerView. Podem implementar-ho fora de RecyclerViewAdapter,
     * però es pot fer dins.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mCardPictureUrl;
        private final TextView mCardFullName;
        private final TextView mCardGrup;
        private final TextView mCardTime;

        private final TextView mCardNumMisatges;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardPictureUrl = itemView.findViewById(R.id.avatar);
            mCardFullName = itemView.findViewById(R.id.fullname);
            mCardGrup = itemView.findViewById(R.id.chat);
            mCardTime = itemView.findViewById(R.id.time_event3);
            mCardNumMisatges =  itemView.findViewById(R.id.num_missatges);
        }

        public void bind(final Grup grup, OnClickHideListener listener) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            Date date = new Date();
            UserRepository userRepository = UserRepository.getInstance();
            User currentUser = userRepository.getUserById(mAuth.getCurrentUser().getEmail());
            User grupUser = grup.getUser(currentUser);
            mCardFullName.setText(grupUser.getNickname());
            mCardGrup.setText(grup.getLastMessage().getText());

            String day;
            String hour = String.valueOf(Integer.parseInt(grup.getLastMessage().getTime().toString().substring(11, 13)) + 2);
            hour = hour + grup.getLastMessage().getTime().toString().substring(13, 16);
            if (grup.getLastMessage().getTime().toString().substring(0, 3).equals(date.toString().substring(0, 3))) {
                day = "Today";
            } else {
                day = grup.getLastMessage().getTime().toString().substring(0, 3);
            }
            mCardNumMisatges.setText(String.valueOf(grup.getMessages().size()));
            mCardTime.setText(day + " " + hour);
            // Carrega foto de l'usuari de la llista directament des d'una Url
            // d'Internet.
            Picasso.get().load(grupUser.getURL()).into(mCardPictureUrl);
            //Picasso.get().load(event.getGameImageId()).into(mCardGameImage);
            //Picasso.get().load(event.getRankImageId()).into(mCardRankImage);
            // Seteja el listener onClick del botó d'amagar (hide), que alhora
            // cridi el mètode OnClickHide que implementen els nostres propis
            // listeners de tipus OnClickHideListener.

        }
    }

}