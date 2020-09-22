package com.ivk23.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

import static java.lang.String.format;

public class SimpleServer {

    public static void main(String[] args) {
        try (var serverSocket = new ServerSocket(3000)) {
            log("Server running at port %d", serverSocket.getLocalPort());

            final Socket socket = serverSocket.accept();
            log("New client connected");

            final var writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("Hello from Server. Time: " + LocalDateTime.now());

            final var reader = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream())
            );

            log(reader.readLine()); // read msg from client ^_^

            writer.close();
            reader.close();
            socket.close();

            log("Server stopped");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void log(final String msg, Object... args) {
        System.out.println(format(msg, args));
    }

}
