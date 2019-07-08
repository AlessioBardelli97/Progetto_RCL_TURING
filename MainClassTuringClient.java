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

public class MainClassTuringClient {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        String[] splitCommand;
        String command;
        Result result;

        String userLoggedName = null;
        String documentNameEdited = null;

        Chat chat = null;
        InetAddress groupAddress;
        int groupPort;

        System.out.println("'--help' per aver informazione\n'--exit' per uscire\n");

        while (true) {

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

                            "\nusage : turing COMMAND [ARGS ...]\n" +
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

                        Remote remoteObject = LocateRegistry.getRegistry(UtilsClass.SERVICE_REGISTER_PORT).lookup(UtilsClass.SERVICE_REGISTER_NAME);
                        RegisterMethod turing = (RegisterMethod) remoteObject;
                        result = turing.register(splitCommand[1], splitCommand[2]);

                        if (result.isOK())
                            System.out.println("  Registrazione eseguita con successo.\n");

                        else if (result.isFAIL())
                            System.out.println("  Registrazione fallita, username non valido.\n");

                    } catch (RemoteException ex) {

                        System.out.println("  Errore di rete...");
                        return;

                    } catch (NotBoundException ex) {

                        System.out.println("  Server non attivo...");
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

                    try { result = WriteAndRead.communication(loginRequest, UtilsClass.serverSocketAddress); }
                    catch (IOException ex) {

                        System.out.println("  Errore di rete, login fallito...");
                        return;
                    }

                    if (result.isOK()) {

                        System.out.println("  Login eseguito con successo.\n");
                        userLoggedName = userName;

                    } else if (result.isFAIL())
                        System.out.println("  Login fallito.\n");

                } break;

                case "logout" : {

                    if (userLoggedName == null) {

                        System.out.println("  Nessun utente è al momento loggato.\n");
                        continue;
                    }

                    Request logoutRequest = Request.makeLogoutRequest(userLoggedName);

                    try { result = WriteAndRead.communication(logoutRequest, UtilsClass.serverSocketAddress); }
                    catch (IOException ex) {

                        System.out.println("  Errore di rete, logout fallito...");
                        return;
                    }

                    if (result.isOK()) {

                        System.out.println("  Logout eseguito con successo.\n");
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

                    try { result = WriteAndRead.communication(createRequest, UtilsClass.serverSocketAddress); }
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

                    try { result = WriteAndRead.communication(shareRequest, UtilsClass.serverSocketAddress); }
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

                        try { result = WriteAndRead.communicationReadDocument(showRequest, UtilsClass.serverSocketAddress); }
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

                        try { result = WriteAndRead.communicationReadDocument(showRequest, UtilsClass.serverSocketAddress); }
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

                    try { result = WriteAndRead.communication(listRequest, UtilsClass.serverSocketAddress); }
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
                    }

                    String documentName = splitCommand[1], section = splitCommand[2];
                    Request editRequest = Request.makeEditRequest(userLoggedName, documentName, section);

                    try { result = WriteAndRead.communicationReadDocumentAndMulticastAddress(editRequest, UtilsClass.serverSocketAddress); }
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
                            groupAddress = InetAddress.getByName(multicastInfoAddress[0]);
                            groupPort = Integer.parseInt(multicastInfoAddress[1]);

                            chat = new Chat(groupAddress, groupPort);
                            chat.start();

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
                    }

                    String documentName = splitCommand[1], section = splitCommand[2];
                    Request endEditRequest = Request.makeEndEditRequest(userLoggedName, documentName, section);

                    try { result = WriteAndRead.communicationWriteDocument(endEditRequest, UtilsClass.serverSocketAddress); }
                    catch (IOException ex) {

                        System.out.println("  Errore di rete, operazione fallita...");
                        return;
                    }

                    if (result.isOK()) {

                        System.out.println("  Sezione " + section + " del documento " + documentName + " aggiornata con successo.\n");
                        documentNameEdited = null;

                        try {

                            if (chat != null) {

                                chat.interrupt();
                                chat.destroy();
                            }

                        } catch (IOException ex) {

                            System.out.println("  Impossibile chiudere la  chat.\n");
                            continue;
                        }
                    }

                    else if (result.isFAIL())
                        System.out.println("  Impossibile aggiornare la sezione richiesta.\n");

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

                    Date date = new Date();
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    StringBuilder mess = new StringBuilder();

                    mess.append(dateFormat.format(date)).append(" ").append(userLoggedName).append(" ");

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

                    if (userLoggedName == null)
                        return;

                    System.out.println("  Impossibile chiudere il client, utente ancora loggato.\n");

                } break;

                default: System.out.println("  Comando sconosciuto\n");
            }
        }
    }
}
