/**
 * Alessio Bardelli Mat. 544270
 */

import java.util.ArrayList;
import java.util.Map;

/**
 * Classe che modella le informazioni di un documento del server turing, tra cui:
 *
 * - il nome dell'utente che ha creato il documento
 * - il numero di sezioni che compongono il documento
 * - una lista che memorizza i nomi degli utenti collaboratori
 * - un array che per ogni sezione memorizza lo stato in cui è (STARTED o EDIT) e il nome dell'utente che la sta' editando
 *   se questa è in stato di edit.
 *
 * Le informazioni di addressing per la chat relativa al documento.
 */

class Document {

// ================= VARIABILI DI ISTANAZA =============================================================== \\

    private String creatorUser;
    private int numSections;
    private ArrayList<String> collaborators;
    private InfoSection[] infoSections;

    private String groupAddress;
    private Integer groupPort;

// ================= COSTRUTTORE ========================================================================= \\

    Document(String creatorUser, int numSections) {

        this.creatorUser = creatorUser;
        this.numSections = numSections;
        this.collaborators = new ArrayList<>();

        this.infoSections = new InfoSection[numSections];
        for (int i = 0; i < numSections; i++)
            this.infoSections[i] = new InfoSection();

        this.groupAddress = UtilsClass.multicastArray.getAddress();
        this.groupPort = UtilsClass.GROUP_MULTICAST_PORT;
    }

// ================= METODI DI ISTANZA =================================================================== \\

    /** @return la porta su cui è attivo il servizio di chat del documento. */
    Integer getGroupPort() { return this.groupPort; }

    /** @return una stringa che reppresenta l'indirizzo di multicast su cui è attivo il servizio di chat. */
    String getGroupAddress() { return this.groupAddress; }

    /** @return il numero di sezioni del documento. */
    int getNumSections() { return this.numSections; }

    /** @return il nome dell'utente che ha creato il documento. */
    String getCreatorUser() { return this.creatorUser; }

    /** @return un array che contiene tutti i nomi degli utente collaboratori al documento. */
    Object[] getCollaborators() { return this.collaborators.toArray(); }

    /** Imposta lo stato di STARTED del documento. */
    void setStarted(int section) {

        this.infoSections[section].state = State.Document.STARTED;
        this.infoSections[section].userNameEditing = null;
        this.infoSections[section].resetTimeStamp();
    }

    /** Imposta lo stato di EDIT del documento. */
    void setEdit(int section, String userName) {

        this.infoSections[section].state = State.Document.EDIT;
        this.infoSections[section].userNameEditing = userName;
        this.infoSections[section].setTimeStamp();
    }

    /** @return il nome dell'utente che sta' editando la sezione {@param section} del documento. */
    String getUserNameEditing(int section) { return this.infoSections[section].userNameEditing; }

    /** Aggiunge {@param userName} alla lista dei collaboratori. */
    void addCollaborators(String userName) { this.collaborators.add(userName); }

    /** @return {@code true} se la sezione {@param section} del documento è editabile. */
    boolean isEdit(int section) { return this.infoSections[section].state == State.Document.EDIT; }

    /** @return {@code true} se la sezione {@param section} del documento è in stato STARTED. */
    boolean isStarted(int section) { return this.infoSections[section].state == State.Document.STARTED; }

    /** @return {@code true} se il documento è editabile da {@param userName} */
    boolean isEditableFromUser(String userName) {
        return this.creatorUser.equals(userName) || this.collaborators.contains(userName);
    }

    long getTimeStamp(int section) { return this.infoSections[section].getTimeStamp(); }

    /**
     * Due documenti sono considerati uguali
     * se hanno lo stesso creatore e lo stesso numero di sezioni.
     */
    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Document) {

            Document doc = (Document) obj;
            return this.creatorUser.equals(doc.creatorUser) &&
                    this.numSections == doc.numSections;
        }

        return false;
    }

// ================= METODI STATICI ====================================================================== \\

    /**
     * @return la lista dei documenti editabili da {@param userName},
     * altrimenti {@code null}
     */
    static ArrayList<Map.Entry<String,Document>> getListOfDocumentsOfUser(String userName) {

        ArrayList<Map.Entry<String,Document>> documents = new ArrayList<>();

        for (Map.Entry<String, Document> entry : MainClassTuringServer.listDocuments.entrySet()) {

            Document document = entry.getValue();
            if (document.isEditableFromUser(userName))
                documents.add(entry);
        }

        if (documents.size() == 0)
            return null;

        return documents;
    }

// ================= INNER CLASS ========================================================================= \\

    /**
     * Classe che modella le informazioni di una sezione del documento, tra cui:
     * - lo stato in cui si trova la sezione (STARTED, EDIT)
     * - il nome dell'utente che sta' editando la sezione se questa è in stato di edit, null altrimenti
     * - tmpo di inizio dell'ultima modifica.
     */
    private static class InfoSection {

        private State.Document state;
        private String userNameEditing;
        private long timeStamp;

        InfoSection() {

            this.state = State.Document.STARTED;
            this.userNameEditing = null;
            resetTimeStamp();
        }

        void setTimeStamp() { this.timeStamp = System.currentTimeMillis(); }

        void resetTimeStamp() { this.timeStamp = Long.MAX_VALUE; }

        long getTimeStamp() { return this.timeStamp; }
    }
}
