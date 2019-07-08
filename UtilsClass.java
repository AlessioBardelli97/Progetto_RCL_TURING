import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * classe che offre variabili globali per reperire le porte e gli indirizzi
 * dei vari servizzi che implementa TURING
 */

@SuppressWarnings("WeakerAccess")

class UtilsClass {

// ================= INFO REPERIRE IL SERVER  =========================================================== \\

    static final int SERVER_PORT = 9998;

    static final String SERVER_ADDRESS = "localhost";

    static SocketAddress serverSocketAddress = new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT);

// ================= INFO PER REPERIRE L'OGGETTO REMOTO PER L'OPERAZIONE DI REGISTRAZIONE =============== \\

    static final String SERVICE_REGISTER_NAME = "__RegisterMethodServer__";

    static final int SERVICE_REGISTER_PORT = 9999;

// ================= INFO PER REPERIRE E GESTIRE LA CHAT DEI DOCUMENTI ================================== \\

    static final int GROUP_MULTICAST_PORT = 12000;

    static final int MAX_MULTICASTADDRESS_NUMBER = 256;

    static final int MAX_MESSAGE_LENGTH = 4096;

    static final MulticastArray multicastArray = new MulticastArray();

}
