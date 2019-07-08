import java.text.SimpleDateFormat;
import java.util.Date;

class Request {

    private String info;

    Request(String info) { this.info = info; }

    String getInfo() { return this.info; }

    String[] getInfoSplitted() { return this.info.split("//__//"); }

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

    static Request makeSendRequest(String userName, String documentName, String mess) {

        String date = (new SimpleDateFormat("HH:mm:ss")).format(new Date());
        String message = date + " " + userName + " " + mess;

        return new Request(
                String.format(
                        "send//__//%s//__//%s//__//%s",
                        userName,
                        documentName,
                        message
                )
        );
    }
}
