import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@SuppressWarnings("RedundantThrows")

class NotifyImplementation
        extends UnicastRemoteObject implements Notify {

// ================= VARIABILI DI ISTANZA ================================================================== \\

    private static final long serialVersionUID = 1L;

// ================= COSTRUTTORE =========================================================================== \\

    NotifyImplementation() throws RemoteException { super(); }

// ================= METODI DI ISTANZA ===================================================================== \\

    @Override
    public void notifyInvite(String document) throws RemoteException {

        System.out.println("Hai ricevuto un invito per l'editing del documento " + document + "\n");
        System.out.print("$ ");
    }
}
