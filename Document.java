import java.util.ArrayList;
import java.util.Map;

class Document {

    private String creatorUser;
    private int numSections;
    private ArrayList<String> collaborators;
    private State.Document[] sectionsStates;
    private String[] usersNameEditing;

    private String groupAddress;
    private Integer groupPort;

    Document(String creatorUser, int numSections) {

        this.creatorUser = creatorUser;
        this.numSections = numSections;
        this.collaborators = new ArrayList<>();

        this.usersNameEditing = new String[numSections];
        this.sectionsStates = new State.Document[numSections];
        for (int i = 0; i < numSections; i++) {

            this.sectionsStates[i] = State.Document.STARTED;
            this.usersNameEditing[i] = null;
        }

        this.groupAddress = UtilsClass.multicastArray.getAddress();
        this.groupPort = UtilsClass.GROUP_MULTICAST_PORT;
    }

    void addCollaborators(String userName) { this.collaborators.add(userName); }

    int getNumSections() { return this.numSections; }

    String getCreatorUser() { return this.creatorUser; }

    String[] getCollaborators() { return this.collaborators.toArray(new String[collaborators.size()]); }

    boolean isEdit(int section) { return this.sectionsStates[section] == State.Document.EDIT; }

    boolean isStarted(int section) { return this.sectionsStates[section] == State.Document.STARTED; }

    void setStarted(int section) { this.sectionsStates[section] = State.Document.STARTED; }

    void setEdit(int section) { this.sectionsStates[section] = State.Document.EDIT; }

    void setUserNameEditing(String userNameEditing, int section) {
        this.usersNameEditing[section] = userNameEditing;
    }

    String getUserNameEditing(int section) { return this.usersNameEditing[section]; }

    boolean isEditableFromUser(String userName) {
        return this.creatorUser.equals(userName) || this.collaborators.contains(userName);
    }

    Integer getGroupPort() { return this.groupPort; }

    String getGroupAddress() { return this.groupAddress; }

    static ArrayList<Map.Entry<String,Document>> getListOfDocumentsOfUser(String userName) {

        ArrayList<Map.Entry<String,Document>> documents = new ArrayList<>();

        for (Map.Entry<String, Document> entry : MainClassTuringServer.listDocuments.entrySet())
            if (entry.getValue().creatorUser.equals(userName) ||
                    entry.getValue().collaborators.contains(userName))
                documents.add(entry);

        if (documents.size() > 0)
            return documents;

        else return null;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Document) {

            Document doc = (Document) obj;
            return this.creatorUser.equals(doc.creatorUser) &&
                    this.numSections == doc.numSections;
        }

        return false;
    }
}
