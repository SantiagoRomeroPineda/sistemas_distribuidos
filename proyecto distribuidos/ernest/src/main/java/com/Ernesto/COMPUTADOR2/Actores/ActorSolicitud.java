package com.Ernesto.COMPUTADOR2.Actores;


import java.util.StringTokenizer;
import java.io.*;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;
import java.text.SimpleDateFormat;
import java.util.Date; 

public class ActorSolicitud {
    public static void main(String[] args)
    {
        // Prepare our context and subscriber
        try  {
            String ipComputadora1="127.0.0.1";
            String ipComputadora2="127.0.0.1";

            ZContext context = new ZContext();
            System.out.println("Inicando AS 2");
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://"+ipComputadora2+":5560");
            
            while (!Thread.currentThread().isInterrupted()) {
                byte[] reply = socket.recv(0);
                
                String contents=new String(reply, ZMQ.CHARSET);  
                System.out.println("recibi "+contents);
                StringTokenizer sscanf = new StringTokenizer(contents," ");
                String nombre=sscanf.nextToken();
                
                ZMQ.Socket socket2 = context.createSocket(SocketType.REQ);
                socket2.connect("tcp://"+ipComputadora1+":5557");
                contents="solicitud "+nombre;
                socket2.send(contents.getBytes(ZMQ.CHARSET),0);
                socket2.setReceiveTimeOut(1000);
                byte[] reply2 = socket2.recv(0);
                socket2.close();
                if(reply2!=null){
                    contents=new String(reply2, ZMQ.CHARSET);  
                    socket.send(contents.getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);
                    System.out.println(contents);   
                }else{
                    ZMQ.Socket socket3 = context.createSocket(SocketType.REQ);
                    socket3.connect("tcp://"+ipComputadora2+":5558");
                    contents="solicitud "+nombre;
                    socket3.send(contents.getBytes(ZMQ.CHARSET),0);
                    socket3.setReceiveTimeOut(1000);
                    byte[] reply3 = socket3.recv(0);
                    contents=new String(reply3, ZMQ.CHARSET);  
                    socket3.close();
                    socket.send(contents.getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);
                }


                
                         
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
