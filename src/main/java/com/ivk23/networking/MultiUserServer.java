package com.ivk23.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.time.LocalDateTime.now;

public class MultiUserServer {

    public static void main(String[] args) {
        try (var serverSocket = new ServerSocket(5000)) {
            log("Server running at port %d", serverSocket.getLocalPort());

            final ExecutorService executorService = Executors.newFixedThreadPool(3);

            while (true) {
                final Socket socket = serverSocket.accept();

                executorService.submit(() -> {
                    try {
                        log("[%s] New client connected", currentThread().getName());

                        final var io = new IO(socket);
                        io.socketOut.println("Hello from Server. Time: " + now().withNano(0));

                        String data = null;
                        while ((data = io.socketIn.readLine()) != null && data.length() > 0) {
                            log("[%s] %s", currentThread().getName(), data);
                        }

                        io.socketOut.close();
                        io.socketIn.close();
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void log(final String msg, Object... args) {
        System.out.println(format(msg, args));
    }

    private static class IO {

        public PrintWriter socketOut;
        public BufferedReader socketIn;

        public IO(final Socket socket) throws IOException {
            this.socketOut = new PrintWriter(socket.getOutputStream(), true);
            this.socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
    }
}
