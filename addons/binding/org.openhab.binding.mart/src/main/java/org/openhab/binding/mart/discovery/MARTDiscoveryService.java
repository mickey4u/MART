package org.openhab.binding.mart.discovery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.mart.handler.martHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MARTDiscoveryService extends AbstractDiscoveryService {

    private Logger logger = LoggerFactory.getLogger(MARTDiscoveryService.class);

    private static int TIME_OUT = 5000;

    // Simple Service Discovery Protocol (SSDP)
    // port to send discovery message to
    public static final int SSDP_PORT_NUMBER = 7090;
    // port to use for sending discovery message
    public static final int SSDP_SEARCH_PORT_NUMBER = 7090;

    // broadcast address for sending discovery message
    private static final String SSDP_IP = "239.255.255.250";

    public InetAddress address;
    // when true keep sending out discovery message
    static boolean discoveryRunning = false;

    public MARTDiscoveryService() {
        super(martHandler.SUPPORTTED_THING_TYPES, 15, true);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void startScan() {
        // TODO Auto-generated method stub

    }

    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return martHandler.SUPPORTTED_THING_TYPES;
    }

    @Override
    protected void startBackgroundDiscovery() {

    }

    @Override
    protected void stopBackgroundDiscovery() {

    }

    private synchronized void discoverMart() {

    }

    public void sendMartDiscoveryMessage() {
        logger.debug("MART discovery service is running.");
        try {
            // returns the address of the local host
            InetAddress localhost = InetAddress.getLocalHost();
            // Creates a socket address from an IP address and a port number.
            InetSocketAddress sourceAddress = new InetSocketAddress(localhost, SSDP_PORT_NUMBER);
            InetSocketAddress destinationAddress = new InetSocketAddress(InetAddress.getByName(SSDP_IP),
                    SSDP_PORT_NUMBER);

            StringBuffer discoveryMessage = new StringBuffer();
            discoveryMessage.append("My discovery message");
            byte[] discoveyMessageBytes = discoveryMessage.toString().getBytes();
            DatagramPacket discoveryPacket = new DatagramPacket(discoveyMessageBytes, discoveyMessageBytes.length,
                    destinationAddress);

            // send multicast packet
            // The multicast datagram socket class is useful for sending and receiving IP multicast packets
            MulticastSocket multicastSocket = null;
            try {
                multicastSocket = new MulticastSocket(null);
                // Binds this DatagramSocket to a specific address and port.
                multicastSocket.bind(sourceAddress);
                multicastSocket.setTimeToLive(4);
                logger.debug("Send discovery packet");
                multicastSocket.send(discoveryPacket);
            } finally {
                if (multicastSocket != null) {
                    multicastSocket.disconnect();
                    multicastSocket.close();
                }
            }
            // response listener
            DatagramSocket martReceiveSocket = null;
            DatagramPacket martReceivePacket = null;

            try {
                // Constructs a DatagramPacket for receiving packets of length length.
                martReceiveSocket = new DatagramSocket(SSDP_PORT_NUMBER);
                martReceiveSocket.setSoTimeout(TIME_OUT);
                martReceiveSocket.send(discoveryPacket);

                while (true) {
                    try {
                        martReceivePacket = new DatagramPacket(new byte[1536], 1536);
                        martReceiveSocket.receive(martReceivePacket);
                        final String message = new String(martReceivePacket.getData());
                        logger.debug("Received message: {}", message);

                        new Thread(new Runnable() {
                            String labelName = "Mart Adapter";
                            ThingUID uuid = null;

                            @Override
                            public void run() {
                                if (message != null) {
                                    if (message.contains("mart adapter")) {

                                    }
                                }

                            }
                        }).start();
                    } catch (Exception e) {
                        logger.debug("Message receive timeout.");
                        break;
                    }
                }

            } finally {
                if (martReceiveSocket != null) {
                    martReceiveSocket.disconnect();
                    martReceiveSocket.close();
                }
            }

        } catch (Exception e) {
            logger.debug("MART discovery service coudn't be started.");
        }

    }

}
