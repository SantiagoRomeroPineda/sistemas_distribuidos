package com.Ernesto.COMPUTADOR2.Actores;

import java.util.StringTokenizer;


import java.io.*;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;
import java.text.SimpleDateFormat;
import java.util.Date; 



class ActorRenovacion
{

    public static void main(String[] args)
    {
       
        try (ZContext context = new ZContext()) {

            String ipComputadora1="127.0.0.1";
            String ipComputadora2="127.0.0.1";

            System.out.println("Inicando AR2 ");
            Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://"+ipComputadora2+":5556");
            subscriber.subscribe("RENOVACION".getBytes(ZMQ.CHARSET));

            while (!Thread.currentThread().isInterrupted()) {
                
                String address = subscriber.recvStr();
                
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
        }
    }
    
}