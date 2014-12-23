package com.fullhousedev.multifind.socket;

import com.fullhousedev.multifind.MultiFind;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This is a description of the class.
 *
 * @author Austin Bolstridge (WolfyTheFur).
 */
public class ProxyServer {

    private InetAddress address;
    private int port;
    private Socket socket;
    private MultiFind plugin;

    private BufferedReader reader;
    private BufferedWriter writer;

    public ProxyServer(String address, int port) throws UnknownHostException {
        this.address = InetAddress.getByName(address);
        this.port = port;
    }

    /**
     * Sets up the socket to this proxy for bi-directional communication.
     * @return <code>true</code> if the socket creation succeeded, <code>false</code> otherwise.
     */
    public boolean setupSocket() {
        try {
            socket = new Socket(address, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendData(String data) {
        try {
            writer.write(data);
            writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public InetAddress getAddress() {
        return address;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isSocketClosed() {
        return socket.isClosed();
    }

    public BufferedWriter getWriter() {
        return writer;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public void closeResource() throws IOException {
        this.writer.close();
        this.reader.close();
        this.socket.close();
    }

}
