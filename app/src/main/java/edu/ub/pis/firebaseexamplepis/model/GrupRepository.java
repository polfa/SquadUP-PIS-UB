package edu.ub.pis.firebaseexamplepis.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GrupRepository {
    private static final String TAG = "Grup Repository";

    /** Autoinstància, pel patró singleton */
    private static final GrupRepository mInstance = new GrupRepository();

    /** Referència a la Base de Dades */
    private FirebaseFirestore mDb;

    private ArrayList<Grup> grupList = new ArrayList<>();


    /** Definició de listener (interficie),
     *  per escoltar quan s'hagin acabat de llegir els usuaris de la BBDD */
    public interface OnLoadGrupsListener {
        void onLoadGrups(ArrayList<Grup> Grup);
    }

    public ArrayList<GrupRepository.OnLoadGrupsListener> mOnLoadGrupsListeners = new ArrayList<>();

    /** Definició de listener (interficie)
     * per poder escoltar quan s'hagi acabat de llegir la Url de la foto de perfil
     * d'un usuari concret */
    public interface OnLoadUserPictureUrlListener {
        void  OnLoadUserPictureUrl(String pictureUrl);
    }

    public GrupRepository.OnLoadUserPictureUrlListener mOnLoadUserPictureUrlListener;

    /**
     * Constructor privat per a forçar la instanciació amb getInstance(),
     * com marca el patró de disseny Singleton class
     */
    private GrupRepository() {
        mDb = FirebaseFirestore.getInstance();
    }

    /**
     * Retorna aqusta instancia singleton
     * @return
     */
    public static GrupRepository getInstance() {
        return mInstance;
    }

    /**
     * Afegir un listener de la operació OnLoadUsersListener.
     * Pot haver-n'hi només un. Fem llista, com a exemple, per demostrar la flexibilitat
     * d'aquest disseny.
     * @param listener
     */
    public void addOnLoadGrupsListener(GrupRepository.OnLoadGrupsListener listener) {
        mOnLoadGrupsListeners.add(listener);
    }

    /**
     * Setejem un listener de la operació OnLoadUserPictureUrlListener.
     * En aquest cas, no és una llista de listeners. Només deixem haver-n'hi un,
     * també a tall d'exemple.
     * @param listener
     */
    public void setOnLoadUserPictureListener(GrupRepository.OnLoadUserPictureUrlListener listener) {
        mOnLoadUserPictureUrlListener = listener;
    }

    /**
     * Mètode que llegeix els usuaris. Vindrà cridat des de fora i quan acabi,
     * avisarà sempre als listeners, invocant el seu OnLoadUsers.
     */
    public void loadUserGrups(ArrayList<Grup> grups, String userID){
        grups.clear();
        mDb.collection("grups")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<Message> messages= new ArrayList<>();
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                ArrayList<Map<String,Object>> messageData = (ArrayList<Map<String,Object>>) document.get("messages");
                                for (int i = 0; i < messageData.size(); i++) {
                                    Map<String, Object> messageMap = messageData.get(i);
                                    String text = (String) messageMap.get("text");
                                    Timestamp time = (Timestamp) messageMap.get("date");
                                    boolean read = (boolean) messageMap.get("read");
                                    // Otros atributos del mensaje que necesites obtener

                                    Message message = new Message("",text, time.toDate(), read);
                                    // Establece los otros atributos del mensaje si los hay

                                    messages.add(message);
                                }
                                Grup grup = new Grup(
                                        document.getId(),
                                        document.getString("grupName"),
                                        (ArrayList<String>) document.get("users"),
                                        messages,
                                        document.getString("imageURL"),
                                        document.getString("description")
                                );
                                if (userID.equals(document.getString("idUser1") )|| userID.equals(document.getString("idUser1"))){
                                    grups.add(grup);
                                }
                            }
                            grupList = grups;
                            /* Callback listeners */
                            for (GrupRepository.OnLoadGrupsListener l: mOnLoadGrupsListeners) {
                                l.onLoadGrups(grups);
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
     * @param messages
     * @param user1_ID
     * @param user2_ID
     */
    public void addGrup(
            String user1_ID,
            String user2_ID,
            ArrayList<Message> messages
    ) {
        // Obtenir informació personal de l'usuari
        Map<String, Object> newGrup = new HashMap<>();
        newGrup.put("idUser1", user1_ID);
        newGrup.put("idUser2", user2_ID);
        newGrup.put("messages", objectToMapArray(messages));


        // Afegir-la a la base de dades
        mDb.collection("grups").document().set(newGrup)
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

    public ArrayList<Map<String,Object>> objectToMapArray(ArrayList<Message> list){
        ArrayList<Map<String,Object>> arrayMap = new ArrayList<>();
        for (Message m: list){
            Map<String,Object> aux = new HashMap<>();
            String text = m.getText();
            Date date = m.getTime();
            boolean read = m.read();
            aux.put("text", text);
            aux.put("date", date);
            aux.put("read", read);
            arrayMap.add(aux);
        }
        return arrayMap;
    }

    public void updateGrup(String id, String groupName, ArrayList<String> users, ArrayList<Message> messages, String imageURL, String description)
        {

            DocumentReference chatRef = mDb.collection("grups").document(id);

            // Obtenir informació personal de l'usuari
            Map<String, Object> newGrup = new HashMap<>();
            newGrup.put("grupName", groupName);
            newGrup.put("users", users);
            newGrup.put("messages", objectToMapArray(messages));
            newGrup.put("imageURL", imageURL);
            newGrup.put("description", description);

            chatRef.set(newGrup).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Chat update completed successfully");
                    } else {
                        Log.d(TAG, "Chat update failed");
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

