package edu.ub.pis.firebaseexamplepis.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import edu.ub.pis.firebaseexamplepis.R;
import edu.ub.pis.firebaseexamplepis.model.Message;
import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.viewmodel.ActiveData;
import edu.ub.pis.firebaseexamplepis.viewmodel.GrupActivityViewModel;

public class GrupInsideCardAdapter extends RecyclerView.Adapter<GrupInsideCardAdapter.ViewHolder> {

    /** Definició de listener (interficie)
     *  per a quan algú vulgui escoltar un event de OnClickHide, és a dir,
     *  quan l'usuari faci clic en la creu (amagar) algún dels items de la RecyclerView
     */
    public interface OnClickHideListener {
        void OnClickHide(int position);
    }
    private GrupActivityViewModel mGrupActivityViewModel; //nuestro viewModel

    private ArrayList<Message> messages;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private User currentUser;

    private OnClickHideListener mOnClickHideListener; // Qui hagi de repintar la ReciclerView
    // quan s'amagui
    // Constructor
    public GrupInsideCardAdapter(ArrayList<Message> messages, GrupActivityViewModel viewModelGrup) {
        mGrupActivityViewModel = viewModelGrup;
        currentUser = mGrupActivityViewModel.getUserById(mAuth.getCurrentUser().getEmail());
        this.messages = messages;
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



    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getUserID().equals(mAuth.getCurrentUser().getEmail())) {
            return 0;
        } else {
            return 1;
        }
    }

    public void updateMessages(ArrayList<Message> messages){
        this.messages = messages;
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

        private final TextView mTextViewMessage;

        private ActiveData data = ActiveData.getInstance();
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewMessage = itemView.findViewById(R.id.textViewMessageContent);

        }

        public void bind(final Message message, OnClickHideListener listener) {

            if (message != null && mAuth.getCurrentUser() != null) {
                if (!message.read()) {
                    message.setRead(mAuth.getCurrentUser().getEmail());
                }
                mTextViewMessage.setText(message.getText());
                System.out.println("2333333333333334");

            }
            //Picasso.get().load(event.getGameImageId()).into(mCardGameImage);
            //Picasso.get().load(event.getRankImageId()).into(mCardRankImage);
            // Seteja el listener onClick del botó d'amagar (hide), que alhora
            // cridi el mètode OnClickHide que implementen els nostres propis
            // listeners de tipus OnClickHideListener.

        }
    }
}

