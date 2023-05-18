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
import edu.ub.pis.firebaseexamplepis.model.Chat;
import edu.ub.pis.firebaseexamplepis.model.Event;
import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.model.UserRepository;

public class ChatCardAdapter extends RecyclerView.Adapter<ChatCardAdapter.ViewHolder> {

    /** Definició de listener (interficie)
     *  per a quan algú vulgui escoltar un event de OnClickHide, és a dir,
     *  quan l'usuari faci clic en la creu (amagar) algún dels items de la RecyclerView
     */
    public interface OnClickHideListener {
        void OnClickHide(int position);
    }
    private ArrayList<Chat> mChats;

    private FirebaseAuth mAuth;
    private UserRepository userRepository;
    private User currentUser;
    private OnClickHideListener mOnClickHideListener; // Qui hagi de repintar la ReciclerView
                                                      // quan s'amagui
    // Constructor
    public ChatCardAdapter(ArrayList<Chat> chatList) {
        mAuth = FirebaseAuth.getInstance();
        userRepository = UserRepository.getInstance();
        currentUser =  userRepository.getUserById(mAuth.getCurrentUser().getEmail());
        this.mChats = chatList;
    }


    public ArrayList<Chat> getCuerrentUserChats(ArrayList<Chat> chatList){
        ArrayList<Chat> aux = new ArrayList<>();
        for (Chat c: chatList){
            System.out.println("------------------------");
            if(c.userInChat(currentUser)){
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
        View view = inflater.inflate(R.layout.chat_card_layout, parent, false);

        // La classe ViewHolder farà de pont entre la classe Event del model i la view (EventCard).
        return new ViewHolder(view);
    }

    /* Mètode cridat per cada ViewHolder de la RecyclerView */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // El ViewHolder té el mètode que s'encarrega de llegir els atributs del Event (1r parametre),
        // i assignar-los a les variables del ViewHolder.
        // Qualsevol listener que volguem posar a un item, ha d'entrar com a paràmetre extra (2n).
        if (mChats.get(position).userInChat(currentUser)) {
            holder.bind(mChats.get(position), this.mOnClickHideListener);
        }
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    public void setChats(ArrayList<Chat> chats) {
        this.mChats = chats; // no recicla/repinta res
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
    public void updateChats() {
        notifyDataSetChanged();
    }

    /**
     * Mètode que repinta només posició indicada
     * @param position
     */
    public void hideChat(int position) {
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
        private final TextView mCardChat;
        private final TextView mCardTime;

        private final TextView mCardNumMisatges;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardPictureUrl = itemView.findViewById(R.id.avatar);
            mCardFullName = itemView.findViewById(R.id.fullname);
            mCardChat = itemView.findViewById(R.id.chat);
            mCardTime = itemView.findViewById(R.id.time_event3);
            mCardNumMisatges =  itemView.findViewById(R.id.num_missatges);
        }

        public void bind(final Chat chat, OnClickHideListener listener) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            Date date = new Date();
            UserRepository userRepository = UserRepository.getInstance();
            try {
                User currentUser = userRepository.getUserById(mAuth.getCurrentUser().getEmail());
                User chatUser = chat.getUser(currentUser);
                mCardFullName.setText(chatUser.getFirstName() + " " + chatUser.getLastName());
                mCardChat.setText(chat.getMessage());
                /*
                String day;
                //a
                String hour = String.valueOf(Integer.parseInt(event.getStartTime().toDate().toString().substring(11, 13)) + 2);
                hour = hour + event.getStartTime().toDate().toString().substring(13, 16);
                if (event.getStartTime().toDate().toString().substring(0, 3).equals(date.toString().substring(0, 3))) {
                    day = "Today";
                } else {
                    day = event.getStartTime().toDate().toString().substring(0, 3);
                }

                mCardTime.setText(day + " " + hour);
                */
                // Carrega foto de l'usuari de la llista directament des d'una Url
                // d'Internet.
                Picasso.get().load(chatUser.getURL()).into(mCardPictureUrl);
                //Picasso.get().load(event.getGameImageId()).into(mCardGameImage);
                //Picasso.get().load(event.getRankImageId()).into(mCardRankImage);
                // Seteja el listener onClick del botó d'amagar (hide), que alhora
                // cridi el mètode OnClickHide que implementen els nostres propis
                // listeners de tipus OnClickHideListener.
            }catch (Exception e){
                System.out.println(e.toString());
            }
        }
    }

}