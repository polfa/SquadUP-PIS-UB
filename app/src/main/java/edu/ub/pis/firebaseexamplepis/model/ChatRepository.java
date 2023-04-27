package edu.ub.pis.firebaseexamplepis.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRepository {
    private static final String TAG = "Chat Repository";

    /** Autoinstància, pel patró singleton */
    private static final ChatRepository mInstance = new ChatRepository();

    /** Referència a la Base de Dades */
    private FirebaseFirestore mDb;

    private ArrayList<Chat> chatList = new ArrayList<>();

    /** Definició de listener (interficie),
     *  per escoltar quan s'hagin acabat de llegir els usuaris de la BBDD */
    public interface OnLoadChatsListener {
        void onLoadChats(ArrayList<Chat> chats);
    }

    public ArrayList<ChatRepository.OnLoadChatsListener> mOnLoadChatsListeners = new ArrayList<>();

    /** Definició de listener (interficie)
     * per poder escoltar quan s'hagi acabat de llegir la Url de la foto de perfil
     * d'un usuari concret */
    public interface OnLoadUserPictureUrlListener {
        void  OnLoadUserPictureUrl(String pictureUrl);
    }

    public ChatRepository.OnLoadUserPictureUrlListener mOnLoadUserPictureUrlListener;

    /**
     * Constructor privat per a forçar la instanciació amb getInstance(),
     * com marca el patró de disseny Singleton class
     */
    private ChatRepository() {
        mDb = FirebaseFirestore.getInstance();
    }

    /**
     * Retorna aqusta instancia singleton
     * @return
     */
    public static ChatRepository getInstance() {
        return mInstance;
    }

    /**
     * Afegir un listener de la operació OnLoadUsersListener.
     * Pot haver-n'hi només un. Fem llista, com a exemple, per demostrar la flexibilitat
     * d'aquest disseny.
     * @param listener
     */
    public void addOnLoadChatsListener(ChatRepository.OnLoadChatsListener listener) {
        mOnLoadChatsListeners.add(listener);
    }

    /**
     * Setejem un listener de la operació OnLoadUserPictureUrlListener.
     * En aquest cas, no és una llista de listeners. Només deixem haver-n'hi un,
     * també a tall d'exemple.
     * @param listener
     */
    public void setOnLoadUserPictureListener(ChatRepository.OnLoadUserPictureUrlListener listener) {
        mOnLoadUserPictureUrlListener = listener;
    }

    /**
     * Mètode que llegeix els usuaris. Vindrà cridat des de fora i quan acabi,
     * avisarà sempre als listeners, invocant el seu OnLoadUsers.
     */
    public void loadChats(ArrayList<Chat> chats){
        chats.clear();
        mDb.collection("chats")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Chat chat = new Chat(
                                        document.getString("user1_ID"),
                                        document.getString("user2_ID")
                                );
                                chats.add(chat);
                            }
                            chatList = chats;
                            /* Callback listeners */
                            for (ChatRepository.OnLoadChatsListener l: mOnLoadChatsListeners) {
                                l.onLoadChats(chats);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void loadPictureOfUser(String email) {
        mDb.collection("users")
                .document(email)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                mOnLoadUserPictureUrlListener.OnLoadUserPictureUrl(document.getString("picture_url"));
                            } else {
                                Log.d("LOGGER", "No such document");
                            }
                        } else {
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }
                    }
                });
    }


    /**
     * Mètode que afegeix un nou usuari a la base de dades. Utilitzat per la funció
     * de Sign-Up (registre) de la SignUpActivity.
     * @param chatID
     * @param user1_ID
     * @param user2_ID
     */
    public void addChat(
            String chatID,
            String user1_ID,
            String user2_ID
    ) {
        // Obtenir informació personal de l'usuari
        Map<String, Object> newChat = new HashMap<>();
        newChat.put("user1_ID", user1_ID);
        newChat.put("user2_ID", user2_ID);
        chatID = String.valueOf(chatList.size() * 2);


        // Afegir-la a la base de dades
        mDb.collection("chats").document(chatID).set(newChat)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Sign up completion succeeded");
                        } else {
                            Log.d(TAG, "Sign up completion failed");
                        }
                    }
                });
    }

    public void setPictureUrlOfUser(String userId, String pictureUrl) {
        Map<String, Object> userEntry = new HashMap<>();
        userEntry.put("picture_url", pictureUrl);

        mDb.collection("users")
                .document(userId)
                .set(userEntry, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Photo upload succeeded: " + pictureUrl);
                })
                .addOnFailureListener(exception -> {
                    Log.d(TAG, "Photo upload failed: " + pictureUrl);
                });
    }


}
