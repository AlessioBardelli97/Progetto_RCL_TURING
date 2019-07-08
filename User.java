class User {

    private String password;
    private State.User state;
    private String editingDocumentName;

    User(String password) { this.password = password; this.state = State.User.STARTED; this.editingDocumentName = null; }

    String getPassword() { return this.password; }

    void setStarted() { this.state = State.User.STARTED; }

    void setLogged() { this.state = State.User.LOGGED; }

    void setEdit() { this.state = State.User.EDIT; }

    boolean isStarted() { return this.state == State.User.STARTED; }

    boolean isLogged() { return this.state == State.User.LOGGED; }

    boolean isEdit() { return this.state == State.User.EDIT; }

    String getEditingDocumentName() { return this.editingDocumentName; }

    void setEditingDocument(String editingDocumentName) { this.editingDocumentName = editingDocumentName; }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof User) {

            User user = (User) obj;
            return this.password.equals(user.password);
        }

        return false;
    }
}
