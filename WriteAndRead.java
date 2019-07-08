import java.io.File;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class WriteAndRead {

    static void write(Request request, SocketChannel channel) throws IOException {

        byte[] req = request.getInfo().getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(4).putInt(req.length).flip();

        while (buffer.hasRemaining())
            channel.write(buffer);

        buffer = ByteBuffer.allocate(req.length).put(req).flip();

        while (buffer.hasRemaining())
            channel.write(buffer);
    }

    static Result read(SocketChannel channel) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(4);

        while (buffer.hasRemaining())
            channel.read(buffer);

        int length = buffer.flip().getInt();

        buffer = ByteBuffer.allocate(length);

        while (buffer.hasRemaining())
            channel.read(buffer);

        return new Result(buffer.array());
    }

    private static void writeDocument(Request request, SocketChannel channel) throws IOException {

        int section; String filename; ByteBuffer buffer;
        String[] splitRequest = request.getInfoSplitted();

        try {

            section = Integer.parseInt(splitRequest[3]);
            if (section < 0) { write(Result.FAIL().toRequest(), channel); return; }

        } catch (NumberFormatException | IndexOutOfBoundsException ex) {

            write(Result.FAIL().toRequest(), channel);
            return;
        }

        filename = String.format("%s\\%d.txt", splitRequest[2], section);

        try (FileChannel fileChannel = FileChannel.open(Paths.get(filename), StandardOpenOption.READ)) {

            buffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(buffer);

        } catch (IOException ex) {

            write(Result.FAIL().toRequest(), channel);
            return;
        }

        Request newRequest = new Request(request.getInfo() + "//__//" + new String(buffer.flip().array()));
        write(newRequest, channel);
    }

    private static Result readDocument(Request request, SocketChannel channel)  throws IOException {

        Result result = read(channel);
        if (result.isFAIL())
            return result;

        String documentName = request.getInfoSplitted()[2];
        String[] files = result.getInfoSplitted();

        //noinspection ResultOfMethodCallIgnored
        new File(documentName).mkdir();

        Integer section = null;
        try { section = Integer.parseInt(request.getInfoSplitted()[3]); }
        catch (IndexOutOfBoundsException | NumberFormatException ignored) {  }

        for (int i = 0; i < files.length; i++) {

            String fileName = String.format("%s\\%d.txt", documentName, section == null ? i : section);
            FileChannel fileChannel = FileChannel.open(Paths.get(fileName), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            byte[] bytesFile = files[i].getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(bytesFile.length);
            buffer.put(bytesFile).flip();

            fileChannel.write(buffer);
            fileChannel.close();
        }

        return Result.OK();
    }

    private static Result readDocumentAndMulticastAddress(Request request, SocketChannel channel) throws IOException {

        Result result = read(channel);
        if (result.isFAIL())
            return result;

        String documentName = request.getInfoSplitted()[2];
        String[] files = result.getInfoSplitted();

        //noinspection ResultOfMethodCallIgnored
        new File(documentName).mkdir();

        Integer section = null;
        try { section = Integer.parseInt(request.getInfoSplitted()[3]); }
        catch (IndexOutOfBoundsException | NumberFormatException ignored) {  }

        for (int i = 0; i < files.length-2; i++) {

            String fileName = String.format("%s\\%d.txt", documentName, section == null ? i : section);
            FileChannel fileChannel = FileChannel.open(Paths.get(fileName), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            byte[] bytesFile = files[i].getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(bytesFile.length);
            buffer.put(bytesFile).flip();

            fileChannel.write(buffer);
            fileChannel.close();
        }

        String res = files[files.length-2] + "//__//" + files[files.length-1];
        return new Result(res);
    }

    static Result communication(Request request, SocketAddress address) throws IOException {

        try (SocketChannel channel = SocketChannel.open(address)) {

            write(request, channel);
            return read(channel);
        }
    }

    static Result communicationReadDocument(Request request, SocketAddress address) throws IOException {

        try (SocketChannel channel = SocketChannel.open(address)) {

            write(request, channel);
            return readDocument(request, channel);
        }
    }

    static Result communicationWriteDocument(Request request, SocketAddress address) throws IOException {

        try (SocketChannel channel = SocketChannel.open(address)) {

            writeDocument(request, channel);
            return read(channel);
        }
    }

    static Result communicationReadDocumentAndMulticastAddress(Request request, SocketAddress address) throws IOException {

        try (SocketChannel channel = SocketChannel.open(address)) {

            write(request, channel);
            return readDocumentAndMulticastAddress(request, channel);
        }
    }
}
