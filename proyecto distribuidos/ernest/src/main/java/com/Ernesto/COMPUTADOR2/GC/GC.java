package com.Ernesto.COMPUTADOR2.GC;

import java.util.StringTokenizer;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class GC
{
  public static void main(String[] args) throws Exception
  {
    try {
        ZContext context = new ZContext();
        String ipComputadora1="127.0.0.1";
        String ipComputadora2="127.0.0.1";
        System.out.println("Inicando GC 2");
        
        ZMQ.Socket socket = context.createSocket(SocketType.REP);
        ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
        
        socket.bind("tcp://"+ipComputadora2+":5555");
        publisher.bind("tcp://"+ipComputadora2+":5556");
        while (!Thread.currentThread().isInterrupted()) {
            byte[] reply = socket.recv(0);
            String contents=new String(reply, ZMQ.CHARSET);    
            StringTokenizer sscanf = new StringTokenizer(contents, " ");
            String solicitud=sscanf.nextToken();  
            String nombre=" ";
            if(sscanf.hasMoreTokens()){
              nombre=sscanf.nextToken();
            } 
            if(solicitud.equals("renovacion")){
              String response = "Renovacion Recibida";
              socket.send(response.getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);
              publisher.sendMore("RENOVACION");
              publisher.send(nombre);
              System.out.println("Se envio renovacion");          
            }else{
              if(solicitud.equals("devolucion")){
                String response = "Devolucion Recibida";
                socket.send(response.getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);
                publisher.sendMore("DEVOLUCION");
                publisher.send(nombre);
                System.out.println("Se envio devolucion");
              }else{
                if(solicitud.equals("solicitud")){

                  ZMQ.Socket socket3 = context.createSocket(SocketType.REQ);
                  socket3 = context.createSocket(SocketType.REQ);
                  socket3.connect("tcp://"+ipComputadora2+":5560");
                  socket3.send(nombre.getBytes(ZMQ.CHARSET),0);
                  socket3.setReceiveTimeOut(4200);
                  byte[] reply1 = socket3.recv(0); 
                  socket3.close(); 
                  if(reply1!=null){
                    contents=new String(reply1, ZMQ.CHARSET);  
                    socket.send(contents.getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);
                    System.out.println("Se envio solicitud");
                  }            
                 
                }else{
                  String response = "Solicitud invalida";
                  socket.send(response.getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);
                  System.out.println("Solicitud invalida");
                }
              }
            } 
          
       
        }
      }catch(Exception e){
        e.printStackTrace();
      }   
  }
}