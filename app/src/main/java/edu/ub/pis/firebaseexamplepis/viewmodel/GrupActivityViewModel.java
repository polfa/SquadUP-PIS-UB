package edu.ub.pis.firebaseexamplepis.viewmodel;

import android.app.Application;
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

import java.util.ArrayList;

import edu.ub.pis.firebaseexamplepis.model.Grup;
import edu.ub.pis.firebaseexamplepis.model.GrupRepository;
import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.model.UserRepository;

public class GrupActivityViewModel extends AndroidViewModel
{
    private final String TAG = "GrupListActivityViewModel";

    /* Elements observables del ViewModel */
    private final MutableLiveData<ArrayList<Grup>> mGrup; // Els Grup que la RecyclerView mostra al home

    private UserRepository userRepository;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    /* Repositori (base de dades) dels usuaris */
    private GrupRepository mGrupRepository; // On es manté la informació dels usuaris

    /* Atributs auxiliars */

    public GrupActivityViewModel(Application application) {
        super(application);

        // Instancia els atributs
        userRepository = UserRepository.getInstance();
        mGrup = new MutableLiveData<>(new ArrayList<>());
        mGrupRepository = GrupRepository.getInstance();

        // Quan s'acabin de llegir de la BBDD els usuaris, el ViewModel ha d'actualitzar
        // l'observable mGrups. I com que la RecyclerView de la HomeGrupsActivity està observant aquesta
        // mateixa variable (mGrups), també se n'enterarà
        mGrupRepository.addOnLoadGrupsListener(new GrupRepository.OnLoadGrupsListener() {
            @Override
            public void onLoadGrups(ArrayList<Grup> grups) {
               // grups = ordenarGrupsPerData(grups);
                GrupActivityViewModel.this.setGrups(grups);
            }
        });

        // Quan s'acabi de llegir la URL de la foto de perfil de l'usuari logat, el ViewModel
        // actualitza també mPictureUrl, per a que la HomeGrupsActivity la mostri en l'ImageView
        // corresponent
    }
    public User getUserById(){
        return userRepository.getUserById(mAuth.getCurrentUser().getEmail());
    }
    /*
     * Retorna els usuaris perquè la HomeGrupsActivity pugui subscriure-hi l'observable.
     */
    public LiveData<ArrayList<Grup>> getGrups() {
        return mGrup;
    }

    /*
     * Mètode que serà invocat pel GrupRepository.OnLoadGrupsListener definit al
     * constructor (quan l'objecte GrupRepository hagi acabat de llegir de la BBDD).
     */
    public void setGrups(ArrayList<Grup> grups) {
        mGrup.setValue(grups);
    }

    /*
     * Mètode cridat per l'Intent de la captura de camera al HomeGrupsActivity,
     * que puja a FireStorage la foto que aquell Intent implicit hagi fet.
     */

    /* Mètode que crida a carregar dades dels usuaris */
    public void loadGrupsFromRepository(String userID) {
        mGrupRepository.loadUserGrups(mGrup.getValue(), userID);
    }

    /* Mètode que crida a carregar la foto d'un usuari entre els usuaris */
    public void loadPictureOfUser(String userId) {
        mGrupRepository.loadPictureOfUser(userId);
    }

    /*
    private ArrayList<Grup> ordenarGrupsPerData(ArrayList<Grup> grupList){
        Collections.sort(grupList, new Comparator<Grup>() {
            public int compare(Grup c1, Grup c2) {
                return c1.getStartTime().compareTo(c2.getStartTime());
            }
        });

        return grupList;
    }*/
}

