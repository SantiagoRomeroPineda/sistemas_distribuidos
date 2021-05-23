package com.Ernesto.COMPUTADOR1.GC;


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

        System.out.println("Inicando GC 1");
        
        ZMQ.Socket socket1 = context.createSocket(SocketType.REP);
        ZMQ.Socket publisher = context.createSocket(SocketType.PUB);

        socket1.bind("tcp://"+ipComputadora1+":5553");

        publisher.bind("tcp://"+ipComputadora1+":5554");
        int turno=0;
        while (!Thread.currentThread().isInterrupted()) {         
          byte[] reply = socket1.recv(0);
          String contents=new String(reply, ZMQ.CHARSET);    
          StringTokenizer sscanf = new StringTokenizer(contents, " ");
          String solicitud=sscanf.nextToken();   
          String nombre=" ";
          if(sscanf.hasMoreTokens()){
            nombre=sscanf.nextToken();
          }       
          

          if(turno==1){
  
            ZMQ.Socket socket2 = context.createSocket(SocketType.REQ);
            socket2.connect("tcp://"+ipComputadora2+":5555");
            socket2.setReceiveTimeOut(1000);
            socket2.send(contents.getBytes(ZMQ.CHARSET),0);           
            byte[] recv = socket2.recv(0);           
            turno=2;
            if (recv != null) {                
                socket1.send(recv,zmq.ZMQ.ZMQ_DONTWAIT);
            }else{
              turno=3;
            }
            socket2.close();
          }
          if(turno==3){
            turno=0;
          }
          if(solicitud.equals("renovacion") && turno==0){
            String response = "Renovacion Recibida";
            socket1.send(response.getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);
            publisher.sendMore("RENOVACION");
            publisher.send(nombre);
            System.out.println("Se envio renovacion");
            turno=1;          
          }else{
            if(solicitud.equals("devolucion") && turno==0){
              String response = "Devolucion Recibida";
              socket1.send(response.getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);
              publisher.sendMore("DEVOLUCION");
              publisher.send(nombre);
              System.out.println("Se envio devolucion");
              turno=1; 
            }else{
              if(solicitud.equals("solicitud") && turno==0){

 
                ZMQ.Socket socket3 = context.createSocket(SocketType.REQ);
                socket3.connect("tcp://"+ipComputadora1+":5569");
                socket3.send(nombre.getBytes(ZMQ.CHARSET),0);
                //socket3.setReceiveTimeOut(2000);
                byte[] reply1 = socket3.recv(0);        
                if(reply1!=null){
                  contents=new String(reply1, ZMQ.CHARSET);  
                  socket1.send(contents.getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);
                  System.out.println(contents);
                }else{
                  socket1.send("fallo".getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);
                  System.out.println("fallo");
                }       
                socket3.close();
                turno=1; 

              }else{
                if(turno==0){
                  String response = "Solicitud invalida";
                  socket1.send(response.getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);
                  System.out.println("Solicitud invalida");
                  turno=1; 
                }
              }
            }
          } 
          if(turno==2){
            turno=0;
          }
       
        }
      }catch(Exception e){
        e.printStackTrace();
      }   
  }
}