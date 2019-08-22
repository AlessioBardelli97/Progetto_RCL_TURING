/**
 * Classe che implementa il formato di scambio di richieste,
 * tra il client e il server turing.
 *
 * Il formato delle richieste è il seguente: request//__//userName//__//[...],
 * la parte opzionale dipende dal tipo di richiesta da fare al server.
 */

class Request {

// ================= VARIABILI DI ISTANZA ========================================================= \\

    /** Stringa che contiene le informazione della richiesta. */
    private String infoRequest;

// ================= COSTRUTTORE ================================================================== \\

    Request(String info) { this.infoRequest = info; }

// ================= METODI DI ISTANZA ============================================================ \\

    /** Restituisce la stringa associata alla richiesta. */
    String getRequest() { return this.infoRequest; }

    /** Restituisce un array che contiene le informazioni della richiesta. */
    String[] getInfoSplitted() { return this.infoRequest.split("//__//"); }

// ================= METODI STATICI PER CREARE RICHIESTE DA SPEDIRE AL SERVER ===================== \\

    static Request makeLoginRequest(String userName, String password) {

        return new Request(
                String.format(
                        "login//__//%s//__//%s",
                        userName,
                        password
                )
        );
    }

    static Request makeLogoutRequest(String userName) {

        return new Request(
                String.format(
                        "logout//__//%s",
                        userName
                )
        );
    }

    static Request makeCreateRequest(String userName, String documentName, String sections) {

        return new Request(
                String.format(
                        "create//__//%s//__//%s//__//%s",
                        userName,
                        documentName,
                        sections
                )
        );
    }

    static Request makeShareRequest(String userName, String documentName, String destUserName) {

        return new Request(
                String.format(
                        "share//__//%s//__//%s//__//%s",
                        userName,
                        documentName,
                        destUserName
                )
        );
    }

    static Request makeShowRequest(String userName, String documentName) {

        return new Request(
                String.format(
                        "show//__//%s//__//%s",
                        userName,
                        documentName
                )
        );
    }

    static Request makeShowRequest(String userName, String documentName, String section) {

        return new Request(
                String.format(
                        "show//__//%s//__//%s//__//%s",
                        userName,
                        documentName,
                        section
                )
        );
    }

    static Request makeListRequest(String userName) {

        return new Request(
                String.format(
                        "list//__//%s",
                        userName
                )
        );
    }

    static Request makeEditRequest(String userName, String documentName, String section) {

        return new Request(
                String.format(
                        "edit//__//%s//__//%s//__//%s",
                        userName,
                        documentName,
                        section
                )
        );
    }

    static Request makeEndEditRequest(String userName, String documentName, String section) {

        return new Request(
                String.format(
                        "end-edit//__//%s//__//%s//__//%s",
                        userName,
                        documentName,
                        section
                )
        );
    }
}
