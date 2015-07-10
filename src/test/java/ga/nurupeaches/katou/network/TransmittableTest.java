package ga.nurupeaches.katou.network;

import ga.nurupeaches.katou.Configuration;
import ga.nurupeaches.katou.filesystem.KatouFile;
import ga.nurupeaches.katou.network.server.Server;
import ga.nurupeaches.katou.network.server.TCPServer;
import ga.nurupeaches.katou.utils.BufferUtils;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TransmittableTest extends TestCase {

    private Server server;
    private static final int PORT = 8080;

    // Not a list of waifus, I swear!
    private String[] randomStrings = {
            "Sejuani", "Tenshi", "Frau", "Xenovia", "Katou", "Aqua", "Yurippe", "Origami",
            "Tohsaka", "Pepperoni", "Schokolade", "Sarasvati", "Maki", "Chizuru", "Galil"
    };

    @Override
    protected void setUp() throws Exception {
        server = new TCPServer(PORT);
    }

    @Override
    protected void tearDown() throws Exception {
        server.close();
        Configuration.saveConfig();
    }

    @Test
    public void testTick() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                while(true){
                    server.tick();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        thread.start();

        try {
            // shh, test name with non-standard characters to test
            KatouFile file = KatouFile.fromFile(new File("C:/Users/Tsunko/Downloads/(C85)(同人音楽)(東方)%5BForeground Eclipse%5D Stories That Last Thorough the Sleepless Nights (256K MP3).rar"));
            char fileName[] = file.getName();
            Socket socket = new Socket(InetAddress.getLocalHost(), PORT);
            OutputStream stream = socket.getOutputStream();

            // write client version/identifier
            stream.write("KatouTestClient".getBytes(StandardCharsets.UTF_8));

            // sleep a bit to let the client not recv the rest of the information and assume it's part of the version
            Thread.sleep(100);

            // write out that we're sending a KatouFile
            ByteBuffer data = ByteBuffer.allocate(Byte.BYTES + Long.BYTES + Integer.BYTES + fileName.length * Character.BYTES + Long.BYTES + KatouFile.HASH_SIZE);
            data.put((byte) 0x02);
            data.putLong(file.getSize());
            // write out length of file name
            data.putInt(fileName.length);

            // write out rest of the data
            BufferUtils.copyCharsToBuffer(fileName, data);
            data.putLong(file.getFileSize());
            data.put(file.getHash());
            for(int i=0; i < 10; i++){
                stream.write(data.array());
                Thread.sleep(50);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        synchronized(this){
            this.wait();
        }
    }

}
