
package data;

/**
 *
 * @author Rober y Cristina
 */
public interface Protocolo {
    
 public static String SEPARADOR=":";
    public static String SEPARADOR2=",";
    public static int MENSAJE_C=0; // Estos mensajes son desde el Cliente
    public static int MENSAJE_S=1; // Estos mensajes son desde el Servidor
    public static int FIN_CLIENTE=2; // Este mensaje lo manda el Cliente cuando cierra su ventana o clicka en salir
    public static int FIN_SERVIDOR=3; // Este mensaje lo manda el Servidor Principal cuando finaliza su run
    public static int USUARIO_CONECTADO_S=4; //Este mensaje se manda desde el servidor a todos los clientes cuando se conecta un nuevo Cliente
    public static int USUARIO_CONECTADO_C=5; // Este mensaje lo manda el cliente al servidor cuando inicia su ejecucion(es el primer mensaje que manda)
    public static int LISTA_USUARIOS_CONECTADOS=6;//Este mensaje lo manda el servidor para cuando se conecta un nuevo cliente sepa los usuarios que ya estan conectados
    public static int ASIGNAR_CODIGO_S=7;//Este mensaje le da un código al cliente desde el servidor
    public static int ELIMINAR_USUARIO_DESCONECTADO_S=8;//Este mensaje lo envia el servidor a todos los clientes cuando uno se desconecta, para que lo eliminen de su lista
    public static int MENSAJE_PRIVADO_C=9;//Este mensaje lo envia el cliente al servidor y contiene codreceptor,emisor y el mensaje
    public static int MENSAJE_PRIVADO_S=10;// Este mensaje lo envia el GC que recibe el mensaje del cliente(emisor) para enviar a su cliente mensaje+codDelemisor
    public static int NOMBRE_YA_EXISTE_S=11;// Este mensaje lo envía el servidor si ya existe un cliente con ese hombre cuando se conecta
    public static int FIN_CLIENTE_SIN_CONECTAR=12; //Este mensaje se envia cuando un cliente se desconecta sin llegar a introducir el nombre
}
