package org.openhab.binding.mart.discovery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.openhab.binding.mart.handler.martHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MARTDiscoveryService extends AbstractDiscoveryService {

    private Logger logger = LoggerFactory.getLogger(MARTDiscoveryService.class);

    private static int TIME_OUT = 5000;

    // Simple Service Discovery Protocol (SSDP)
    public static final int SSDP_PORT_NUMBER = 7090;
    // broadcast address for sending discovery message
    private static final String SSDP_IP = "239.255.255.250";

    public InetAddress address;
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
            InetSocketAddress sourceAddress = new InetSocketAddress(localhost, SSDP_PORT_NUMBER);
            InetSocketAddress destinationAddress = new InetSocketAddress(InetAddress.getByName(SSDP_IP),
                    SSDP_PORT_NUMBER);

            StringBuffer discoveryMessage = new StringBuffer();
            discoveryMessage.append("My discovery message");
            byte[] discoveyMessageBytes = discoveryMessage.toString().getBytes();
            DatagramPacket discoveryPacket = new DatagramPacket(discoveyMessageBytes, discoveyMessageBytes.length,
                    destinationAddress);

            // send multicast packet
            MulticastSocket multicastSocket = null;
            try {
                multicastSocket = new MulticastSocket(null);
                // Binds this DatagramSocket to a specific address and port.
                multicastSocket.bind(sourceAddress);
                multicastSocket.setTimeToLive(4);
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

                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }

            } finally {
                // TODO: handle finally clause
            }

        } catch (Exception e) {
            logger.debug("MART discovery service coudn't be started.");
        }

    }

}
