package com.grupo5;

import java.util.StringTokenizer;
import java.io.*;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;
import java.text.SimpleDateFormat;
import java.util.Date;

class ActorRenovacion {

    public static void main(String[] args) {
        // Prepare our context and subscriber
        try (ZContext context = new ZContext()) {
            Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://localhost:5554");
            subscriber.subscribe("RENOVACION".getBytes(ZMQ.CHARSET));

            while (!Thread.currentThread().isInterrupted()) {
                // Read envelope with address
                String address = subscriber.recvStr();
                // Read message contents
                String contents = subscriber.recvStr();
                StringTokenizer sscanf = new StringTokenizer(contents, " ");
                String codigo = sscanf.nextToken();
                // Read message contents
                renovarLibro(codigo);
                System.out.println(address + " -> codigo: " + codigo);
            }
        }
    }

    public static void renovarLibro(String codigoLibroRenovar) {
        File archivo = null;
        long pos = 0;
        RandomAccessFile fichero = null;
        try {
            archivo = new File("/home/saropi/Trabajo/sistemas_distribuidos/proyecto/DB.txt");
            fichero = new RandomAccessFile(archivo, "rw");

            // Lectura del fichero
            pos = 0;
            String linea = fichero.readLine();
            SimpleDateFormat objSDF = new SimpleDateFormat("dd-MMM-yyyy");

            Date dt_1 = new Date();
            String nombre;
            String id;
            while (linea != null) {

                StringTokenizer sscanf = new StringTokenizer(linea, " ");
                if (sscanf.hasMoreTokens()) {
                    nombre = sscanf.nextToken();
                    if (sscanf.hasMoreTokens()) {
                        id = sscanf.nextToken();
                        if (id.equals(codigoLibroRenovar)) {
                            fichero.seek(pos);

                            fichero.writeBytes(nombre + " " + id + " 1 " + objSDF.format(dt_1));
                        }
                    }
                    pos = fichero.getFilePointer();
                }
                linea = fichero.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta
            // una excepcion.
            try {
                if (null != fichero) {
                    fichero.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}