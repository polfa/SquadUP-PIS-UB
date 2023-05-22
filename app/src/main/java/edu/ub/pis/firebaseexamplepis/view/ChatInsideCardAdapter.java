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
import edu.ub.pis.firebaseexamplepis.model.Message;
import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.model.UserRepository;
import edu.ub.pis.firebaseexamplepis.viewmodel.ChatActivityViewModel;

public class ChatInsideCardAdapter extends RecyclerView.Adapter<ChatInsideCardAdapter.ViewHolder> {

    /** Definició de listener (interficie)
     *  per a quan algú vulgui escoltar un event de OnClickHide, és a dir,
     *  quan l'usuari faci clic en la creu (amagar) algún dels items de la RecyclerView
     */
    public interface OnClickHideListener {
        void OnClickHide(int position);
    }
    private ChatActivityViewModel mChatActivityViewModel; //nuestro viewModel

    private ArrayList<Message> messages;

    private FirebaseAuth mAuth;
    private User currentUser;
    private OnClickHideListener mOnClickHideListener; // Qui hagi de repintar la ReciclerView
    // quan s'amagui
    // Constructor
    public ChatInsideCardAdapter(Chat chat, ChatActivityViewModel viewModelChat) {
        mAuth = FirebaseAuth.getInstance();
        mChatActivityViewModel = viewModelChat;
        currentUser = mChatActivityViewModel.getUserById();
        this.messages = chat.getMessages();
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


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_own, parent, false);
            return new ViewHolder(view);
        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other, parent, false);
            return new ViewHolder(view);
        }
        return null;
    }

    /* Mètode cridat per cada ViewHolder de la RecyclerView */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(messages.get(position), this.mOnClickHideListener);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(Chat chat) {
        this.messages = chat.getMessages(); // no recicla/repinta res
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getUserID() == mAuth.getCurrentUser().getEmail()) {
            return 0;
        } else {
            return 1;
        }
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

        private final TextView mTextViewMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewMessage = itemView.findViewById(R.id.textViewMessageContent);

        }

        public void bind(final Message message, OnClickHideListener listener) {
            mTextViewMessage.setText(message.getText());
            //Picasso.get().load(event.getGameImageId()).into(mCardGameImage);
            //Picasso.get().load(event.getRankImageId()).into(mCardRankImage);
            // Seteja el listener onClick del botó d'amagar (hide), que alhora
            // cridi el mètode OnClickHide que implementen els nostres propis
            // listeners de tipus OnClickHideListener.

        }
    }
}

