package com.ivk23.networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient {

    public static void main(String[] args) throws InterruptedException {
        // first server (e.g from MultiUserServer) has to be started :)
        while (true) {
            try (var client = new Socket(InetAddress.getLocalHost(), 5000);
                 var reader = new BufferedReader(
                         new InputStreamReader(client.getInputStream()));
                 var writer = new PrintWriter(client.getOutputStream())) {

                writer.println("[CLIENT] Hello! What time is it?");
                System.out.println(reader.readLine());

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Thread.sleep(5000);
        }
    }

}
