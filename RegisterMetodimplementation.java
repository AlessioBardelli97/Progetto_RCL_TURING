import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

class RegisterMetodimplementation
        extends UnicastRemoteObject implements RegisterMethod {

    private final HashMap<String, User> listRegisteredUsers;

    RegisterMetodimplementation(HashMap<String,User> users) throws RemoteException {

        super();
        this.listRegisteredUsers = users;
    }

    /**
     * @param userName nome dell'utente che si vuole registrare
     * @param password password dell'utente che si vuole registarre
     * @return {@code result//__//ok} se la registrazione è avvenuta con successo
     * @throws {@code RemoteException}
     */
    @Override
    @SuppressWarnings({"RedundantThrows", "JavaDoc"})
    public Result register(String userName, String password) throws RemoteException {

        System.out.println("Register: name=" + userName + " password=" + password);

        synchronized (listRegisteredUsers) {

            if (listRegisteredUsers.containsKey(userName))
                return Result.FAIL();

            listRegisteredUsers.put(userName, new User(password));
            return Result.OK();
        }
    }
}
