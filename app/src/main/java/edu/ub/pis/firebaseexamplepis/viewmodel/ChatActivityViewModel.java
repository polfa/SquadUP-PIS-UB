package edu.ub.pis.firebaseexamplepis.viewmodel;

import android.app.Application;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import edu.ub.pis.firebaseexamplepis.model.Chat;
import edu.ub.pis.firebaseexamplepis.model.ChatRepository;
import edu.ub.pis.firebaseexamplepis.model.Event;
import edu.ub.pis.firebaseexamplepis.model.Message;
import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.model.UserRepository;

public class ChatActivityViewModel extends AndroidViewModel
{
    private final String TAG = "ChatActivityViewModel";

    /* Elements observables del ViewModel */
    private final MutableLiveData<ArrayList<Chat>> mChat; // Els Chats que la RecyclerView mostra al home
    private final MutableLiveData<String> mPictureUrl; // URL de la foto de l'usuari logat
    private final MutableLiveData<Integer> mHidPosition;

    private UserRepository userRepository;

    /* Repositori (base de dades) dels usuaris */
    private ChatRepository mChatRepository; // On es manté la informació dels usuaris




    public ChatActivityViewModel(Application application) {
        super(application);

        // Instancia els atributs
        userRepository = UserRepository.getInstance();
        mChat = new MutableLiveData<>(new ArrayList<>());
        mPictureUrl = new MutableLiveData<>();
        mHidPosition = new MutableLiveData<>();
        mChatRepository = ChatRepository.getInstance();

        // Quan s'acabin de llegir de la BBDD els usuaris, el ViewModel ha d'actualitzar
        // l'observable mChats. I com que la RecyclerView de la HomeChatsActivity està observant aquesta
        // mateixa variable (mChats), també se n'enterarà
        mChatRepository.addOnLoadChatsListener(new ChatRepository.OnLoadChatsListener() {
            @Override
            public void onLoadChats(ArrayList<Chat> chats) {
               chats = ordenarChatsPerData(chats);
               ChatActivityViewModel.this.setChats(chats);
            }
        });


        // Quan s'acabi de llegir la URL de la foto de perfil de l'usuari logat, el ViewModel
        // actualitza també mPictureUrl, per a que la HomeChatsActivity la mostri en l'ImageView
        // corresponent
        mChatRepository.setOnLoadUserPictureListener(new ChatRepository.OnLoadUserPictureUrlListener() {
            @Override
            public void OnLoadUserPictureUrl(String pictureUrl) {
                // Log.d(TAG, "Loaded pictureUrl: " + pictureUrl);
                mPictureUrl.setValue(pictureUrl);
            }
        });
    }

    private ArrayList<Chat> ordenarChatsPerData(ArrayList<Chat> chats) {
        Collections.sort(chats, new Comparator<Chat>() {
            public int compare(Chat e2, Chat e1) {
                return e1.getLastMessage().getTime().compareTo(e2.getLastMessage().getTime());
            }
        });

        return chats;
    }

    /*
     * Retorna els usuaris perquè la HomeChatsActivity pugui subscriure-hi l'observable.
     */
    public LiveData<ArrayList<Chat>> getChats() {
        return mChat;
    }

    /*
     * Mètode que serà invocat pel ChatRepository.OnLoadChatsListener definit al
     * constructor (quan l'objecte ChatRepository hagi acabat de llegir de la BBDD).
     */
    public void setChats(ArrayList<Chat> chats) {
        mChat.setValue(chats);
    }

    public User getUserById(String id){
        return userRepository.getUserById(id);
    }


    /* Mètode que crida a carregar dades dels usuaris */
    public void loadChatsFromRepository(String userID) {
        mChatRepository.loadUserChats(mChat.getValue(), userID);
    }


    public void updateChat(Chat currentChat) {
        mChatRepository.updateChat(currentChat.getId(), currentChat.getUser1().getID(), currentChat.getUser2().getID(), currentChat.getMessages());
    }

    public void addChat(String idUser1, String idUser2) {
        mChatRepository.addChat(idUser1, idUser2, new ArrayList<Message>());
    }

}

