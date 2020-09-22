package com.ivk23.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(9090)) {
            System.out.println("UDP Server running at port 9090");
            byte[] receivedDataBuffer = new byte[1024];
            while (true) {
                final DatagramPacket packet =
                        new DatagramPacket(receivedDataBuffer,
                                receivedDataBuffer.length);
                socket.receive(packet);
                System.out.println(String.format("RECEIVED: %s FROM: %s:%d",
                        new String(receivedDataBuffer),
                        packet.getAddress().getHostName(),
                        packet.getPort()));
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
