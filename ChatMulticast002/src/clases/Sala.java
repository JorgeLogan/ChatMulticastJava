package clases;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import paquetes.PaqueteChat;
import paquetes.PaqueteSala;

/**
 * La sala con los metodos que necesita para ser mas independiente.
 * Tambien se encarga de pasar la propia sala al listado de salas disponibles
 * @author Jorge
 *
 */
public class Sala extends Thread {
	// Atributos para la sala
	private String creador;
	private InetAddress grupo;
	private String direccion;
	private String ipEmisor;
	private String nombreSala;
	private int puerto;
	private int tamMaximoBuffer;
	private MulticastSocket socket;
	
	// Atributos para el hilo
	private boolean salirSala = false;
	
	// Tendra mensajes de publicidad cada poco para darle vida.
	// Se deben crear despues de configurar la clase.
	private String[] publi; 
		
	// Constructor con los parametros
	public Sala(String creador, String ip, String direccionGrupo, String nombreSala, int puerto, int tamMaximoBuffer) {
		super();
		try {
			this.creador = creador;
			this.ipEmisor = ip;
			this.direccion = direccionGrupo;
			this.grupo = InetAddress.getByName(direccionGrupo);
			this.nombreSala = nombreSala;
			this.puerto = puerto;
			this.tamMaximoBuffer = tamMaximoBuffer;	
									
			// Abrimos el hilo
			this.start();
		
		} 
		catch (UnknownHostException e) {
			System.out.println("No se pudo crear/conectar la sala: " + e.getMessage() );
			this.salirSala = true;
		}
	}

	// Constructor a base de paquete de sala
	public Sala(PaqueteSala paquete) {
		super();
		try {
			this.creador = paquete.getCreador();
			this.ipEmisor = paquete.getIpRemota();
			this.direccion = paquete.getGrupo();
			this.grupo = InetAddress.getByName(this.direccion);
			this.nombreSala = paquete.getNombre();
			this.puerto = paquete.getPuerto();
			this.tamMaximoBuffer = paquete.getTamMaxBuffer();	
									
			// Abrimos el hilo
			this.start();
		
		} 
		catch (Exception e) {
			System.out.println("No se pudo crear/conectar la sala: " + e.getMessage() );
			this.salirSala = true;
		}
	}
	
	/**
	 * Para enviar mensajes a la sala
	 * @param paquete el paquete de datos que enviaremos
	 */
	public void enviarMensaje(PaqueteChat paquete) {
		try {
			// Preparamos el multicast
			MulticastSocket socket = new MulticastSocket();
			
			// Nos unimos al grupo
			socket.joinGroup(grupo);
			
			// Preparamos el flujo de bytes para el envio
			byte[] paqueteBytes = FuncionesConversion.convertirPaquete(paquete);
					
			// Enviamos el datagrama con el paqute
			DatagramPacket datagrama = new DatagramPacket(paqueteBytes, paqueteBytes.length ,this.grupo, this.puerto);
			socket.send(datagrama);
			
			// Abandonamos el grupo y cerramos socket
			socket.leaveGroup(grupo);
			socket.close();		
			
		} catch (IOException e) {
			System.out.println("No se pudo enviar el mensaje: " + e.getMessage());
		}
	}

