/**
 * Alessio Bardelli Mat. 544270
 */

import java.util.HashMap;

/**
 * Classe che contiene il task per il thread cleaner.
 */

class Cleaner extends Thread {

    /** Riferimento alla lista dei documenti creati dagli utenti. */
    private final HashMap<String, Document> listDocuments;

    /** Riferimento alla lista degli utenti registrati. */
    private final HashMap<String, User> listRegisteredUsers;

    Cleaner(HashMap<String, Document> ld, HashMap<String, User> lru) {

        this.listDocuments = ld;
        this.listRegisteredUsers = lru;
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {

        while (true) {

            try { Thread.sleep(UtilsClass.SLEEPING_TIME); }
            catch (InterruptedException ex) {
                System.out.println("Errore: thread cleaner risvegliato durante la sleep\n");
            }

            synchronized (listDocuments) {

                for (Document document : listDocuments.values()) {

                    int numSections = document.getNumSections();

                    for (int i = 0; i < numSections; i++) {

                        long timeStamp = document.getTimeStamp(i);

                        if (System.currentTimeMillis() - timeStamp > UtilsClass.MAX_EDIT_TIME) {

                            String userName = document.getUserNameEditing(i);

                            synchronized (listRegisteredUsers) {

                                User user = listRegisteredUsers.get(userName);

                                user.setStarted();
                                document.setStarted(i);
                            }
                        }
                    }
                }
            }

            synchronized (listRegisteredUsers) {

                for (User user : listRegisteredUsers.values()) {

                    long timeStamp = user.getTimeStamp();

                    if (System.currentTimeMillis() - timeStamp > UtilsClass.MAX_LOGIN_TIME)
                        user.setStarted();
                }
            }
        }
    }
}
