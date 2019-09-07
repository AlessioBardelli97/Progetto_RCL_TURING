/**
 * Alessio Bardelli Mat. 544270
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaccia remota per la registarzione a TURING,
 * che espone il relativo metodo.
 */
public interface RegisterMethod extends Remote {

    /**
     * Metodo remoto invocato dal client,
     * e implementato nella clacsse RegisterMethodImplementation,
     * per la registrazione a turing.
     *
     * @param userName nome dell'utente che si vuole registrare a turing
     * @param password password dell'utente che si vuole registrare a turing
     * @return {@code Result.OK} se la registrazione dell'utente è avvenuta con successo
     */
    Result register(String userName, String password) throws RemoteException;

    /**
     * Metodo remoto per la registrazione al servizio di notifica,
     * quando si riceve un invito all'editing di un documento.
     * @param userName nome dell'utente che si vuole registrare al servizio
     * @param clientNotify oggetto remoto da utilizzare per notificare l'invito
     */
    void registerForCallback(String userName, Notify clientNotify) throws RemoteException;

    /**
     * Metodo remoto per la deregistrazione al servizio di notifica.
     * @param userName nome dell'utente che si vuole deregistrare al servizio
     */
    void unregisterFromCallback(String userName) throws RemoteException;
}
