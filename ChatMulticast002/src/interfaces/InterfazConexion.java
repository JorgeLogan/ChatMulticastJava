package interfaces;

import java.net.DatagramPacket;
import java.net.Socket;

import paquetes.PaqueteLogin;


/**
 * Interfaz para la conexion. Uso un generico de 2 tipos, porque voy a usar
 * para el envio, PaqueteLogin, y para la recepcion PaqueteSala
 * @author Jorge
 *
 * @param <T> El paquete con el login al servidor; tambien tiene un booleano para avisar de desconexion
 * @param <P> El paquete con los datos de la sala si es aceptado, o un booleano que indica que no es aceptado
 */
public interface InterfazConexion <T,P>{
	public final int PUERTO_TCP = 2000;	
	public final String HOST = "192.168.0.103";
	
	// Creo este mensaje de texto para que en TCP se sepa si se quiere cerrar conexion
	public final String MENSAJE_DESCONEXION = "Cerrar conexion";
	
	// El modo TCP necesita conexion. UDP no
	public boolean conectar();
	public void desconectar();
	
	// Para el envío y recepción de mensajes
	public boolean enviarMensaje(T mensaje); 
	P recibirMensajeTCP(Socket socket);
}