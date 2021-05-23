package com.Ernesto.COMPUTADOR1.BD;

import java.io.*;
import java.util.StringTokenizer;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import java.text.SimpleDateFormat;
import java.util.Date; 

public class serverBD {
    public static void main(String[] args) throws Exception
  {
    try {
        ZContext context = new ZContext();
        String ipComputadora1="127.0.0.1";
        String ipComputadora2="127.0.0.1";
        System.out.println("Inicando BD 1");
        
        ZMQ.Socket socket = context.createSocket(SocketType.REP);
        socket.bind("tcp://"+ipComputadora1+":5557");

        ZMQ.Socket socket2 = context.createSocket(SocketType.REQ);
        socket2.connect("tcp://"+ipComputadora2+":5558");
        socket2.send("actualizar BD".getBytes(ZMQ.CHARSET),zmq.ZMQ.ZMQ_DONTWAIT);
        socket2.close();

        while (!Thread.currentThread().isInterrupted()) {
            byte[] reply = socket.recv(0);
            String contents=new String(reply, ZMQ.CHARSET);    
            StringTokenizer sscanf = new StringTokenizer(contents, " ");
            String solicitud=sscanf.nextToken();          
            String nombre=sscanf.nextToken();
            String respuesta="";

            if(solicitud.equals("renovacion")){
              
              respuesta="renovacion "+nombre;
              renovarLibro(nombre); 
              socket.send(" ", zmq.ZMQ.ZMQ_DONTWAIT); 
              
            }else{
              if(solicitud.equals("devolucion")){
              
                respuesta="devolucion "+nombre;
                devolverLibro(nombre);
                socket.send(" ", zmq.ZMQ.ZMQ_DONTWAIT); 
                 
              }else{
                if(solicitud.equals("solicitud")){
                  
                  String aux=solicitudLibro(nombre);
                  
                  socket.send(aux.getBytes(ZMQ.CHARSET), zmq.ZMQ.ZMQ_DONTWAIT);  

                    
                  sscanf = new StringTokenizer(aux," ");
                  respuesta=sscanf.nextToken(); 
                  String respuesta2=sscanf.nextToken(); 
                  if(respuesta2.equals("Satisfactoria")){
                    respuesta="renovacion "+respuesta;  
                    socket2 = context.createSocket(SocketType.REQ);
                    socket2.connect("tcp://"+ipComputadora2+":5558");
                    socket2.send(respuesta.getBytes(ZMQ.CHARSET),zmq.ZMQ.ZMQ_DONTWAIT);
                    socket2.close();
                  }
                  

                }else{

                  if(solicitud.equals("actualizar")){
                    File archivo = null;
                    RandomAccessFile fichero=null;
                    socket2 = context.createSocket(SocketType.REQ);
                    socket2.connect("tcp://"+ipComputadora2+":5558");
                    socket2.send("1 1".getBytes(ZMQ.CHARSET),zmq.ZMQ.ZMQ_DONTWAIT);
                    socket2.close();
                    try {
                          archivo = new File ("C:\\Users\\PC\\Desktop\\distr\\proyecto distribuidos\\ernest\\src\\main\\java\\com\\Ernesto\\COMPUTADOR1\\BD\\Libros.txt");
                          fichero = new RandomAccessFile(archivo, "rw");
                          String linea= fichero.readLine(); 
                          while(linea!=null){                                                 
                            if(linea!=null){
  
                              socket2 = context.createSocket(SocketType.REQ);
                              socket2.connect("tcp://"+ipComputadora2+":5558");
                              socket2.send(linea.getBytes(ZMQ.CHARSET),zmq.ZMQ.ZMQ_DONTWAIT);
                              socket2.close();
                            }
                            linea= fichero.readLine();  
                                     
                          }
                          Thread.sleep(300);
                          socket2 = context.createSocket(SocketType.REQ);
                          socket2.connect("tcp://"+ipComputadora2+":5558");
                          socket2.send("-1 -1".getBytes(ZMQ.CHARSET),zmq.ZMQ.ZMQ_DONTWAIT);
                          socket2.close();
                        }
                        catch(Exception e){
                          e.printStackTrace();
                        }finally{
                          try{                    
                            if( null != fichero ){   
                                fichero.close();  
                            }                  
                          }catch (Exception e2){ 
                            e2.printStackTrace();
                          }
                        }





                  }else{
                    if(solicitud.equals("1")){     
                      try {
                           File archivo = null;
                           RandomAccessFile fichero=null;
                           archivo = new File ("C:\\Users\\PC\\Desktop\\distr\\proyecto distribuidos\\ernest\\src\\main\\java\\com\\Ernesto\\COMPUTADOR1\\BD\\Libros.txt");
                           fichero = new RandomAccessFile(archivo, "rw");
                           reply = socket.recv(0);
                           String linea=new String(reply, ZMQ.CHARSET);  
                           StringTokenizer sscanf2 = new StringTokenizer(linea," ");
                           String aux=sscanf2.nextToken();
                           if(!(aux.equals("actualizar")||(aux.equals("solicitud")) ||(aux.equals("devolucion"))||(aux.equals("renovacion") ))){
                            fichero.writeBytes(linea); 
                          }         
                           while(linea!=null ){                            
                                reply = socket.recv(0);  
                                linea=new String(reply, ZMQ.CHARSET); 
                                sscanf2 = new StringTokenizer(linea," ");
                                aux=sscanf2.nextToken();                                 
                                if (linea.equals("-1 -1")){                                 
                                    linea=null;
                                    break;
                                }else{

                                  if(!(aux.equals("actualizar")||(aux.equals("solicitud")) ||(aux.equals("devolucion"))||(aux.equals("renovacion") ))){
                                    fichero.writeBytes("\n");  
                                    fichero.writeBytes(linea); 
                                  }                               
                                }                                                                                                                       
                           }
                           fichero.close();  

                            
                      }
                      catch(Exception e){
                         e.printStackTrace();
                      }
                    }
                  }
                }
              }
            } 
            
            
   

        
        }
      }catch(Exception e){
        e.printStackTrace();
      }   
  }
  public static String solicitudLibro(String peticion) {
    File archivo = null;
    String nombreLibroSolicitud=peticion;
    RandomAccessFile fichero=null;
    long pos=0;
    String respuesta="No existencia";
    int primeraVez=1;
    int encontrado=0;
    int totalNumero=0;
    int librosPrestamo=0;
    try {
        archivo = new File ("C:\\Users\\PC\\Desktop\\distr\\proyecto distribuidos\\ernest\\src\\main\\java\\com\\Ernesto\\COMPUTADOR1\\BD\\Libros.txt");
        fichero = new RandomAccessFile(archivo, "rw");
        String idAux=null;

        
        pos = 0;
        String linea= fichero.readLine();             
        String nombre;
        String id;
        String prestado;
        while(linea!=null){                
            StringTokenizer sscanf = new StringTokenizer(linea, " ");
            if(sscanf.hasMoreTokens()){
                nombre=sscanf.nextToken();
                id=sscanf.nextToken();
                prestado=sscanf.nextToken();
                if(nombre.equals(nombreLibroSolicitud)){
                    totalNumero=totalNumero+1;                       
                }
                if(nombre.equals(nombreLibroSolicitud)&& prestado.equals("1")){
                    librosPrestamo=librosPrestamo+1;                       
                }
                if(nombre.equals(nombreLibroSolicitud) && primeraVez==1){
                    primeraVez=0;
                    respuesta="Libro en prestamo";
                }
                if(nombre.equals(nombreLibroSolicitud) && prestado.equals("0") && encontrado==0 ){
                    fichero.seek(pos);
                    SimpleDateFormat objSDF = new SimpleDateFormat("dd-MMM-yyyy"); 
                    Date dt_1 = new Date();
                    fichero.writeBytes(nombre+" "+id+" 1 "+objSDF.format(dt_1));
                    respuesta="Satisfactoria";
                    encontrado=1;
                    librosPrestamo=librosPrestamo+1;
                    idAux=id;
                    break;
                }
                pos = fichero.getFilePointer();                   
            }               
            linea= fichero.readLine();                   
        }  
        fichero.close();
        if(idAux!=null){
          respuesta=idAux+" "+respuesta;

        }
                    
    }
    catch(Exception e){
       e.printStackTrace();
    }
    return respuesta;
  }
 

