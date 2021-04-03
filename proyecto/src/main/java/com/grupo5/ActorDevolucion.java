package com.grupo5;

import java.util.StringTokenizer;

import java.io.*;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

/**
 * Pubsub envelope subscriber
 */

class ActorDevolucion {

    public static void main(String[] args) {
        // Prepare our context and subscriber
        try (ZContext context = new ZContext()) {
            Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://localhost:5554");
            subscriber.subscribe("DEVOLUCION".getBytes(ZMQ.CHARSET));

            while (!Thread.currentThread().isInterrupted()) {
                // Read envelope with address
                String address = subscriber.recvStr();
                String contents = subscriber.recvStr();
                StringTokenizer sscanf = new StringTokenizer(contents, " ");
                String codigo = sscanf.nextToken();
                // Read message contents
                devolverLibro(codigo);
                System.out.println(address + " -> codigo: " + codigo);
            }
        }
    }

    public static void devolverLibro(String codigoLibroDevolver) {
        File archivo = null;
        long pos = 0;
        RandomAccessFile fichero = null;
        try {
            archivo = new File("BD.txt");
            fichero = new RandomAccessFile(archivo, "rw");

            // Lectura del fichero
            pos = 0;
            String linea = fichero.readLine();

            String nombre;
            String id;
            while (linea != null) {

                StringTokenizer sscanf = new StringTokenizer(linea, " ");
                if (sscanf.hasMoreTokens()) {
                    nombre = sscanf.nextToken();
                    id = sscanf.nextToken();
                    if (id.equals(codigoLibroDevolver)) {
                        fichero.seek(pos);
                        fichero.writeBytes(nombre + " " + id + " 0             ");
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
