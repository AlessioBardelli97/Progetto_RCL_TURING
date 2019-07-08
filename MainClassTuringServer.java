import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;

public class MainClassTuringServer {

    private static final HashMap<String, User> listRegisteredUsers = new HashMap<>();
    static final HashMap<String, Document> listDocuments = new HashMap<>();

    public static void main(String[] args) {

        try {

            RegisterMetodimplementation turing = new RegisterMetodimplementation(listRegisteredUsers);
            LocateRegistry.createRegistry(UtilsClass.SERVICE_REGISTER_PORT);

            LocateRegistry.getRegistry(UtilsClass.SERVICE_REGISTER_PORT).rebind(
                    UtilsClass.SERVICE_REGISTER_NAME,
                    turing
            );

        } catch (RemoteException ex) {

            System.out.println("Errore di rete, impossibile avviare turing...");
            return;
        }

        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            serverChannel.socket().bind(new InetSocketAddress(UtilsClass.SERVER_PORT));
            serverChannel.configureBlocking(false);

            try (Selector selector = Selector.open()) {

                serverChannel.register(selector, SelectionKey.OP_ACCEPT);
                System.out.println("Turing server avviato...");

                //noinspection InfiniteLoopStatement
                while (true) {

                    selector.select();
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                    while (keyIterator.hasNext()) {

                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isAcceptable()) {

                            ServerSocketChannel server = (ServerSocketChannel) key.channel();
                            SocketChannel clientChannel = server.accept();
                            clientChannel.configureBlocking(false);
                            clientChannel.register(selector, SelectionKey.OP_READ);

                        } else if (key.isReadable()) {

                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            Request request = WriteAndRead.read(clientChannel).toRequest();
                            Result result = handleRequest(request);
                            clientChannel.register(selector, SelectionKey.OP_WRITE, result);

                        } else if (key.isWritable()) {

                            SocketChannel clientChannel = (SocketChannel) key.channel();
                            Result result = (Result) key.attachment();
                            WriteAndRead.write(result.toRequest(), clientChannel);
                            clientChannel.close();
                        }
                    }
                }
            }

        } catch (IOException ex) {

            System.out.println("Errore di rete, impossibile avviare turing...");
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static Result handleRequest(Request request) throws IOException {

        System.out.println(request.getInfo());
        Result result = Result.FAIL();
        String[] splitRequest = request.getInfoSplitted();

        switch (splitRequest[0]) {

            case "login" : {

                String userName = splitRequest[1], password = splitRequest[2];

                synchronized (listRegisteredUsers) {

                    User user = listRegisteredUsers.get(userName);

                    if (user != null && user.isStarted() &&
                            user.getPassword().equals(password)) {

                        user.setLogged();
                        result = Result.OK();
                    }
                }

            } break;

            case "logout" : {

                String userName = splitRequest[1];

                synchronized (listRegisteredUsers) {

                    User user = listRegisteredUsers.get(userName);

                    if (user != null && user.isLogged()) {

                        user.setStarted();
                        result = Result.OK();
                    }
                }

            } break;

            case "create" : {

                int numSections;
                try { numSections = Integer.parseInt(splitRequest[3]); }
                catch (NumberFormatException ex) { break; }
                if (numSections <= 0) { break; }

                String userName = splitRequest[1], documentName = splitRequest[2];

                synchronized (listRegisteredUsers) {

                    User user = listRegisteredUsers.get(userName);

                    if (user != null && user.isLogged()) {

                        if (new File(documentName).mkdir()) {

                            for (int i = 0; i < numSections; i++)
                                new File(documentName + "\\" + i + ".txt").createNewFile();

                            synchronized (listDocuments) {
                                listDocuments.put(documentName, new Document(userName, numSections));
                            }

                            result = Result.OK();
                        }
                    }
                }

            } break;

            case "share" : {

                String creatorUserName = splitRequest[1],
                        destUserName = splitRequest[3], documentName = splitRequest[2];

                synchronized (listRegisteredUsers) {

                    User creatorUser = listRegisteredUsers.get(creatorUserName);
                    User destUser = listRegisteredUsers.get(destUserName);

                    if (destUser != null && creatorUser != null && creatorUser.isLogged()) {

                        synchronized (listDocuments) {

                            Document document = listDocuments.get(documentName);

                            if (document != null && document.getCreatorUser().equals(creatorUserName)) {

                                document.addCollaborators(destUserName);
                                result = Result.OK();
                            }
                        }
                    }
                }

            } break;

            case "show" : {

                String userName = splitRequest[1], documentName = splitRequest[2];

                synchronized (listRegisteredUsers) {

                    User user = listRegisteredUsers.get(userName);

                    if (user != null && user.isLogged()) {

                        synchronized (listDocuments) {

                            Document document = listDocuments.get(documentName);

                            if (document != null) {

                                ByteBuffer buffer;

                                try {

                                    if (splitRequest.length == 4) {

                                        int section = Integer.parseInt(splitRequest[3]);
                                        if (section < 0) { break; }

                                        String fileName = String.format("%s\\%d.txt", documentName, section);
                                        FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ);
                                        buffer = ByteBuffer.allocate((int) channel.size());

                                        channel.read(buffer);
                                        channel.close();

                                    } else {

                                        String[] fileNames = new String[document.getNumSections()];
                                        long totSizeFiles = 0;

                                        for (int i = 0; i < fileNames.length; i++) {

                                            fileNames[i] = String.format("%s\\%d.txt", documentName, i);
                                            totSizeFiles += new File(fileNames[i]).length();
                                        }

                                        byte[] separator = "//__//".getBytes();
                                        buffer = ByteBuffer.allocate(((separator.length) * (fileNames.length - 1)) + (int) totSizeFiles);

                                        int i = 0;
                                        for (String fileName : fileNames) {

                                            FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ);
                                            channel.read(buffer);
                                            channel.close();

                                            if ((i++) < (fileNames.length - 1))
                                                buffer.put(separator);
                                        }
                                    }

                                    result = new Result(buffer.flip().array());

                                } catch (Exception ex) { break; }
                            }
                        }
                    }
                }

            } break;

            case "list" : {

                String userName = splitRequest[1];

                synchronized (listRegisteredUsers) {

                    User user = listRegisteredUsers.get(userName);

                    if (user != null && user.isLogged()) {

                        synchronized (listDocuments) {

                            ArrayList<Map.Entry<String,Document>> documents = Document.getListOfDocumentsOfUser(userName);

                            if (documents != null) {

                                StringBuilder res = new StringBuilder();

                                for (Map.Entry<String,Document> document : documents) {

                                    res.append("  ").append(document.getKey()).append(":\n");
                                    res.append("    Creatore: ").append(document.getValue().getCreatorUser()).append("\n");
                                    res.append("    Collaboratori: ");

                                    for (String un : document.getValue().getCollaborators())
                                        res.append(un).append(", ");

                                    res.append("\n");
                                }

                                result = new Result(res.toString());
                            }
                        }
                    }
                }

            } break;

            case "edit" : {

                String userName = splitRequest[1], documentName = splitRequest[2];

                synchronized (listRegisteredUsers) {

                    User user = listRegisteredUsers.get(userName);

                    if (user != null && user.isLogged()) {

                        try {

                            int section = Integer.parseInt(splitRequest[3]);
                            if (section < 0) { break; }

                            synchronized (listDocuments) {

                                Document document = listDocuments.get(documentName);

                                if (document.isEditableFromUser(userName) && document.isStarted(section)) {

                                    String fileName = String.format("%s\\%d.txt", documentName, section);
                                    FileChannel channel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ);
                                    ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());

                                    channel.read(buffer);
                                    channel.close();

                                    user.setEdit();
                                    user.setEditingDocument(documentName);
                                    document.setUserNameEditing(userName, section);
                                    document.setEdit(section);

                                    Integer groupPort = document.getGroupPort();
                                    String groupAddress = document.getGroupAddress();

                                    String res = new String(buffer.flip().array()) + "//__//" + groupAddress + "//__//" + groupPort.toString();
                                    result = new Result(res);
                                }
                            }

                        } catch (Exception ex) { break; }
                    }
                }

            } break;

            case "end-edit" : {

                String userName = splitRequest[1], documentName = splitRequest[2], fileContent = splitRequest[4];

                synchronized (listRegisteredUsers) {

                    User user = listRegisteredUsers.get(userName);

                    if (user != null && user.isEdit()) {

                        try {

                            int section = Integer.parseInt(splitRequest[3]);
                            if (section < 0) { break; }

                            synchronized (listDocuments) {

                                Document document = listDocuments.get(documentName);

                                if (document.isEditableFromUser(userName) && document.isEdit(section)
                                        && document.getUserNameEditing(section).equals(userName)) {

                                    String fileName = String.format("%s\\%d.txt", documentName, section);
                                    FileChannel fileChannel = FileChannel.open(Paths.get(fileName), StandardOpenOption.WRITE);

                                    byte[] fileContentByte = fileContent.getBytes();
                                    ByteBuffer buffer = ByteBuffer.allocate(fileContentByte.length);
                                    buffer.put(fileContentByte).flip();
                                    fileChannel.write(buffer);
                                    fileChannel.close();

                                    user.setLogged();
                                    user.setEditingDocument(null);
                                    document.setStarted(section);
                                    document.setUserNameEditing(null, section);
                                    result = Result.OK();
                                }
                            }

                        } catch (Exception ex) { break; }
                    }
                }

            } break;
        }

        return result;
    }
}
