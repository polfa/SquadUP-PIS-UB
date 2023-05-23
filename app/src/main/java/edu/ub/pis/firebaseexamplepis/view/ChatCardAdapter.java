package edu.ub.pis.firebaseexamplepis.view;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
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
import edu.ub.pis.firebaseexamplepis.viewmodel.ChatActivityViewModel;

public class ChatCardAdapter extends RecyclerView.Adapter<ChatCardAdapter.ViewHolder> {

    /** Definició de listener (interficie)
     *  per a quan algú vulgui escoltar un event de OnClickHide, és a dir,
     *  quan l'usuari faci clic en la creu (amagar) algún dels items de la RecyclerView
     */
    public interface OnClickEnterListener {
        void OnClickEnter(int position, Chat chat);
    }
    private static ChatActivityViewModel mChatActivityViewModel; //nuestro viewModel

    private ArrayList<Chat> mChats;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private User currentUser;
    private OnClickEnterListener mOnClickEnterListener; // Qui hagi de repintar la ReciclerView
                                                      // quan s'amagui
    // Constructor
    public ChatCardAdapter(ArrayList<Chat> chatList, ChatActivityViewModel viewModelChat) {
        mChatActivityViewModel = viewModelChat;
        currentUser = mChatActivityViewModel.getUserById(mAuth.getCurrentUser().getEmail());
        this.mChats = chatList;
    }



    public void setOnClickEnterListener(OnClickEnterListener listener) {
        this.mOnClickEnterListener = listener;
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
            holder.bind(mChats.get(position), this.mOnClickEnterListener);
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
    public void enterChat(int position) {
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

        private final ConstraintLayout mChatBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardPictureUrl = itemView.findViewById(R.id.avatar);
            mCardFullName = itemView.findViewById(R.id.fullname);
            mCardChat = itemView.findViewById(R.id.chat);
            mCardTime = itemView.findViewById(R.id.time_last_message);
            mCardNumMisatges =  itemView.findViewById(R.id.num_missatges);
            mChatBtn = itemView.findViewById(R.id.chatBtn);
        }

        public void bind(final Chat chat, OnClickEnterListener listener) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            Date date = new Date();
            User currentUser = mChatActivityViewModel.getUserById(mAuth.getCurrentUser().getEmail());
            User chatUser = chat.getUser(currentUser);
            mCardFullName.setText(chatUser.getNickname());
            mCardChat.setText(chat.getLastMessage().getText());

            String day;
            String hour = String.valueOf(Integer.parseInt(chat.getLastMessage().getTime().toString().substring(11, 13)) + 2);
            hour = hour + chat.getLastMessage().getTime().toString().substring(13, 16);
            if (chat.getLastMessage().getTime().toString().substring(0, 3).equals(date.toString().substring(0, 3))) {
                day = "Today";
            } else {
                day = chat.getLastMessage().getTime().toString().substring(0, 3);
            }

            mCardNumMisatges.setText(String.valueOf(chat.getMessages().size()));

            mCardTime.setText(day + " " + hour);
            // Carrega foto de l'usuari de la llista directament des d'una Url
            // d'Internet.
            Picasso.get().load(chatUser.getURL()).into(mCardPictureUrl);
            //Picasso.get().load(event.getGameImageId()).into(mCardGameImage);
            //Picasso.get().load(event.getRankImageId()).into(mCardRankImage);
            // Seteja el listener onClick del botó d'amagar (hide), que alhora
            // cridi el mètode OnClickHide que implementen els nostres propis
            // listeners de tipus OnClickHideListener.

            mChatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnClickEnter(getAdapterPosition(),chat);
                }
            });



        }
    }

}