	/**
	 * Para la funcion de escucha mensajes, que seran de tipo PaqueteChat
	 * 
	 * @return un paquete chat con los datos recibidos
	 */
	public PaqueteChat escucharMensaje() {
		// Preparamos el paquete a devolver
		PaqueteChat paquete = new PaqueteChat();
		
		try {
			// Creo el multicast para leer el objeto, con el puerto que queremos escuchar
			socket = new MulticastSocket(this.puerto);
			
			// Configuramos la IP del grupo de conexion
			InetSocketAddress grupoSocket = new InetSocketAddress(this.grupo, this.puerto);
			NetworkInterface network = NetworkInterface.getByName(this.ipEmisor);
			
			// Nos unimos al grupo SOCKET y al network
			socket.joinGroup(grupoSocket, network);
			
			
			// Preparamos un flujo de bytes donde guardar la escucha
			byte[] buffer = new byte[this.tamMaximoBuffer];
			
			// Preparamos el datagrama que escuchara los datos
			DatagramPacket datagrama = new DatagramPacket(buffer, buffer.length);
			
			// Y nos ponemos el socket a la escucha
			socket.receive(datagrama);
			
			// Despues de recibirlo, abandonamos el grupo
			socket.leaveGroup(grupo);
			
			// Cerramos el socket
			socket.close();
			
			// Obtenemos el paquete con la funcion que creamos
			paquete = FuncionesConversion.extraerPaquete(buffer);
			
		} catch (IOException e) {
			System.out.println("Error al leer el paquete de entrada en la sala " + this.nombreSala 
					+ ": " + e.getMessage());
			paquete = null; // Devuelvo null para que de una excepcion fuera
		}
		
		return paquete;
	}
	
	// Para cerrar la sala
	public void cerrarSala() {
		this.salirSala = true;
		// Para evitar quedar colgado como me paso a veces, cierro el socket para causar excepcion
		try {
			this.socket.close();
			this.socket = null;	
		}
		catch(Exception e) {}
	}
	
	/**
	 * Clase para darle vida al chat sin estar escirbiendo en los clientes constantemente
	 */
	private void crearPubli() {
		this.publi = new String[] {
				Emojis.RISAS + "&nbsp; Bienvenid@ al chat &nbsp;" + Emojis.RISAS + "<br>" 
					+ this.nombreSala + " en el puerto " + this.puerto 
					+ " en la direccion " + this.direccion + " en el emisor " + this.ipEmisor 
					+ " con un tamaño de mensaje de " + this.tamMaximoBuffer + " bytes!!",
			"Chat " + this.nombreSala + " en el puerto " + this.puerto + " con un tamaño de " 
					+ this.tamMaximoBuffer + " maximo",
			"Chat " + this.nombreSala + " tu chat!!" + Emojis.CORAZON,
			"Disfuta del chat " + this.nombreSala + ", el mejor chat de todos!",
			"Chat " + this.nombreSala + " el unico que no tiene mensajes automáticos!!",
			"Blablabla... ¿Eres una cotorra?? &nbsp ESTE ES TU SITIO!!" + Emojis.ENCANTADO
		};
	}
	
	/**
	 * Metodo de la ejecucion del hilo, que emitira propaganda de la emisora
	 */
	@Override 
	public void run() {
		// Preparamos la publi
		this.crearPubli();
		
		System.out.println("Comienza emision UDP de la sala " + this.nombreSala);
		
		// Y empezamos a emitirla mientras la sala siga viva
		PaqueteChat paquete = new PaqueteChat();
		paquete.setNombreUsuario(this.nombreSala);

		// Preparamos un indice para ir pasando los mensajes
		int i= 0; 
		
		while(this.salirSala == false) {
			try {
				
				paquete.setMensaje(publi[i]);
				this.enviarMensaje(paquete);
				
				if(i >= this.publi.length -1) i = 0;
				else i++;
				
				sleep(10 * 1000);
			} 
			catch (Exception e) {
				System.out.println("Error en el bucle de publi de la sala " +
						this.nombreSala + ": " + e.getMessage());
				this.salirSala = true;
			}
		}
		System.out.println("\n<--- <--- Cerrada la sala " + this.nombreSala + "\n");
	}

	/**
	 * Para extraer un paquete con los datos de la sala
	 * @return un objeto de PaqueteSala con los datos de la sala
	 */
	public PaqueteSala getPaqueteSala() {
		PaqueteSala paquete = new PaqueteSala();
		paquete.setCreador(creador);
		paquete.setGrupo(direccion);
		paquete.setIpRemota(ipEmisor);
		paquete.setNombre(nombreSala);
		paquete.setPuerto(puerto);
		paquete.setTamMaxBuffer(tamMaximoBuffer);
		return paquete;
	}
	
	private String getNombreSala() {
		return this.nombreSala;
	}
}
