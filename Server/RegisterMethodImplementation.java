import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

/**
 * Classe che implementa il metodo per la registrazione a turing,
 * e la registrazione al servizio di notifica degli inviti di editing.
 */

@SuppressWarnings({"RedundantThrows"})

class RegisterMethodImplementation
        extends UnicastRemoteObject implements RegisterMethod {

// ================= VARIABILI DI ISTANZA ======================================================= \\

    /** Riferimento alla lista degli utenti registrati. */
    private final HashMap<String, User> listRegisteredUsers;

    /**
     * Per ogni utente che accede a TURING,
     * si memorizza l'oggetto remoto,
     * per notificare l'invito all'editing di un documento.
     */
    private static HashMap<String, Notify> clients;

// ================= COSTRUTTORE ================================================================ \\

    RegisterMethodImplementation(HashMap<String,User> users) throws RemoteException {

        super();
        clients = new HashMap<>();
        this.listRegisteredUsers = users;
    }

// ================= METODI DI ISTANZA ========================================================== \\

    @Override
    public Result register(String userName, String password) throws RemoteException {

        System.out.println("Register: name=" + userName + " password=" + password);

        synchronized (listRegisteredUsers) {

            if (listRegisteredUsers.containsKey(userName))
                return Result.FAIL();

            listRegisteredUsers.put(userName, new User(password));
            return Result.OK();
        }
    }

    @Override
    public void registerForCallback(String userName, Notify notifyObject) throws RemoteException {

        clients.put(userName, notifyObject);
    }

    @Override
    public void unregisterFromCallback(String userName) throws RemoteException {

        clients.remove(userName);
    }

// ================= METODI STATICI ============================================================= \\

    /**
     * Metodo invocato da un utente che è online per notificare l'invito all'editing di un documento.
     * @param userName nome dell'utente a cui si deve notificare l'invito di editing
     * @param document nome del documento a cui si è invitati all'editing
     */
    static void notifyOnlineInvite(String userName, String document) {

        Notify notifyObject = clients.get(userName);

        try { notifyObject.notifyInvite(document); }
        catch (RemoteException ignore) {}
    }
}