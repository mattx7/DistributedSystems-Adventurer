package vsp.api_client.utility;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import vsp.api_client.http.HTTPRequest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class BlackBoard {
    private static final Logger LOG = Logger.getLogger(HTTPRequest.class);

    private final Integer port;

    private final String hostAddress;

    /**
     * Initialize blackboard. Will send a broadcast and hopefully get port and hostaddress.
     *
     * @param remotePort Not null.
     * @throws IOException
     */
    public BlackBoard(int remotePort) throws IOException {
        // Platz für Pakete vorbereiten (1024 Buffer)
        byte[] byteArray = new byte[1024];

        // Datagrammsocket erzeugen + Socket binden
        DatagramPacket receivePacket;
        try (DatagramSocket datagramSocket = new DatagramSocket(remotePort)) {
            LOG.debug(String.format("datagramSocket created, listens on port %d", remotePort));

            // Erzeugen eines neuen Datagrammes mit dem ByteArray (Byte-Puffer & Größe des Puffers)
            receivePacket = new DatagramPacket(byteArray, byteArray.length);

            // Warten auf Ankunft eines Datagrammes (solange, bis ein Paket eintrifft)
            datagramSocket.receive(receivePacket);

            // Response holen, Port auslesen und speichern
            String responseMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

            port = Integer.parseInt(responseMessage.replaceAll("[\\D]", ""));
            LOG.debug(String.format("found the blackboard port %d", port));
            hostAddress = receivePacket.getAddress().getHostAddress();

            // Schließen des Sockets
            datagramSocket.close();
            LOG.debug("datagramSocket closed!");
        }
    }

    /**
     * @return the blackboard port
     */
    @NotNull
    public Integer getPort() {
        return port;
    }

    public String getHostAddress() {
        return hostAddress;
    }
}
