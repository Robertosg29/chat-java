package data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rober y Cristina
 */
public class GestionaCliente implements Runnable {

    Socket s;
    DataInputStream in = null;
    DataOutputStream out = null;
    boolean finServidor = false;
    String nombreCliente;
    final int COD;

    public GestionaCliente(Socket s) {

        COD = Servidor.cod_cliente++;
        this.s = s;
        try {
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(GestionaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            while (!finServidor) {
                try {
                    s.setSoTimeout(1000);
                    String cad = in.readUTF();
                    System.out.println("Cliente dice : " + cad);
                    recibirCliente(cad);
                } catch (IOException iOException) {
                }
            }
        } finally {
            try {

                out.close();
                in.close();
                s.close();
                Servidor.gClientes.remove(this);//OJOOOOO QUE SI NO BORRAS AL GESTIONACLIENTE DE LA LISTA, TE SALTAN
                //400 EXCEPCIONES PORQUE NO PUEDE USAR LOS FLUJOS EN LOS METODOS QUE RECORREN LA LISTA
                System.out.println("Se ha finalizado el chat.");
            } catch (IOException ex) {
                System.out.println("Algún flujo no puede cerrarse.");
            }
        }
    }

    private void recibirCliente(String cad) {
        try {
            //0|"MENSAJE"
            String[] msj = cad.split(Protocolo.SEPARADOR);
            switch (msj[0]) {
                case "" + Protocolo.MENSAJE_C:
                    enviarMensajeAClientes(msj[1]);
                    break;
                case "" + Protocolo.FIN_CLIENTE:
                    finServidor = true;
                    enviarMensajeAClientesBajaCliente();
                    System.out.println("Fin conexion con cliente.");

                    break;
                case "" + Protocolo.USUARIO_CONECTADO_C:
                    nombreCliente = msj[1];
                    if (existeClienteConEseNombre(nombreCliente)) {
                        out.writeUTF(Protocolo.NOMBRE_YA_EXISTE_S + Protocolo.SEPARADOR + "Ya existe un cliente con ese nombre, debes introducir otro");
                        out.flush();
                    } else {
                        enviarMensajeAClientesNuevoCliente(msj[1]);
                    }
                    break;
                    case "" + Protocolo.FIN_CLIENTE_SIN_CONECTAR:
                    finServidor = true;
                    System.out.println("Fin conexion con cliente.");

                    break;
                case "" + Protocolo.MENSAJE_PRIVADO_C:
                    enviarMensajePrivado(msj);
                    break;
            }
            if (!finServidor) {
                out.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(GestionaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enviarMensajeAClientesNuevoCliente(String mensaje) {
        for (GestionaCliente gCliente : Servidor.gClientes) {
            if (gCliente.equals(this)) {
                try {
                    out.writeUTF(Protocolo.ASIGNAR_CODIGO_S + Protocolo.SEPARADOR + COD);
                    out.flush();
                    out.writeUTF(Protocolo.LISTA_USUARIOS_CONECTADOS + usuariosConectados());
                    out.flush();
                    out.writeUTF(Protocolo.MENSAJE_S + Protocolo.SEPARADOR + "Bienvenid@ " + nombreCliente);
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(GestionaCliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    if (gCliente.getNombreCliente() != null) {
                        gCliente.out.writeUTF(Protocolo.USUARIO_CONECTADO_S + Protocolo.SEPARADOR + nombreCliente + Protocolo.SEPARADOR2 + COD);
                        gCliente.out.flush();
                    }
                    //gCliente.out.writeUTF(Protocolo.MENSAJE_S + Protocolo.SEPARADOR + "\nSe ha unido " + nombreCliente);
                    //gCliente.out.flush();

                } catch (IOException ex) {
                    Logger.getLogger(GestionaCliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

    private void enviarMensajeAClientes(String mensaje) {
        for (GestionaCliente gCliente : Servidor.gClientes) {
            if (gCliente.equals(this)) {
                try {
                    out.writeUTF(Protocolo.MENSAJE_S + Protocolo.SEPARADOR + "\nYo-> " + mensaje);
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(GestionaCliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    if (gCliente.getNombreCliente() != null) {
                        gCliente.out.writeUTF(Protocolo.MENSAJE_S + Protocolo.SEPARADOR + "\n" + nombreCliente + "-> " + mensaje);
                        gCliente.out.flush();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(GestionaCliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void enviarMensajeAClientesBajaCliente() {
        for (GestionaCliente gCliente : Servidor.gClientes) {
            if (!gCliente.equals(this)) {
                try {
                    if (gCliente.getNombreCliente() != null) {

                        gCliente.out.writeUTF(Protocolo.ELIMINAR_USUARIO_DESCONECTADO_S + Protocolo.SEPARADOR + COD);
                        gCliente.out.flush();
                        gCliente.out.writeUTF(Protocolo.MENSAJE_S + Protocolo.SEPARADOR + "\n" + nombreCliente + " se ha desconectado.");
                        gCliente.out.flush();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(GestionaCliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private String usuariosConectados() {
        String cad = "";
        for (GestionaCliente gCliente : Servidor.gClientes) {
            if (!gCliente.equals(this)) {
                if (gCliente.getNombreCliente() != null) {
                    cad += Protocolo.SEPARADOR + gCliente.getNombreCliente() + Protocolo.SEPARADOR2 + gCliente.getCod();
                }
            }
        }
        return cad;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public int getCod() {
        return COD;
    }

    private void enviarMensajePrivado(String[] msj) {
        try {
            //EJ 9||CODEMISOR||CODRECEPTOR||MENSAJE //gestionamos el mensaje recibido por el cliente
            //buscamos al GC que coincide con el codigo receptor, porque será el que debe enviar el mensaje a su cliente
            GestionaCliente gAux = buscarGestionaCliente(Integer.parseInt(msj[2]));
            //EJ 10||CODEMISOR||MENSAJE ahora enviamos al cliente el codigo del emisor mas el mensaje
            gAux.out.writeUTF(Protocolo.MENSAJE_PRIVADO_S + Protocolo.SEPARADOR + msj[1] + Protocolo.SEPARADOR + msj[3]);
            gAux.out.flush();
        } catch (IOException ex) {
            Logger.getLogger(GestionaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private GestionaCliente buscarGestionaCliente(int cod) {
        for (GestionaCliente gCliente : Servidor.gClientes) {
            if (gCliente.getCod() == cod) {
                return gCliente;
            }
        }
        return null;
    }

    private boolean existeClienteConEseNombre(String nombre) {
        for (GestionaCliente gCliente : Servidor.gClientes) {
            if (!gCliente.equals(this)) {
                if (gCliente.getNombreCliente().compareToIgnoreCase(nombre) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

}
