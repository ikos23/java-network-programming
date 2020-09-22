package com.ivk23.networking;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;

import static java.lang.String.format;


public class SimpleWebServer {

    public static void main(String[] args) {
        try (var serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress("localhost", 8080));

            print("Server running at port %d", 8080);
            print("---------------------------");

            while (true) {
                final SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    print("---------------------------");
                    final var reqBuilder = new StringBuilder();
                    final var buffer = ByteBuffer.allocate(1024);
                    int bytesRead = socketChannel.read(buffer);
                    while (bytesRead != -1) {
                        buffer.flip(); // "switch" to read mode

                        while (buffer.hasRemaining()) {
                            reqBuilder.append((char) buffer.get());
                        }

                        if (reqBuilder.toString().contains("\r\n"))
                            break;

                        buffer.clear();
                        bytesRead = socketChannel.read(buffer);
                    }

                    // at this point we have HTTP req start line
                    final var reqString = reqBuilder.toString();
                    final var startLine = reqString.substring(0, reqString.indexOf("\r\n"));
                    print(startLine);

                    final String path = startLine.split(" ")[1];
                    try {
                        final var file = new File("files" + path);
                        if (file.isFile() && file.exists()) {
                            final var data = Files.readAllBytes(file.toPath());
                            socketChannel.write(ByteBuffer.wrap("HTTP/1.1 200 OK\r\n".getBytes()));
                            socketChannel.write(ByteBuffer
                                    .wrap("Content-Type: text/html\r\n".getBytes()));
                            socketChannel.write(ByteBuffer
                                    .wrap(("Content-Length: " + data.length + "\r\n").getBytes()));
                            socketChannel.write(ByteBuffer.wrap("\r\n".getBytes()));
                            socketChannel.write(ByteBuffer.wrap(data));
                        } else {
                            socketChannel.write(ByteBuffer
                                    .wrap("HTTP/1.1 404 Not Found\r\n".getBytes()));
                            socketChannel.write(ByteBuffer
                                    .wrap("Content-Type: text/plain\r\n".getBytes()));
                            socketChannel.write(ByteBuffer.wrap("\r\n".getBytes()));
                            socketChannel.write(ByteBuffer.wrap("404 Not Found".getBytes()));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        socketChannel.write(ByteBuffer
                                .wrap("HTTP/1.1 500 Internal Server Error\r\n".getBytes()));
                    } finally {
                        socketChannel.close();
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void print(final String msg, Object... args) {
        System.out.println(format(msg, args));
    }

}
