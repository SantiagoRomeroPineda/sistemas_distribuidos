package com.grupo5;

import java.io.File;
import java.util.Scanner;

import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class PS {
    public static void sendMessage(String msg) {
        // Socket to talk to server
        ZContext context = new ZContext();
        ZMQ.Socket socket = context.createSocket(SocketType.REQ);

        socket.connect("tcp://127.0.0.1:5553");
        System.out.println("mensaje enviando...");
        boolean b = socket.send(msg.getBytes(ZMQ.CHARSET), 0);
        System.out.println("Sent with return: " + b);

        byte[] recv = socket.recv();
        if (recv != null) {
            System.out.println("received: " + new String(recv));
        }
    }

    public static void main(String[] args) {

        try {
            Scanner input = new Scanner(new File("z"));
            while (input.hasNextLine()) {
                String line = input.nextLine();
                sendMessage(line);
            }
            input.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}