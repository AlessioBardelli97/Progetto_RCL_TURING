/**
 * Alessio Bardelli Mat. 544270
 */

import java.util.ArrayList;

/**
 * Classe che modella le informazioni di un utente.
 * Memorizza quindi, per ogni utente:
 *   - la password
 *   - lo stato in cui si trova in un dato istante (STARTED, LOGGED, EDIT)
 *   - una lista che contiene le notifiche da mandare all'utente quando torna in stato di LOGGED
 */

class User {

// ================= VARIABILI DI ISTANZA =============================================== \\

    private String password;
    private State.User state;
    private ArrayList<String> notYetAcked;
    private long timeStamp;

// ================= COSTRUTTORE ======================================================== \\

    User(String password) {

        this.password = password;
        this.state = State.User.STARTED;
        this.notYetAcked = new ArrayList<>();
        this.timeStamp = Long.MAX_VALUE;
    }

// ================= METODI DI ISTANZA ================================================== \\

    /** @return la password dell'utente. */
    String getPassword() { return this.password; }

    /** Imposta lo stato di STARTED dell'utente. */
    void setStarted() {
        this.state = State.User.STARTED;
        this.timeStamp = Long.MAX_VALUE;
    }

    /** Imposta lo stato di LOGGED dell'utente. */
    void setLogged() {
        this.state = State.User.LOGGED;
        this.timeStamp = System.currentTimeMillis();
    }

    /** Imposta lo stato di EDIT dell'utente. */
    void setEdit() {
        this.state = State.User.EDIT;
        this.timeStamp = Long.MAX_VALUE;
    }

    /** @return {@code true} se l'utente è in stato di STARTED. */
    boolean isStarted() { return this.state == State.User.STARTED; }

    /** @return {@code true} se l'utente è in stato di LOGGED. */
    boolean isLogged() { return this.state == State.User.LOGGED; }

    /** @return {@code true} se l'utente è in stato di EDIT. */
    boolean isEdit() { return this.state == State.User.EDIT; }

    /** @return una lista che contiene le notifiche da mandare all'utente quando torna in stato di LOGGED. */
    Object[] getNotYetAcked() { return this.notYetAcked.toArray(); }

    /** Svuota la lista che contiene le notifiche da mandare all'utente quando torna in stato di LOGGED. */
    void cleanNotYetAck() { this.notYetAcked.clear(); }

    /** Notifica l'invito di editing per il documento {@param documentName}. */
    void notifyInvited(String userName, String documentName) {

        if (this.isStarted() || this.isEdit())
            notYetAcked.add("Hai ricevuto un invito per l'editing del documento " + documentName);

        else RegisterMethodImplementation.notifyOnlineInvite(userName, documentName);
    }

    long getTimeStamp() { return this.timeStamp; }

    /**
     * Due utenti sono considerati uguali
     * se hanno la stessa password.
     */
    @Override
    public boolean equals(Object obj) {

        if (obj instanceof User) {

            User user = (User) obj;
            return this.password.equals(user.password);
        }

        return false;
    }
}
