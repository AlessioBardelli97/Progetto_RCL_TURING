/**
 * Alessio Bardelli Mat. 544270
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Notify extends Remote {

    /**
     * Metodo remoto usato per notificare ad un utente online,
     * che ha ricevuto un invito di editing.
     * @param document nome del documento a cui si è stati invitati all'editing
     */
    void notifyInvite(String document) throws RemoteException;
}
