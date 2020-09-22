package com.ivk23.networking;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

// let's try to impl a very simple REST
// endpoint with no frameworks :)
public class SimpleRestService {

    private static final List<String> testData = new ArrayList<>();

    static {
        testData.add("{ \"id\": 1, \"name\": \"Foo\" }");
        testData.add("{ \"id\": 2, \"name\": \"Bar\" }");
        testData.add("{ \"id\": 3, \"name\": \"Baz\" }");
    }

    private static class Response {
        public static String ok(String body) {
            return format("HTTP/1.1 200 OK%n")
                    + format("Content-Type: application/json%n")
                    + format("%n%n")
                    + body;
        }

        public static String error(String msg) {
            return format("HTTP/1.1 500 Internal Server Error%n")
                    + format("Content-Type: application/json%n")
                    + format("%n%n")
                    + format("{ \"message\": \"%s\"}", msg);
        }
    }

    public static void main(String[] args) {
        try (final var serverSocket = new ServerSocket(5000)) {
            log("Server running at port %d%n", serverSocket.getLocalPort());

            while (true) {
                final Socket socket = serverSocket.accept();

                final var writer = new PrintWriter(socket.getOutputStream(), true);
                final var reader = new InputStreamReader(socket.getInputStream());

                final String req = readLine(reader);
                int contentLength = -1;
                String payload = "";
                if (req.startsWith("POST")) {
                    // why not to read until we get null or -1 or something ?
                    // imagine we make a request from browser, the client (browser)
                    // connects and remains connected waiting for a response.
                    // it's input stream never ends, if we know there will be some
                    // data - we have to know basically how much to read and
                    // read exactly that amount !
                    while (true) {
                        String temp = readLine(reader);
                        if (temp.startsWith("Content-Length")) {
                            contentLength = parseInt(temp.split(":")[1].trim());
                        } else if (temp.equals("\r\n")) {
                            // now it is time to read payload
                            char[] buff = new char[contentLength];
                            int read = reader.read(buff);
                            payload = new String(buff);
                            break;
                        }
                    }
                }

                log("New request: %s%s", req, payload);

                if (req.startsWith("GET /users HTTP/1.1")) {
                    writer.println(Response.ok(getTestData()));
                } else if (req.startsWith("POST /users HTTP/1.1")) {
                    testData.add(payload); // create new user xD
                    writer.println(Response.ok("{\"message\": \"Created :)\"}"));
                } else {
                    writer.println(Response.error("Cannot process your request"));
                }

                writer.close();
                reader.close();
                socket.close();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // this can read lines that end with \r\n
    private static String readLine(final Reader reader) throws IOException {
        final var builder = new StringBuilder("");
        while (true) {
            int nextChar = reader.read();
            if (nextChar == 13) {
                int temp = reader.read();
                builder.append((char) nextChar);
                builder.append((char) temp);
                if (temp == 10) {
                    // line end reached
                    break;
                }
            }
            builder.append((char) nextChar);
        }
        return builder.toString();
    }

    private static void log(final String msg, Object... args) {
        System.out.println(format(msg, args));
    }

    private static String getTestData() {
        return "[" + String.join(",", testData) + "]";
    }
}
