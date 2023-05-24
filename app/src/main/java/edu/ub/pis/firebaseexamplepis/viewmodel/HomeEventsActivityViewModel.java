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
import java.util.Collections;
import java.util.Comparator;

import edu.ub.pis.firebaseexamplepis.model.Event;
import edu.ub.pis.firebaseexamplepis.model.EventRepository;
import edu.ub.pis.firebaseexamplepis.model.User;
import edu.ub.pis.firebaseexamplepis.model.UserRepository;

public class HomeEventsActivityViewModel extends AndroidViewModel
{
    private final String TAG = "HomeEventsActivityViewModel";

    private UserRepository userRepository;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /* Elements observables del ViewModel */
    private final MutableLiveData<ArrayList<Event>> mEvents; // Els events que la RecyclerView mostra al home

    /* Repositori (base de dades) dels usuaris */
    private EventRepository mEventRepository; // On es manté la informació dels usuaris



    public HomeEventsActivityViewModel(Application application) {
        super(application);

        // Instancia els atributs
        userRepository = UserRepository.getInstance();
        mEvents = new MutableLiveData<>(new ArrayList<>());
        mEventRepository = EventRepository.getInstance();

        // Quan s'acabin de llegir de la BBDD els usuaris, el ViewModel ha d'actualitzar
        // l'observable mEvents. I com que la RecyclerView de la HomeEventsActivity està observant aquesta
        // mateixa variable (mEvents), també se n'enterarà
        mEventRepository.addOnLoadEventsListener(new EventRepository.OnLoadEventsListener() {
            @Override
            public void onLoadEvents(ArrayList<Event> events) {
                events = ordenarEventsPerData(events);
                HomeEventsActivityViewModel.this.setEvents(events);
            }
        });

    }

    /*
     * Retorna els usuaris perquè la HomeEventsActivity pugui subscriure-hi l'observable.
     */
    public EventRepository getInstance(){
        return mEventRepository;
    }

    public User getUserById(){
        return userRepository.getUserById(mAuth.getCurrentUser().getEmail());
    }

    public LiveData<ArrayList<Event>> getEvents() {
        return mEvents;
    }




    /*
     * Mètode que serà invocat pel EventRepository.OnLoadEventsListener definit al
     * constructor (quan l'objecte EventRepository hagi acabat de llegir de la BBDD).
     */
    public void setEvents(ArrayList<Event> events) {
        mEvents.setValue(events);
    }

    /*
     * Mètode cridat per l'Intent de la captura de camera al HomeEventsActivity,
     * que puja a FireStorage la foto que aquell Intent implicit hagi fet.
     */


    /* Mètode que crida a carregar dades dels usuaris */
    public void loadEventsFromRepository() {
        mEventRepository.loadEvents(mEvents.getValue());
    }


    private ArrayList<Event> ordenarEventsPerData(ArrayList<Event> eventList){
        Collections.sort(eventList, new Comparator<Event>() {
            public int compare(Event e1, Event e2) {
                return e1.getStartTime().compareTo(e2.getStartTime());
            }
        });

        return eventList;
    }
}

