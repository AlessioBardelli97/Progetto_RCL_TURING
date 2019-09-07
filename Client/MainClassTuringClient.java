/**
 * Alessio Bardelli Mat. 544270
 */

import java.io.IOException;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Usage: java MainClassTuringClient [-sp server_port] [-sa server_address] [-srp service_register_port].
 * Consultare la documentazione per maggiori informazioni.
 */

public class MainClassTuringClient {

    private static int server_port = UtilsClass.SERVER_PORT;
    private static String server_address = UtilsClass.SERVER_ADDRESS;

    private static int service_register_port = UtilsClass.SERVICE_REGISTER_PORT;

    public static void main(String[] args) {

        try {

            for (int i = 0; i < args.length; i += 2) {

                switch (args[i]) {

                    case "-sp" : server_port = Integer.parseInt(args[i+1]); break;
                    case "-sa" : server_address = args[i+1]; break;
                    case "-srp" : service_register_port = Integer.parseInt(args[i+1]); break;
                }
            }

        } catch (IndexOutOfBoundsException | NumberFormatException ignored) {}
		
		SocketAddress serverSocketAddress = new InetSocketAddress(server_address, server_port);

        Scanner in = new Scanner(System.in);

        boolean stop = false;
        String[] splitCommand;
        String command;
        Result result;

        String userLoggedName = null;
        String documentNameEdited = null;

        Chat chat = null;

        RegisterMethod turing;

        try {

            Remote remoteObject = LocateRegistry.getRegistry(service_register_port)
                    .lookup(UtilsClass.SERVICE_REGISTER_NAME);

            turing = (RegisterMethod) remoteObject;

        } catch (RemoteException | NotBoundException ex) {

            System.out.println("Impossibile avviare il client...");
            return;
        }

        System.out.println("'--help' per aver informazione\n'--exit' per uscire\n");

        while (!stop) {

            System.out.print("$ ");
            command = in.nextLine();
            splitCommand = command.split(" ");

            if (splitCommand.length < 1) {

                System.out.println("  Sintassi del comando errata.\n");
                continue;
            }

            switch (splitCommand[0]) {

                case "--help" : {

                    System.out.println(

                            "\nusage : COMMAND [ARGS ...]\n" +
                                    "\ncommands :\n" +
                                    "  register <username> <password> registra l'utente\n" +
                                    "  login <username> <password>    effettua il login\n" +
                                    "  logout                         effettua il logout\n\n" +
                                    "  create <doc> <numsezioni> crea un documento\n" +
                                    "  share <doc> <username>    condivide il documento\n" +
                                    "  show <doc> <sec>          mostra una sezione del documento\n" +
                                    "  show <doc>                mostra l'intero documento\n" +
                                    "  list                      mostra la lista dei documenti\n\n" +
                                    "  edit <doc> <sec>     modifica una sezione del documento\n" +
                                    "  end-edit <doc> <sec> fine modifica della sezione del doc\n\n" +
                                    "  send <msg> invia un msg sulla chat\n" +
                                    "  receive    visualizza i msg ricevuti sulla chat\n"
                    );

                } break;

                case "register" : {

                    if (userLoggedName != null) {

                        System.out.println("  Impossibile registrare un utente quando si è loggati.");
                        continue;

                    } else if (splitCommand.length != 3) {

                        System.out.println("  Sintassi del comando errata.\n");
                        continue;
                    }

                    try {

                        result = turing.register(splitCommand[1], splitCommand[2]);

                        if (result.isOK())
                            System.out.println("  Registrazione eseguita con successo.\n");

                        else if (result.isFAIL())
                            System.out.println("  Registrazione fallita, username non valido.\n");

                    } catch (RemoteException ex) {

                        System.out.println("  Errore di rete...");
                        return;

                    }

                } break;

                case "login" : {

                    if (userLoggedName != null) {

                        System.out.println("  Impossibile loggarsi con un altro utente.\n");
                        continue;

                    } else if (splitCommand.length != 3) {

                        System.out.println("  Sintassi del comando errata.\n");
                        continue;
                    }

                    String userName = splitCommand[1], password = splitCommand[2];
                    Request loginRequest = Request.makeLoginRequest(userName, password);

                    try { result = WriteAndRead.communication(loginRequest, serverSocketAddress); }
                    catch (IOException ex) {

                        System.out.println("  Errore di rete, login fallito...");
                        return;
                    }

                    if (result.isOK()) {

                        System.out.println("  Login eseguito con successo, non ci sono nuove notifiche.\n");
                        userLoggedName = userName;

                        try {

                            Notify notifyObject = new NotifyImplementation();
                            turing.registerForCallback(userName, notifyObject);

                        } catch (RemoteException ignored) {}

                    } else if (result.isFAIL()) {

                        System.out.println("  Login fallito.\n");

                    } else {

                        userLoggedName = userName;
                        System.out.println("  Login eseguito con successo, notifiche ricevute: ");

                        for (String notice : result.getInfoSplitted())
                            System.out.println("    " + notice);

                        System.out.println();

                        try {

                            Notify notifyObject = new NotifyImplementation();
                            turing.registerForCallback(userName, notifyObject);

                        } catch (RemoteException ignored) {}
                    }

                } break;

                case "logout" : {

                    if (userLoggedName == null) {

                        System.out.println("  Nessun utente è al momento loggato.\n");
                        continue;
						
                    } else if (documentNameEdited != null) {
						
						System.out.println("  Impossibile effettuare il logout, l'utente sta' editando un documento.\n");
						continue;
					}

                    Request logoutRequest = Request.makeLogoutRequest(userLoggedName);

                    try { result = WriteAndRead.communication(logoutRequest, serverSocketAddress); }
                    catch (IOException ex) {

                        System.out.println("  Errore di rete, logout fallito...");
                        return;
                    }

                    if (result.isOK()) {

                        System.out.println("  Logout eseguito con successo.\n");

                        try { turing.unregisterFromCallback(userLoggedName); }
                        catch (RemoteException ignore) {}

                        userLoggedName = null;

                    } else if (result.isFAIL())
                        System.out.println("  Logout fallito.\n");

                } break;

                case "create" : {

                    if (userLoggedName == null) {

                        System.out.println("  Impossibile creare un nuovo documento, nessun utente loggato.\n");
                        continue;

                    } else if (splitCommand.length != 3) {

                        System.out.println("  Sintassi del comando errata.\n");
                        continue;
                    }

                    String documentName = splitCommand[1], section = splitCommand[2];
                    Request createRequest = Request.makeCreateRequest(userLoggedName, documentName, section);

                    try { result = WriteAndRead.communication(createRequest, serverSocketAddress); }
                    catch (IOException ex) {

                        System.out.println("  Errore di rete, creazione del documento fallita...");
                        return;
                    }

                    if (result.isOK())
                        System.out.println("  Documento " + splitCommand[1] + " creato con successo, composto da " + splitCommand[2] + " sezioni.\n");

                    else if (result.isFAIL())
                        System.out.println("  Impossibile creare il documento.\n");

                } break;

                case "share" : {

                    if (userLoggedName == null) {

                        System.out.println("  Impossibile condividere il documento, nessun utente loggato.\n");
                        continue;

                    } else if (splitCommand.length != 3) {

                        System.out.println("  Sintassi del comando errata.\n");
                        continue;
                    }

                    String documentName = splitCommand[1], destUserName = splitCommand[2];
                    Request shareRequest = Request.makeShareRequest(userLoggedName, documentName, destUserName);

                    try { result = WriteAndRead.communication(shareRequest, serverSocketAddress); }
                    catch (IOException ex) {

                        System.out.println("  Erroe di rete, condivisione documento fallita...");
                        return;
                    }

                    if (result.isOK())
                        System.out.println("  Documento " + splitCommand[1] + " condiviso con " + splitCommand[2] +" con successo.\n");

                    else if (result.isFAIL())
                        System.out.println("  Impossibile condividere il documento.\n");

                } break;

                case "show" : {

                    if (userLoggedName == null) {

                        System.out.println("  Impossibile scaricare un documento, accedere prima.\n");
                        continue;

                    } else if (splitCommand.length != 3 && splitCommand.length != 2) {

                        System.out.println("  Sintassi del comando errata.\n");
                        continue;
                    }

                    Request showRequest;

                    if (splitCommand.length == 3) {

                        String documentName = splitCommand[1], section = splitCommand[2];
                        showRequest = Request.makeShowRequest(userLoggedName, documentName, section);

                        try { result = WriteAndRead.communicationReadDocument(showRequest, serverSocketAddress); }
                        catch (IOException ex) {

                            System.out.println("  Erroe di rete, scaricamento sezione fallito...");
                            return;
                        }

                        if (result.isOK())
                            System.out.println("  Sezione " + splitCommand[2] + " scaricata con successo.\n");

                        else if (result.isFAIL())
                            System.out.println("  Impossibile scaricare la sezione.\n");

                    } else {

                        String documentName = splitCommand[1];
                        showRequest = Request.makeShowRequest(userLoggedName, documentName);

                        try { result = WriteAndRead.communicationReadDocument(showRequest, serverSocketAddress); }
                        catch (IOException ex) {

                            System.out.println("  Erroe di rete, scaricamento documento fallito...");
                            ex.printStackTrace();
                            return;
                        }

                        if (result.isOK())
                            System.out.println("  Documento " + splitCommand[1] + " scaricato con successo.\n");

                        else if (result.isFAIL())
                            System.out.println("  Impossibile scaricare il documento.\n");
                    }

                } break;

                case "list" : {

                    if (userLoggedName == null) {

                        System.out.println("  Impossibile ottenere la lista dei documenti, accedere prima.\n");
                        continue;
                    }

                    Request listRequest = Request.makeListRequest(userLoggedName);

                    try { result = WriteAndRead.communication(listRequest, serverSocketAddress); }
                    catch (IOException ex) {

                        System.out.println("  Errore di rete, operazione fallita...");
                        return;
                    }

                    if (result.isFAIL())
                        System.out.println("  Impossibile ottenere la lista dei documenti.\n");

                    else
                        System.out.println(result.getInfo());

                } break;

                case "edit" : {

                    if (userLoggedName == null) {

                        System.out.println("  Impossibile modificare un documento, nessun utente loggato.\n");
                        continue;

                    } else if (splitCommand.length != 3) {

                        System.out.println("  Sintassi del comando errata.\n");
                        continue;

                    } else if (documentNameEdited != null) {

                        System.out.println("  Impossibile modificare un documento, l'utente è gia in editing.\n");
                        continue;
                    }

                    String documentName = splitCommand[1], section = splitCommand[2];
                    Request editRequest = Request.makeEditRequest(userLoggedName, documentName, section);

                    try { result = WriteAndRead.communicationReadDocumentAndMulticastAddress(editRequest, serverSocketAddress); }
                    catch (IOException ex) {

                        System.out.println("  Errore di rete, operazione fallita...");
                        return;
                    }

                    if (result.isFAIL())
                        System.out.println("  Impossibile editare la sezione richiesta.\n");

                    else {

                        System.out.println("  Sezione " + section + " del documento " + documentName + " scaricata con successo.\n");
                        documentNameEdited = documentName;

                        try {

                            String[] multicastInfoAddress = result.getInfoSplitted();
                            InetAddress groupAddress = InetAddress.getByName(multicastInfoAddress[0]);
                            int groupPort = Integer.parseInt(multicastInfoAddress[1]);

                            chat = new Chat(groupAddress, groupPort);
                            chat.start();

                            turing.unregisterFromCallback(userLoggedName);

                        } catch (IOException ex) {

                            System.out.println("  Impossibile avviare la chat.\n");
                            continue;
                        }
                    }

                } break;

                case "end-edit" : {

                    if (userLoggedName == null) {

                        System.out.println("  Impossibile aggiornare la sezione, nessun utente loggato.\n");
                        continue;

                    } else if (splitCommand.length != 3) {

                        System.out.println("  Sintassi del comando errata.\n");
                        continue;

                    } else if (documentNameEdited == null) {

                        System.out.println("  Impossibile aggiornare la sezione, nessun documento in editing.\n");
                        continue;
                    }

                    String documentName = splitCommand[1], section = splitCommand[2];
                    Request endEditRequest = Request.makeEndEditRequest(userLoggedName, documentName, section);

                    try { result = WriteAndRead.communicationWriteDocument(endEditRequest, serverSocketAddress); }
                    catch (IOException ex) {

                        System.out.println("  Errore di rete, operazione fallita...");
                        return;
                    }

                    if (result.isOK()) {

                        System.out.println("  Sezione " + section + " del documento " + documentName + " aggiornata con successo.");
                        System.out.println("  Non ci sono nuove notifiche.\n");
                        documentNameEdited = null;

                        try {

                            if (chat != null) { chat.leaveChat(); }

                            Notify notifyObject = new NotifyImplementation();
                            turing.registerForCallback(userLoggedName, notifyObject);

                        } catch (IOException ex) {

                            System.out.println("  Impossibile chiudere la chat.\n");
                            continue;
                        }

                    } else if (result.isFAIL()) {

                        System.out.println("  Impossibile aggiornare la sezione richiesta.\n");

                    } else {

                        System.out.println("  Sezione " + section + " del documento " + documentName + " aggiornata con successo.");
                        documentNameEdited = null;

                        for (String notice : result.getInfoSplitted())
                            System.out.println("    " + notice);

                        System.out.println();

                        try {

                            if (chat != null) { chat.leaveChat(); }

                            Notify notifyObject = new NotifyImplementation();
                            turing.registerForCallback(userLoggedName, notifyObject);

                        } catch (IOException ex) {

                            System.out.println("  Impossibile chiudere la chat.\n");
                            continue;
                        }
                    }

                } break;

                case "send" : {

                    if (userLoggedName == null) {

                        System.out.println("  Impossibile inviare un messaggio, nessun utente loggato.\n");
                        continue;

                    } else if (splitCommand.length < 2) {

                        System.out.println("  Sintassi del comando errata.\n");
                        continue;

                    } else if (documentNameEdited == null) {

                        System.out.println("  Impossibile inviare un messaggio, nessun documento in editing.\n");
                        continue;

                    } else if (chat == null) {

                        System.out.println("  Impossibile inviare un messaggo, chat non attiva.\n");
                        continue;
                    }

                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    StringBuilder mess = new StringBuilder();
                    String date = dateFormat.format(new Date());

                    mess.append(date).append(" - ").append(userLoggedName).append(" - ");

                    for (int i = 1; i  < splitCommand.length; i++)
                        mess.append(splitCommand[i]).append(" ");

                    result = chat.sendMessage(mess.toString());

                    if (result.isOK())
                        System.out.println("  Messaggio inviato con successo.\n");

                    else if (result.isFAIL())
                        System.out.println("  Impossibile inviare il messaggio.\n");

                } break;

                case "receive" : {

                    if (userLoggedName == null) {

                        System.out.println("  impossibile ricevere i messaggi, nessun utente loggato.\n");
                        continue;

                    } else if (splitCommand.length != 1) {

                        System.out.println("  Sintassi del comando errata.\n");
                        continue;

                    } else if (documentNameEdited == null) {

                        System.out.println("  Impossibile ricevere i messaggi, nessun documento in editing.\n");
                        continue;

                    } else if (chat == null) {

                        System.out.println("  Impossibile ricevere i messaggi, chat non attiva.\n");
                        continue;
                    }

                    System.out.println(chat.receiveMessage());

                } break;

                case "--exit" : {

                    if (userLoggedName == null) {

                        System.out.println("  Cliet terminato.\n");
                        stop = true;
                    }

                    else
                        System.out.println("  Impossibile chiudere il client, utente ancora loggato.\n");

                } break;

                default: System.out.println("  Comando sconosciuto\n");
            }
        }

        System.exit(0);
    }
}
