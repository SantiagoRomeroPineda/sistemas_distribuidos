package com.grupo5;

import java.util.StringTokenizer;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class GC {
  public static void main(String[] args) throws Exception {
    try (ZContext context = new ZContext()) {
      try (ZContext context2 = new ZContext()) {

        // Socket to talk to clients
        ZMQ.Socket socket = context.createSocket(SocketType.REP);
        ZMQ.Socket publisher = context2.createSocket(SocketType.PUB);
        socket.bind("tcp://192.168.0.23:5553");
        publisher.bind("tcp://*:5554");
        while (!Thread.currentThread().isInterrupted()) {
          byte[] reply = socket.recv(0);
          // Prepare our context and publisher

          // Write two messages, each with an envelope and content
          String contents = new String(reply, ZMQ.CHARSET);
          StringTokenizer sscanf = new StringTokenizer(contents, " ");
          String solicitud = sscanf.nextToken();
          String nombre = sscanf.nextToken();
          if (solicitud.equals("1")) {

            String response = "Renovacion Recibida";
            socket.send(response.getBytes(ZMQ.CHARSET), 0);
            publisher.sendMore("RENOVACION");

            publisher.send(nombre);
            System.out.println("Se envio renovacion");

          }
          if (solicitud.equals("2")) {
            String response = "Devolucion Recibida";
            socket.send(response.getBytes(ZMQ.CHARSET), 0);
            publisher.sendMore("DEVOLUCION");
            publisher.send(nombre);
            System.out.println("Se envio devolucion");
          }
          if (solicitud.equals("3")) {
            String response = "Solicitud Recibida";
            socket.send(response.getBytes(ZMQ.CHARSET), 0);
            System.out.println("Se envio solicitud");
          }

          Thread.sleep(1000); // Do some 'work'
        }
      }
    }
  }
}