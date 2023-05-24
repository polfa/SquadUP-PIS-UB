package edu.ub.pis.firebaseexamplepis.viewmodel;
import android.app.Application;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.model.UserRepository;

public class HomeActivityViewModel extends AndroidViewModel
{
    private final String TAG = "HomeActivityViewModel";

    /* Elements observables del ViewModel */
    private final MutableLiveData<ArrayList<User>> mUsers; // Els usuaris que la RecyclerView mostra al home
    private final MutableLiveData<String> mPictureUrl; // URL de la foto de l'usuari logat
    private final MutableLiveData<Integer> mHidPosition;

    /* Repositori (base de dades) dels usuaris */
    private UserRepository mUserRepository; // On es manté la informació dels usuaris

    /* Atributs auxiliars */
    private FirebaseStorage mStorage; // Per pujar fitxers grans (fotos) i accedir-hi

    public HomeActivityViewModel(Application application) {
        super(application);

        // Instancia els atributs
        mUsers = new MutableLiveData<>(new ArrayList<>());
        mPictureUrl = new MutableLiveData<>();
        mHidPosition = new MutableLiveData<>();
        mUserRepository = UserRepository.getInstance();
        mStorage = FirebaseStorage.getInstance();

        // Quan s'acabin de llegir de la BBDD els usuaris, el ViewModel ha d'actualitzar
        // l'observable mUsers. I com que la RecyclerView de la HomeActivity està observant aquesta
        // mateixa variable (mUsers), també se n'enterarà
        mUserRepository.addOnLoadUsersListener(new UserRepository.OnLoadUsersListener() {
            @Override
            public void onLoadUsers(ArrayList<User> users) {
                HomeActivityViewModel.this.setUsers(users);
            }
        });

        // Quan s'acabi de llegir la URL de la foto de perfil de l'usuari logat, el ViewModel
        // actualitza també mPictureUrl, per a que la HomeActivity la mostri en l'ImageView
        // corresponent
        mUserRepository.setOnLoadUserPictureListener(new UserRepository.OnLoadUserPictureUrlListener() {
            @Override
            public void OnLoadUserPictureUrl(String pictureUrl) {
                // Log.d(TAG, "Loaded pictureUrl: " + pictureUrl);
                mPictureUrl.setValue(pictureUrl);
            }
        });
    }

    /*
     * Retorna els usuaris perquè la HomeActivity pugui subscriure-hi l'observable.
     */
    public LiveData<ArrayList<User>> getUsers() {
        return mUsers;
    }


    /*
     * Retorna el LiveData de la URL de la foto per a què HomeActivity
     * pugui subscriure-hi l'observable.
     */
    public LiveData<String> getPictureUrl() {
        return mPictureUrl;
    }


    /*
     * Mètode que serà invocat pel UserRepository.OnLoadUsersListener definit al
     * constructor (quan l'objecte UserRepository hagi acabat de llegir de la BBDD).
     */
    public void setUsers(ArrayList<User> users) {
        mUsers.setValue(users);
    }

    public User getUsersById(String id){
        return mUserRepository.getUserById(id);
    }



    /* Mètode que crida a carregar dades dels usuaris */
    public void loadUsersFromRepository() {
        mUserRepository.loadUsers(mUsers.getValue());
    }


    /*
     * Mètode que esborra un usuari de la llista d'usuaris, donada una posició en
     * la llista. La posició ve del UserCardAdapter, que es torna a la HomeActivity
     * i aquesta crida aquest mètode, després que el HomeActivityViewModel hagi esborrat
     * l'usuari en qüestió de mUsers.
     */
    public void removeUserFromHome(int position) {
        mUsers.getValue().remove(position);
    }


}

