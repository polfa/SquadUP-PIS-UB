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
    private final MutableLiveData<String> mPictureUrl; // URL de la foto de l'usuari logat
    private final MutableLiveData<Integer> mHidPosition;

    private UserRepository userRepository;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    /* Repositori (base de dades) dels usuaris */
    private GrupRepository mGrupRepository; // On es manté la informació dels usuaris

    /* Atributs auxiliars */
    private FirebaseStorage mStorage; // Per pujar fitxers grans (fotos) i accedir-hi

    public GrupActivityViewModel(Application application) {
        super(application);

        // Instancia els atributs
        userRepository = UserRepository.getInstance();
        mGrup = new MutableLiveData<>(new ArrayList<>());
        mPictureUrl = new MutableLiveData<>();
        mHidPosition = new MutableLiveData<>();
        mGrupRepository = GrupRepository.getInstance();
        mStorage = FirebaseStorage.getInstance();

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
        mGrupRepository.setOnLoadUserPictureListener(new GrupRepository.OnLoadUserPictureUrlListener() {
            @Override
            public void OnLoadUserPictureUrl(String pictureUrl) {
                // Log.d(TAG, "Loaded pictureUrl: " + pictureUrl);
                mPictureUrl.setValue(pictureUrl);
            }
        });
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
     * Retorna el LiveData de la URL de la foto per a què HomeGrupsActivity
     * pugui subscriure-hi l'observable.
     */
    public LiveData<String> getPictureUrl() {
        return mPictureUrl;
    }

    /*
     * Retorna el LiveData de la URL de la foto per a què HomeGrupsActivity
     * pugui subscriure-hi l'observable.
     */
    public LiveData<Integer> getHidPosition() {
        return mHidPosition;
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
                    mGrupRepository.setPictureUrlOfUser(userId, uploadUrl.toString());
                    mPictureUrl.setValue(uploadUrl.toString());
                }
            }
        });
    }

    /* Mètode que crida a carregar dades dels usuaris */
    public void loadGrupsFromRepository(String userID) {
        mGrupRepository.loadUserGrups(mGrup.getValue(), userID);
    }

    /* Mètode que crida a carregar la foto d'un usuari entre els usuaris */
    public void loadPictureOfUser(String userId) {
        mGrupRepository.loadPictureOfUser(userId);
    }

    /*
     * Mètode que esborra un usuari de la llista d'usuaris, donada una posició en
     * la llista. La posició ve del GrupCardAdapter, que es torna a la HomeGrupsActivity
     * i aquesta crida aquest mètode, després que el HomeGrupsActivityViewModel hagi esborrat
     * l'usuari en qüestió de mGrups.
     */
    public void removeGrupFromHome(int position) {
        mGrup.getValue().remove(position);
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
