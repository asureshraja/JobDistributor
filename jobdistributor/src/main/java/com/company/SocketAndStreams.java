package com.company;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

/**
 * Created by suresh on 12/6/15.
 */
public class SocketAndStreams{
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket s) {
        this.socket = s;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public BufferedWriter getOut() {
        return out;
    }

    public void setOut(BufferedWriter out) {
        this.out = out;
    }
}
