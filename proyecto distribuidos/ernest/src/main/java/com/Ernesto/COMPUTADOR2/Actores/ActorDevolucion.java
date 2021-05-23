package com.Ernesto.COMPUTADOR2.Actores;

import java.util.StringTokenizer;

import java.io.*;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;



class ActorDevolucion
{

    public static void main(String[] args)
    {
        try {
            String ipComputadora1="127.0.0.1";
            String ipComputadora2="127.0.0.1";

            ZContext context = new ZContext();
            System.out.println("Inicando AD2");
            Socket subscriber = context.createSocket(SocketType.SUB);

            subscriber.connect("tcp://"+ipComputadora2+":5556");
            subscriber.subscribe("DEVOLUCION".getBytes(ZMQ.CHARSET));
            while (!Thread.currentThread().isInterrupted()) {

                String address = subscriber.recvStr();
                String contents = subscriber.recvStr();
                StringTokenizer sscanf = new StringTokenizer(contents, " ");
                String codigo=sscanf.nextToken();

                
                ZMQ.Socket socket = context.createSocket(SocketType.REQ);
                socket.connect("tcp://"+ipComputadora1+":5557");
                contents="devolucion "+contents;
                socket.send(contents.getBytes(ZMQ.CHARSET),zmq.ZMQ.ZMQ_DONTWAIT);
                socket.close();

                ZMQ.Socket socket3 = context.createSocket(SocketType.REQ);
                socket3.connect("tcp://"+ipComputadora2+":5558");
                socket3.send(contents.getBytes(ZMQ.CHARSET),zmq.ZMQ.ZMQ_DONTWAIT);
                socket3.close();  
                       
                System.out.println(address + " -> codigo: " + codigo );
            }
        }catch(Exception e){
            e.printStackTrace();
        }  
    }



}