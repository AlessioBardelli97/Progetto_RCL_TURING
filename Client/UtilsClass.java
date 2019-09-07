/**
 * Alessio Bardelli Mat. 544270
 */

/**
 * Classe che offre variabili globali per reperire porte e indirizzi predefiniti
 * dei vari servizi offerti da TURING.
 * Contiene in oltre variabili per la gestione delle chat dei documenti, 
 * e altre variabili di utilità.
 */

class UtilsClass {
	
// ================= VARIABILI DI UTILITA' ====================================================== \\

    /**
     * Massima dimensione, in byte,
     * di un messagio che si può inviare
     * sulla chat di un documento.
     */
    static final int MAX_MESSAGE_LENGTH = 16384;

// ================= INFO DI DEFAULT PER REPERIRE IL SERVER  ==================================== \\

    static final int SERVER_PORT = 9998;

    static final String SERVER_ADDRESS = "localhost";

// ================= INFO DI DEFAULT PER REPERIRE L'OGGETTO REMOTO ============================== \\

    static final String SERVICE_REGISTER_NAME = "__RegisterMethodServer__";

    static final int SERVICE_REGISTER_PORT = 9999;

// ================= INFO PER REPERIRE E GESTIRE LA CHAT DEI DOCUMENTI ========================== \\

    static final int GROUP_MULTICAST_PORT = 12000;

    static final int MAX_MULTICASTADDRESS_NUMBER = 256;

    static final MulticastArray multicastArray = new MulticastArray();
}
