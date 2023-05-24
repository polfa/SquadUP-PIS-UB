package edu.ub.pis.firebaseexamplepis.model;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.ub.pis.firebaseexamplepis.viewmodel.HomeActivityViewModel;

/** Classe que fa d'adaptador entre la base de dades (Cloud Firestore) i les classes del model
 * Segueix el patró de disseny Singleton.
 */
public class UserRepository {
    private static final String TAG = "Repository";

    /** Autoinstància, pel patró singleton */
    private static final UserRepository mInstance = new UserRepository();

    /** Referència a la Base de Dades */
    private FirebaseFirestore mDb;

    private ArrayList<User> userList = new ArrayList<>();

    /** Definició de listener (interficie),
     *  per escoltar quan s'hagin acabat de llegir els usuaris de la BBDD */
    public interface OnLoadUsersListener {
        void onLoadUsers(ArrayList<User> users);
    }

    public ArrayList<OnLoadUsersListener> mOnLoadUsersListeners = new ArrayList<>();

    /** Definició de listener (interficie)
     * per poder escoltar quan s'hagi acabat de llegir la Url de la foto de perfil
     * d'un usuari concret */
    public interface OnLoadUserPictureUrlListener {
        void OnLoadUserPictureUrl(String pictureUrl);
    }

    public OnLoadUserPictureUrlListener mOnLoadUserPictureUrlListener;

    /**
     * Constructor privat per a forçar la instanciació amb getInstance(),
     * com marca el patró de disseny Singleton class
     */
    private UserRepository() {
        mDb = FirebaseFirestore.getInstance();
    }

    /**
     * Retorna aqusta instancia singleton
     * @return
     */
    public static UserRepository getInstance() {
        return mInstance;
    }

    /**
     * Afegir un listener de la operació OnLoadUsersListener.
     * Pot haver-n'hi només un. Fem llista, com a exemple, per demostrar la flexibilitat
     * d'aquest disseny.
     * @param listener
     */
    public void addOnLoadUsersListener(OnLoadUsersListener listener) {
        mOnLoadUsersListeners.add(listener);
    }

    /**
     * Setejem un listener de la operació OnLoadUserPictureUrlListener.
     * En aquest cas, no és una llista de listeners. Només deixem haver-n'hi un,
     * també a tall d'exemple.
     * @param listener
     */
    public void setOnLoadUserPictureListener(OnLoadUserPictureUrlListener listener) {
        mOnLoadUserPictureUrlListener = listener;
    }

    /**
     * Mètode que llegeix els usuaris. Vindrà cridat des de fora i quan acabi,
     * avisarà sempre als listeners, invocant el seu OnLoadUsers.
     */
    public void loadUsers(ArrayList<User> users){
        users.clear();
        mDb.collection("users")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            ArrayList<String> friends = (ArrayList<String>) document.get("friends");
                            if (friends == null){
                                friends = new ArrayList<>();
                            }
                            User user = new User(
                                document.getId(), // ID = Email
                                document.getString("nickname"),
                                document.getString("descripcio"),
                                document.getString("picture_url"),
                                document.getString("mail"),
                                document.getString("gameImageId"),
                                document.getString("rankImageId"),
                                    friends);
                            users.add(user);

                        }
                        userList = users;
                        /* Callback listeners */
                        for (OnLoadUsersListener l: mOnLoadUsersListeners) {
                            l.onLoadUsers(users);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
    }

    public void loadUserFriends(ArrayList<User> users, ArrayList<String> friends){
        users.clear();
        mDb.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                ArrayList<String> friends = (ArrayList<String>) document.get("friends");
                                User user = new User(
                                        document.getId(), // ID = Email
                                        document.getString("nickname"),
                                        document.getString("descripcio"),
                                        document.getString("picture_url"),
                                        document.getString("mail"),
                                        document.getString("gameImageId"),
                                        document.getString("rankImageId"),
                                        friends);
                                if (friends != null) {
                                    if (friends.contains(user.getID())) {
                                        users.add(user);
                                    }
                                }

                            }
                            userList = users;
                            /* Callback listeners */
                            for (OnLoadUsersListener l: mOnLoadUsersListeners) {
                                l.onLoadUsers(users);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public User getUserById(String userID){
        if (!userID.isEmpty() && userID.charAt(0) == ' '){
            userID = userID.substring(1);
        }
        for (User u: userList) {
            String uID;
            if (u.getID() != null && u.getID().charAt(0) == ' '){
                uID = u.getID().substring(1);
            }else{
                uID = u.getID();
            }
            if (userID.equals(uID)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Mètode que llegeix la Url d'una foto de perfil d'un usuari indicat pel seu
     * email. Vindrà cridat des de fora i quan acabi, avisarà sempre al listener,
     * invocant el seu OnLoadUserPictureUrl.
     */
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
     * @param email
     * @param Nickname
     * @param descripcio
     * @param gameImageId
     * @param rankImageId
     */
    public void addUser(
        String email,
        String Nickname,
        String descripcio,
        String gameImageId,
        String rankImageId,
        ArrayList<String> friends
    ) {
         // Obtenir informació personal de l'usuari
        Map<String, Object> signedUpUser = new HashMap<>();
        signedUpUser.put("nickname",Nickname);
        signedUpUser.put("descripcio",descripcio);
        signedUpUser.put("picture_url",null);
        signedUpUser.put("mail",email);
        signedUpUser.put("gameImageId",gameImageId);
        signedUpUser.put("rankImageId",rankImageId);
        signedUpUser.put("friends", friends);

        // Afegir-la a la base de dades
        mDb.collection("users").document(email).set(signedUpUser)
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

    /**
     * Mètode que guarda la Url d'una foto de perfil que un usuari hagi pujat
     * des de la HomeActivity a la BBDD. Concretament, es cridat pel HomeActivityViewModel.
     * @param userId
     * @param pictureUrl
     */
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

    public ArrayList<User> getUserList() {
        return userList;
    }
}
