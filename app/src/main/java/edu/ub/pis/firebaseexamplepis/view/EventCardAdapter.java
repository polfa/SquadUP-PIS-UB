package edu.ub.pis.firebaseexamplepis.view;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.model.Event;
import edu.ub.pis.firebaseexamplepis.model.UserRepository;
import edu.ub.pis.firebaseexamplepis.view.imageURLs.VideogameLogos;

public class EventCardAdapter extends RecyclerView.Adapter<EventCardAdapter.ViewHolder> {

    /** Definició de listener (interficie)
     *  per a quan algú vulgui escoltar un event de OnClickJoin, és a dir,
     *  quan l'usuari faci clic en la creu (amagar) algún dels items de la RecyclerView
     */
    public interface OnClickJoinListener {
        void OnClickJoin(int position);
    }
    private ArrayList<Event> mEvents; // Referència a la llista d'usuaris
    private OnClickJoinListener mOnClickJoinListener; // Qui hagi de repintar la ReciclerView
                                                      // quan s'amagui

    // Constructor
    public EventCardAdapter(ArrayList<Event> eventList) {
        this.mEvents = eventList; // no fa new (La llista la manté el ViewModel)

    }

    public void setOnClickJoinListener(OnClickJoinListener listener) {
        this.mOnClickJoinListener = listener;
    }

    @NonNull
    @Override
    public EventCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate crea una view genèrica definida pel layout que l'hi passem (l'event_card_layout)
        View view = inflater.inflate(R.layout.events_card_layout, parent, false);

        // La classe ViewHolder farà de pont entre la classe Event del model i la view (EventCard).
        return new EventCardAdapter.ViewHolder(view);
    }

    /* Mètode cridat per cada ViewHolder de la RecyclerView */
    @Override
    public void onBindViewHolder(@NonNull EventCardAdapter.ViewHolder holder, int position) {
        // El ViewHolder té el mètode que s'encarrega de llegir els atributs del Event (1r parametre),
        // i assignar-los a les variables del ViewHolder.
        // Qualsevol listener que volguem posar a un item, ha d'entrar com a paràmetre extra (2n).
        holder.bind(mEvents.get(position), this.mOnClickJoinListener);
    }

    /**
     * Retorna el número d'elements a la llista.
     * @return
     */
    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    /**
     * Mètode que seteja de nou la llista d'usuaris si s'hi han fet canvis de manera externa.
     * @param events
     */
    public void setEvents(ArrayList<Event> events) {
        this.mEvents = events; // no recicla/repinta res
    }

    /**
     * Mètode que repinta la RecyclerView sencera.
     */
    public void updateEvents() {
        notifyDataSetChanged();
    }

    /**
     * Mètode que repinta només posició indicada
     * @param position
     */
    public void JoinEvent(int position) {
        notifyItemRemoved(position);
    }

    public void ClickJoin(int position){


    }

    /**
     * Classe ViewHolder. No és més que un placeholder de la vista (event_card_list.xml)
     * dels items de la RecyclerView. Podem implementar-ho fora de RecyclerViewAdapter,
     * però es pot fer dins.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mCardPictureUrl;
        private final TextView mCardFullName;
        private final TextView mCardHobbies;
        private final TextView mCardTime;

        private final TextView mCardMembers;
        private final ImageView mCardGameImage;

        private final ImageView mCardRankImage;

        private final Button mCardJoin;

        private final ImageView mCardCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardPictureUrl = itemView.findViewById(R.id.avatar);
            mCardFullName = itemView.findViewById(R.id.fullname);
            mCardHobbies = itemView.findViewById(R.id.description_event);
            mCardGameImage = itemView.findViewById(R.id.game_event_image);
            mCardRankImage = itemView.findViewById(R.id.rank_event_image);
            mCardTime = itemView.findViewById(R.id.time_event);
            mCardMembers = itemView.findViewById(R.id.membersTxt);
            mCardJoin = itemView.findViewById(R.id.joinbtn);
            mCardCheck = itemView.findViewById(R.id.check_image);
        }

        public void bind(final Event event, OnClickJoinListener listener) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            Date date = new Date();
            UserRepository userRepository = UserRepository.getInstance();
            String currentUserID = userRepository.getUserById(mAuth.getCurrentUser().getEmail()).getID();
            if (date.getTime() < event.getStartTime().toDate().getTime()) {
                mCardFullName.setText(event.getUser().getFirstName() + " " + event.getUser().getLastName());
                mCardHobbies.setText(event.getDescription());
                String day;
                String hour = String.valueOf(Integer.parseInt(event.getStartTime().toDate().toString().substring(11, 13)) + 2);
                hour = hour + event.getStartTime().toDate().toString().substring(13, 16);
                if (event.getStartTime().toDate().toString().substring(0, 3).equals(date.toString().substring(0, 3))) {
                    day = "Today";
                } else {
                    day = event.getStartTime().toDate().toString().substring(0, 3);
                }

                mCardTime.setText(day + " " + hour);
                // Carrega foto de l'usuari de la llista directament des d'una Url
                // d'Internet
                Picasso.get().load(event.getUser().getURL()).into(mCardPictureUrl);
                VideogameLogos vl = VideogameLogos.valueOf(event.getGameImageId());
                Picasso.get().load(vl.getImageLocation()).into(mCardGameImage);
                Picasso.get().load(vl.getRank(event.getRankImageId())).into(mCardRankImage);
                mCardMembers.setText(event.getCurrentMembers() + "/" + event.getMaxMembers());
                if(event.userInEvent(currentUserID)){
                    mCardJoin.setVisibility(View.INVISIBLE);
                    mCardCheck.setVisibility(View.VISIBLE);
                }else{
                    mCardCheck.setVisibility(View.INVISIBLE);
                }
                mCardJoin.setOnClickListener(view -> {
                    if (!event.userInEvent(currentUserID)){
                        try {
                            event.addMember(userRepository.getUserById(currentUserID));
                            mCardJoin.setVisibility(View.INVISIBLE);
                            mCardCheck.setVisibility(View.VISIBLE);
                        }catch (Exception e){
                            System.out.println("User not found");
                        }
                    }
                    mCardMembers.setText(event.getCurrentMembers() + "/" + event.getMaxMembers());
                });

                mCardCheck.setOnClickListener(view -> {
                    if (event.userInEvent(currentUserID)){
                        try {
                            event.removeMember(userRepository.getUserById(currentUserID));
                            mCardJoin.setVisibility(View.VISIBLE);
                            mCardCheck.setVisibility(View.INVISIBLE);
                        }catch (Exception e){
                            System.out.println("User not found");
                        }
                    }
                    mCardMembers.setText(event.getCurrentMembers() + "/" + event.getMaxMembers());
                });
                // Seteja el listener onClick del botó d'amagar (Join), que alhora
                // cridi el mètode OnClickJoin que implementen els nostres propis
                // listeners de tipus OnClickJoinListener.
            }
        }
    }

}