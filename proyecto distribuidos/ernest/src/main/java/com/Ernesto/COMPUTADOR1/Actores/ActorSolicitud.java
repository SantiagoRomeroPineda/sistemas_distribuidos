package com.Ernesto.COMPUTADOR1.Actores;


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
        try  {
            String ipComputadora1="127.0.0.1";
            String ipComputadora2="127.0.0.1";

            ZContext context = new ZContext();
            System.out.println("Inicando AS 1");
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://"+ipComputadora1+":5569");
            
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
                byte[] reply2 = socket2.recv(0);
                socket2.close();
                if(reply2!=null){
                    contents=new String(reply2, ZMQ.CHARSET);  
                    socket.send(contents.getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);
                    System.out.println(contents);   
                }else{
                    socket.send("fallo".getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);
                    System.out.println("fallo");
                }


                
                         
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
