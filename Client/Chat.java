import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

/**
 * Classe cha implemanta la chat per i documenti creati dagli utenti.
 * Ogni utente quando entra in stato di edit istanzia la chat e attiva il thread,
 * che in un ciclo infinito legge i messaggi inviati sul documento e li memorizza.
 */

class Chat extends Thread {

// ================= VARIABILI DI ISTANZA ========================================================= \\

    /** Lista che memorizza i messaggi da leggere, inviati sul documento. */
    private final ArrayList<String> messages;

    /** Gruppo di multicast del documento a cui l'utente si registra. */
    private MulticastSocket multicastSocket;
    private InetAddress groupAddress;
    private int groupPort;

// ================= COSTRUTTORE ================================================================== \\

    Chat(InetAddress groupAddress, int groupPort) throws IOException {

        this.groupAddress = groupAddress;
        this.groupPort = groupPort;

        this.multicastSocket = new MulticastSocket(groupPort);
        this.multicastSocket.joinGroup(groupAddress);

        this.messages = new ArrayList<>();
    }

// ================= METODI DI ISTANZA ============================================================ \\

    /**
     * Lascia il gruppo di multicast, e lo chiude,
     * quindi abbandona la chat.
     * Invocato dal client quando l'utente esegue una end-edit.
     */
    void destroy() throws IOException {

        multicastSocket.leaveGroup(groupAddress);
        multicastSocket.close();
        multicastSocket = null;
    }

    /**
     * Metodo usato dal client quando l'utente vuole visualizzare i messaggi
     * che sono stati inviati sulla chat del documento, non ancora letti.
     */
    String receiveMessage() {

        StringBuilder builder = new StringBuilder();

        synchronized (messages) {

            for (String message : messages)
                builder.append("  ").append(message).append("\n");

            messages.clear();
        }

        return builder.toString();
    }

    /**
     * Metodo usato dal client,
     * quando l'utente vuole mandare un messaggio sulla chat del documento.
     */
    Result sendMessage(String message) {

        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, groupAddress, groupPort);

        try { multicastSocket.send(packet); }
        catch (IOException ex) { return Result.FAIL(); }

        return Result.OK();
    }

    /**
     * Quando l'utente esegue una edit,
     * si attiva il thread chat che in un ciclo infinito,
     * riceve i messaggi inviati sulla chat del documento
     * e li mette nella lista dei messaggi da leggere.
     */
    @Override
    public void run() {

        boolean stop = false;

        while (!stop) {

            try {

                byte[] buffer = new byte[UtilsClass.MAX_MESSAGE_LENGTH];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                multicastSocket.receive(packet);
                String data = new String(packet.getData(), packet.getOffset(), packet.getLength());

                synchronized (messages) { messages.add(data); }

            } catch (IOException ex) { stop = true; }
        }
    }
}