  public static void devolverLibro(String codigoLibroDevolver) {
    File archivo = null;
    long pos=0;
    RandomAccessFile fichero=null;
    try {
         archivo = new File ("C:\\Users\\PC\\Desktop\\distr\\proyecto distribuidos\\ernest\\src\\main\\java\\com\\Ernesto\\COMPUTADOR1\\BD\\Libros.txt");
         fichero = new RandomAccessFile(archivo, "rw");
         
       // Lectura del fichero
          pos = 0;
         String linea= fichero.readLine();
         
         String nombre;
         String id;
         while(linea!=null){
            
             StringTokenizer sscanf = new StringTokenizer(linea, " ");
             if(sscanf.hasMoreTokens()){
                 nombre=sscanf.nextToken();
                 id=sscanf.nextToken();
                 if(id.equals(codigoLibroDevolver)){
                     fichero.seek(pos);
                     fichero.writeBytes(nombre+" "+id+" 0             ");
                 }
                 pos = fichero.getFilePointer();                   
             }               
             linea= fichero.readLine();                   
         }
          
    }
    catch(Exception e){
       e.printStackTrace();
    }finally{
       try{                    
          if( null != fichero ){   
            fichero.close();  
          }                  
       }catch (Exception e2){ 
          e2.printStackTrace();
       }
    }
 }

public static void renovarLibro(String codigoLibroRenovar) {
        File archivo = null;
        long pos=0;
        RandomAccessFile fichero=null;
        try {
             archivo = new File ("C:\\Users\\PC\\Desktop\\distr\\proyecto distribuidos\\ernest\\src\\main\\java\\com\\Ernesto\\COMPUTADOR1\\BD\\Libros.txt");
             fichero = new RandomAccessFile(archivo, "rw");
             
           // Lectura del fichero
              pos = 0;
             String linea= fichero.readLine();
             SimpleDateFormat objSDF = new SimpleDateFormat("dd-MMM-yyyy"); 
             Date dt_1 = new Date();
             String nombre;
             String id;
             while(linea!=null){
                
                 StringTokenizer sscanf = new StringTokenizer(linea, " ");
                 if(sscanf.hasMoreTokens()){
                     nombre=sscanf.nextToken();
                     id=sscanf.nextToken();
                     if(id.equals(codigoLibroRenovar)){
                         fichero.seek(pos);
                         
                         fichero.writeBytes(nombre+" "+id+" 1 "+objSDF.format(dt_1));
                     }
                     pos = fichero.getFilePointer();                   
                 }               
                 linea= fichero.readLine();                   
             }
              
        }
        catch(Exception e){
           e.printStackTrace();
        }finally{
           try{                    
              if( null != fichero ){   
                fichero.close();  
              }                  
           }catch (Exception e2){ 
              e2.printStackTrace();
           }
        }
     }



}
