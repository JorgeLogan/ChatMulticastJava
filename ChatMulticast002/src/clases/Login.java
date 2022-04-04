package clases;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;

import hilos_escucha.HiloEscucha;
import paquetes.PaqueteChat;
import paquetes.PaqueteLogin;
import paquetes.PaqueteRespuesta;
import paquetes.PaqueteSala;

/**
 * Clase para el servidor que se ponga a la escucha de nuevos clientes
 * @author Jorge
 *
 */
public class Login extends HiloEscucha implements Serializable{
	/**
	 * Requerido por el id para la serializacion
	 */
	private static final long serialVersionUID = 1L;
	// Atributos
	private ObjectInputStream objEntrada; // Para el flujo de entrada de los objetos
	private ObjectOutputStream objSalida; // para el flujo de salida de los objetos
	private PaqueteSala paqueteSala; // El paquete con los datos que recibiran los clientes aceptados
	private ServerSocket serverSocket;
	private int puerto;
	//private List<String> listadoUsuarios;
	private DefaultListModel<String> listaUsuarios;
	
	// Constructor
	public Login(PaqueteSala paquete, int puertoSocket, DefaultListModel<String> listaUsuarios) {
		this.paqueteSala = paquete;
		this.puerto = puertoSocket;
		this.listaUsuarios = listaUsuarios;
		
		try {
			this.serverSocket = new ServerSocket(puerto);			
			this.start();
		}
		catch (Exception e) {
			System.out.println("Error al intentar abrir el serversocket TCP: " + e.getMessage());
		}
	}
	
	/**
	 * Metodo para la escucha de clientes TCP. Pondremos al hilo a escuchar a sockets entrantes
	 */
	Socket sCliente = null;
	@Override
	public synchronized void escucha() {
		try {
			System.out.println("Iniciada espera por clientes TCP...");
			// Escuchamos clientes
			sCliente = this.serverSocket.accept();
		
			System.out.println("RECIBO PAQUETE");
			
			// Leemos el paquete que envio el cliente a traves del socket
			PaqueteLogin paqueteRecibido = this.leerPaqueteRecibido(sCliente);
			
			System.out.println("Recibido paquete en el Login: " + paqueteRecibido.toString());
			
			// Y le enviamos lo que corresponda segun este paquete
			this.enviarRespuesta(paqueteRecibido, sCliente);
			
		}
		catch (Exception e) {
			System.out.println("Error en el Login al escuchar clientes entrantes: " + e.getMessage());
		}
	}
	
	/**
	 * Funcion para devolver un paquete de login recibido por un cliente
	 * @param sCliente el cliente que envia la informacion
	 * @return un PaqueteLogin con los datos enviados del cliente
	 * @throws Exception
	 */
	private PaqueteLogin leerPaqueteRecibido(Socket sCliente) throws Exception {
		PaqueteLogin paquete = null;
		// Si llegamos aqui, es que hemos recibido algo. Asi que desciframos el paquete
		this.objEntrada = new ObjectInputStream(sCliente.getInputStream());
					
		// Lo convertimos a nuestra clase de paquete de login
		paquete = (PaqueteLogin) objEntrada.readObject();
		
		return paquete;
	}
	
	// Preparamos un paquete respuesta segun el paquete recibido
	private PaqueteRespuesta elaborarRespuesta(PaqueteLogin paqueteRecibido) {
		// Preparamos un paquete respuesta para el cliente
		PaqueteRespuesta respuesta = new PaqueteRespuesta();
		
		System.out.println("Elaboraremos respuesta segun el paquete recibido: " + paqueteRecibido.toString());
		
		// Comprobamos si el cliente ya existe y se quiere desconectar
		if(paqueteRecibido.isDesconectar()) {
			// Preparamos un mensaje de vuelta
			System.out.println("El cliente quiere desconectar");
			respuesta.setMensaje("Gracias por tu tiempo! Esperamos verte pronto, " 
					+ paqueteRecibido.getNick());
			// Lo eliminamos del listado
			this.listaUsuarios.removeElement(paqueteRecibido.getNick());
		}
		else {
			System.out.println("El cliente quiere logearse");
			// No se busca desconexion, sino Login, asi que vamos a ver si ya existe el nick
			if(this.listaUsuarios.contains(paqueteRecibido.getNick()) == false) {
				// Aceptado
				System.out.println("Aceptado");
				this.listaUsuarios.addElement(paqueteRecibido.getNick());
				respuesta.setPaqueteSala(this.paqueteSala);
				respuesta.setMensaje("Bienvenid@, " + paqueteRecibido.getNick());
				respuesta.setAceptado(true);
			}
			else {
				// No se puede aceptar, ya existe el nick
				System.out.println("No aceptado");
				respuesta.setMensaje("Lo siento, busca otro nick mas Original!!");
				respuesta.setAceptado(false);
			}
		}
		// Devolvemos el paquete elaborado
		return respuesta;
	}
	
	// Funcion para enviar mensajes a los clientes
	private void enviarRespuesta(PaqueteLogin paqueteRecibido, Socket sCliente) {
		// Preparamos la respuesta segun lo que requiera el paquete
		PaqueteRespuesta respuesta = this.elaborarRespuesta(paqueteRecibido);
		System.out.println("paquete respuesta extraido--> " + respuesta.toString());
		// Y la enviamos al cliente
		try {
			this.objSalida = new ObjectOutputStream(sCliente.getOutputStream());
			this.objSalida.writeObject(respuesta);
			
			System.out.println("Respuesta enviada " + respuesta.toString());
		} 
		catch (IOException e) {
			System.out.println("Error al enviar datos al cliente. Le cierro el socket");
			try {
				sCliente.close();
			} catch (IOException e1) {
				System.out.println("Error al cerrar el socket del cliente");
			}
		}		
	}	
	
	// Sobreescribimos el método de cerrar hilo, para cerrar los flujos tambien
	@Override
	public void cerrarHilo() {
		this.salirHilo = true;
		
		// Cerramos los flujos
		try {
			if(this.objEntrada!= null) this.objEntrada.close();
			if(this.objSalida!= null) this.objSalida.close();
		} catch (IOException e) {
			System.out.println("Error al cerrar los flujos en la salida del hilo de login");
		}
		
		try {
			this.serverSocket.close();
		} catch (Exception e) {
			System.out.println("Error al intentar cerrar el serversocket del servidor: " + e.getMessage());
		}
		
		this.serverSocket = null;
		System.out.println("Hilo de escucha de Login (TCP) desde el servidor, cerrado");
	}
}
