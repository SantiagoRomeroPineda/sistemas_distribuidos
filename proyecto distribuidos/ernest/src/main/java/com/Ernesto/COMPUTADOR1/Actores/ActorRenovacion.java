package com.Ernesto.COMPUTADOR1.Actores;

import java.util.StringTokenizer;


import java.io.*;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;
import java.text.SimpleDateFormat;
import java.util.Date; 

/**
 * Pubsub envelope subscriber
 */

class ActorRenovacion
{

    public static void main(String[] args)
    {
        // Prepare our context and subscriber
        try  {
            String ipComputadora1="127.0.0.1";
            String ipComputadora2="127.0.0.1";
            ZContext context = new ZContext();

            System.out.println("Inicando AR1");
            Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://"+ipComputadora1+":5554");
            subscriber.subscribe("RENOVACION".getBytes(ZMQ.CHARSET));

            while (!Thread.currentThread().isInterrupted()) {
                // Read envelope with address
                String address = subscriber.recvStr();
                // Read message contents
                String contents = subscriber.recvStr();
                StringTokenizer sscanf = new StringTokenizer(contents, " ");
                String codigo=sscanf.nextToken();
  
                ZMQ.Socket socket = context.createSocket(SocketType.REQ);
                socket.connect("tcp://"+ipComputadora1+":5557");
                contents="renovacion "+contents;
                socket.send(contents.getBytes(ZMQ.CHARSET),zmq.ZMQ.ZMQ_DONTWAIT);
                socket.close();

                ZMQ.Socket socket2 = context.createSocket(SocketType.REQ);
                socket2.connect("tcp://"+ipComputadora2+":5558");
                socket2.send(contents.getBytes(ZMQ.CHARSET),zmq.ZMQ.ZMQ_DONTWAIT);
                socket2.close();
                System.out.println(address + " -> codigo: " + codigo );
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
}