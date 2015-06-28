package ga.nurupeaches.katou.network.server;

import java.io.IOException;

public interface Server {

    public void tick();

    public void close() throws IOException;

}
