package com.ivk23.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;

public class UDPClient {

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(9091)) {
            System.out.println("UDP Client running at port 9091");

            final InetAddress address = InetAddress.getByName("localhost");
            final byte[] data = LocalDateTime.now().withNano(0).toString().getBytes();
            final DatagramPacket packet =
                    new DatagramPacket(data, data.length, address, 9090);

            socket.send(packet);
            System.out.println("Msg has been sent.");

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

}
