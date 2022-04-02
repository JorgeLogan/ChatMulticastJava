package interfaces;


import java.net.DatagramPacket;
import java.net.Socket;

public interface InterfazConexion <T>{
	public final int PUERTO_TCP = 2000;	
	public final String HOST = "192.168.0.103";
	
	// Creo este mensaje de texto para que en TCP se sepa si se quiere cerrar conexion
	public final String MENSAJE_DESCONEXION = "Cerrar conexion";
	
	// El modo TCP necesita conexion. UDP no
	public boolean conectar();
	public void desconectar();
	
	// Para el envio de mensajes por TCP necesitamos sockets. Por TCP envio solo strings (nicks)
	public boolean enviarMensajeTCP(String mensaje);
	public boolean enviarMensajeTCP(String mensaje, Socket socketEspecifico);
	T recibirMensajeTCP(Socket socket);
	
	// Para el envio/recepcion de mensajes por UDP Datagramas
	public boolean enviarMensajeUDP(T mensaje);
	T recibirMensajeUDP(DatagramPacket datagram);
	
	// Para controlar las escuchas del cliente/servidor
	public void escucha();

}
