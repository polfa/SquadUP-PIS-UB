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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** Classe que fa d'adaptador entre la base de dades (Cloud Firestore) i les classes del model
 * Segueix el patró de disseny Singleton.
 */
public class EventRepository {
    private static final String TAG = "Event Repository";

    /** Autoinstància, pel patró singleton */
    private static final EventRepository mInstance = new EventRepository();

    /** Referència a la Base de Dades */
    private FirebaseFirestore mDb;

    /** Definició de listener (interficie),
     *  per escoltar quan s'hagin acabat de llegir els usuaris de la BBDD */
    public interface OnLoadEventsListener {
        void onLoadEvents(ArrayList<Event> events);
    }

    public ArrayList<OnLoadEventsListener> mOnLoadEventsListeners = new ArrayList<>();

    /** Definició de listener (interficie)
     * per poder escoltar quan s'hagi acabat de llegir la Url de la foto de perfil
     * d'un usuari concret */
    public interface OnLoadUserPictureUrlListener {
        void  OnLoadUserPictureUrl(String pictureUrl);
    }

    public OnLoadUserPictureUrlListener mOnLoadUserPictureUrlListener;

    /**
     * Constructor privat per a forçar la instanciació amb getInstance(),
     * com marca el patró de disseny Singleton class
     */
    private EventRepository() {
        mDb = FirebaseFirestore.getInstance();
    }

    /**
     * Retorna aqusta instancia singleton
     * @return
     */
    public static EventRepository getInstance() {
        return mInstance;
    }

    /**
     * Afegir un listener de la operació OnLoadUsersListener.
     * Pot haver-n'hi només un. Fem llista, com a exemple, per demostrar la flexibilitat
     * d'aquest disseny.
     * @param listener
     */
    public void addOnLoadEventsListener(OnLoadEventsListener listener) {
        mOnLoadEventsListeners.add(listener);
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
    public void loadEvents(ArrayList<Event> events){
        events.clear();
        mDb.collection("events")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Date date = new Date();
                            if (document.getTimestamp("startTime").toDate().getTime() >= date.getTime()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Event event = new Event(
                                        document.getString("userID"),
                                        document.toString(), // ID
                                        document.getString("description"),
                                        document.getString("gameImageId"),
                                        document.getString("rankImageId"),
                                        document.getTimestamp("startTime"),
                                        document.getLong("maxMembers"),
                                        document.getString("members")
                                );
                                events.add(event);
                            }
                        }
                        /* Callback listeners */
                        for (OnLoadEventsListener l: mOnLoadEventsListeners) {
                            l.onLoadEvents(events);
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
     * @param userID
     * @param description
     * @param gameImageID
     * @param rankImageId
     * @param startTime
     */
    public void addEvent(
        String userID,
        String description,
        String gameImageID,
        String rankImageId,
        com.google.firebase.Timestamp startTime
    ) {
         // Obtenir informació personal de l'usuari
        Map<String, Object> newEvent = new HashMap<>();
        newEvent.put("userID", userID);
        newEvent.put("description", description);
        newEvent.put("gameImageID", gameImageID);
        newEvent.put("rankImageId", rankImageId);
        newEvent.put("startTime", startTime);

        // Afegir-la a la base de dades
        mDb.collection("events").document().set(newEvent)
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
