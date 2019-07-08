import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaccia remota per la registarzione a TURING,
 * che espone il nome del servizio,
 * e il relativo metodo per registrarsi
 */
public interface RegisterMethod extends Remote {

    Result register(String userName, String password) throws RemoteException;
}
