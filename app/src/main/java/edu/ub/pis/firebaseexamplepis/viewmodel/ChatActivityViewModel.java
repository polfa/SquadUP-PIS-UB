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
import java.util.Map;

import edu.ub.pis.firebaseexamplepis.model.Chat;
import edu.ub.pis.firebaseexamplepis.model.ChatRepository;
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

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    /* Repositori (base de dades) dels usuaris */
    private ChatRepository mChatRepository; // On es manté la informació dels usuaris

    /* Atributs auxiliars */
    private FirebaseStorage mStorage; // Per pujar fitxers grans (fotos) i accedir-hi


    public ChatActivityViewModel(Application application) {
        super(application);

        // Instancia els atributs
        userRepository = UserRepository.getInstance();
        mChat = new MutableLiveData<>(new ArrayList<>());
        mPictureUrl = new MutableLiveData<>();
        mHidPosition = new MutableLiveData<>();
        mChatRepository = ChatRepository.getInstance();
        mStorage = FirebaseStorage.getInstance();

        // Quan s'acabin de llegir de la BBDD els usuaris, el ViewModel ha d'actualitzar
        // l'observable mChats. I com que la RecyclerView de la HomeChatsActivity està observant aquesta
        // mateixa variable (mChats), també se n'enterarà
        mChatRepository.addOnLoadChatsListener(new ChatRepository.OnLoadChatsListener() {
            @Override
            public void onLoadChats(ArrayList<Chat> chats) {
               // chats = ordenarChatsPerData(chats);
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

    /*
     * Retorna els usuaris perquè la HomeChatsActivity pugui subscriure-hi l'observable.
     */
    public LiveData<ArrayList<Chat>> getChats() {
        return mChat;
    }




    /*
     * Retorna el LiveData de la URL de la foto per a què HomeChatsActivity
     * pugui subscriure-hi l'observable.
     */
    public LiveData<String> getPictureUrl() {
        return mPictureUrl;
    }

    /*
     * Retorna el LiveData de la URL de la foto per a què HomeChatsActivity
     * pugui subscriure-hi l'observable.
     */
    public LiveData<Integer> getHidPosition() {
        return mHidPosition;
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


    /*
     * Mètode cridat per l'Intent de la captura de camera al HomeChatsActivity,
     * que puja a FireStorage la foto que aquell Intent implicit hagi fet.
     */
    public void setPictureUrlOfUser(String userId, Uri imageUri) {
        // Sejetar una foto d'usuari implica:
        // 1. Pujar-la a Firebase Storage (ho fa aquest mètode)
        // 2. Setejar la URL de la imatge com un dels camps de l'usuari a la base de dades
        //    (es delega al DatabaseAdapter.setPictureUrlOfUser)

        StorageReference storageRef = mStorage.getReference();
        StorageReference fileRef = storageRef.child("uploads")
            .child(imageUri.getLastPathSegment());

        // Crea una tasca de pujada de fitxer a FileStorage
        UploadTask uploadTask = fileRef.putFile(imageUri);

        // Listener per la pujada
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");
            }
        });

        // La tasca en si: ves fent-la (pujant) i fins que s'hagi completat (onCompleteListener).
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    // Continue with the task to get the download URL
                    return fileRef.getDownloadUrl();
                } else {
                    throw task.getException();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete (@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri uploadUrl = task.getResult();
                    // un cop pujat, passa-li la URL de la imatge a l'adapter de
                    // la Base de Dades per a que l'associï a l'usuari
                    Log.d(TAG, "DownloadTask: " + uploadUrl.toString());
                    mChatRepository.setPictureUrlOfUser(userId, uploadUrl.toString());
                    mPictureUrl.setValue(uploadUrl.toString());
                }
            }
        });
    }

    /* Mètode que crida a carregar dades dels usuaris */
    public void loadChatsFromRepository(String userID) {
        mChatRepository.loadUserChats(mChat.getValue(), userID);
    }

    public void loadChatByIdFromRepository(String chatId) {
        mChatRepository.loadUserChats(mChat.getValue(), chatId);
    }






    /* Mètode que crida a carregar la foto d'un usuari entre els usuaris */
    public void loadPictureOfUser(String userId) {
        mChatRepository.loadPictureOfUser(userId);
    }

    /*
     * Mètode que esborra un usuari de la llista d'usuaris, donada una posició en
     * la llista. La posició ve del ChatCardAdapter, que es torna a la HomeChatsActivity
     * i aquesta crida aquest mètode, després que el HomeChatsActivityViewModel hagi esborrat
     * l'usuari en qüestió de mChats.
     */
    public void removeChatFromHome(int position) {
        mChat.getValue().remove(position);
    }

    public void updateChat(Chat currentChat) {
        mChatRepository.updateChat(currentChat.getId(), currentChat.getUser1().getID(), currentChat.getUser2().getID(), currentChat.getMessages());
    }

    public void addChat(String idUser1, String idUser2) {
        mChatRepository.addChat(idUser1, idUser2, new ArrayList<Message>());
    }

    /*
    private ArrayList<Chat> ordenarChatsPerData(ArrayList<Chat> chatList){
        Collections.sort(chatList, new Comparator<Chat>() {
            public int compare(Chat c1, Chat c2) {
                return c1.getStartTime().compareTo(c2.getStartTime());
            }
        });

        return chatList;
    }*/
}

