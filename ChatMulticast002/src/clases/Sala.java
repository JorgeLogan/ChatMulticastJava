package clases;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

import paquetes.PaqueteChat;
import paquetes.PaqueteSala;

/**
 * La sala con los metodos que necesita para ser mas independiente
 * @author Jorge
 *
 */
public class Sala extends Thread {
	// Atributos para la sala
	private InetAddress grupo;
	private String direccion;
	private String ipEmisor;
	private String nombreSala;
	private int puerto;
	private int tamMaximoBuffer;
	
	// Atributos para el hilo
	private boolean salirSala = false;
	
	// Tendra mensajes de publicidad cada poco para darle vida.
	// Se deben crear despues de configurar la clase.
	private String[] publi; 
	
	public Sala(String ip, String direccionGrupo, String nombreSala, int puerto, int tamMaximoBuffer) {
		super();
		try {
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

	// Para enviar mensajes a la sala
	public void enviarMensaje(PaqueteChat paquete) {
		try {
			// Preparamos el multicast
			MulticastSocket socket = new MulticastSocket();
			
			// Nos unimos al grupo
			socket.joinGroup(grupo);
			
			// Preparamos el flujo de bytes para el envio
			byte[] paqueteBytes = this.convertirPaquete(paquete);
					
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

	// para recibir mensajes de la sala
	public PaqueteChat escucharMensaje() {
		// Preparamos el paquete a devolver
		PaqueteChat paquete = new PaqueteChat();
		
		try {
			// Creo el multicast para leer el objeto, con el puerto que queremos escuchar
			MulticastSocket socket = new MulticastSocket(this.puerto);
			
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
			paquete = this.extraerPaquete(buffer);
			
		} catch (IOException e) {
			System.out.println("Error al leer el paquete de entrada en la sala " + this.nombreSala 
					+ ": " + e.getMessage());
		}
		
		return paquete;
	}
	
	public byte[] convertirPaquete(PaqueteChat paquete) {
		// Preparamos el flujo donde guardar el paquete convertido
		byte[] buffer = null;
		
		// Preparo el objeto de flujo de array de bytes (que nombre)
		ByteArrayOutputStream objBytesSalida = new ByteArrayOutputStream();
		
		// Preparo el ObjectOutput para grabar los datos
		ObjectOutputStream objSalida;
		try {
			objSalida = new ObjectOutputStream(objBytesSalida);	
			
			// Grabamos el objecto
			objSalida.writeObject(paquete);
			
			// Pasamos al buffer el flujo leido
			buffer = objBytesSalida.toByteArray();
		} 
		catch (IOException e) {
			System.out.println("No se pudo convertir el paquete a bytes[] en la sala " 
					+ this.nombreSala + ": " + e.getMessage());
		}

		// devolvemos el buffer leido o null si no pudo
		return buffer;
	}
	
	
	// Para convertir los bytes leidos en el paquete
	public PaqueteChat extraerPaquete(byte[] arrayBytes) {
		
		PaqueteChat paquete = new PaqueteChat();
		
		// Preparamos el array de bytes
		ByteArrayInputStream arrayEntrada = new ByteArrayInputStream(arrayBytes);
		
		// Usamos un flujo de entrada, porque queremos leer de su flujo parametro
		try {
			ObjectInputStream objEntrada = new ObjectInputStream(arrayEntrada);
			
			// Leemos los datos
			paquete = (PaqueteChat)objEntrada.readObject();
		}
		catch (Exception e) {
			System.out.println("Error al intentar extraer el paquete de datos de la sala " 
					+  this.nombreSala + ": " + e.getMessage());
		}
		
		// Grabamos el paquete o null si fallo algo		
		return paquete;
	}
	
	// Para cerrar la sala
	public void cerrarSala() {
		this.salirSala = true;
	}
	
	private void crearPubli() {
		this.publi = new String[] {
				"Bienvenid@ al chat " + this.nombreSala + " en el puerto " + this.puerto 
					+ " en la direccion " + this.direccion + " en el emisor " + this.ipEmisor 
					+ " con un tamaño de mensaje de " + this.tamMaximoBuffer + " bytes!!",
			"Chat " + this.nombreSala + " en el puerto " + this.puerto + " con un tamaño de " 
					+ this.tamMaximoBuffer + " maximo",
			"Chat " + this.nombreSala + " tu chat!!",
			"Disfuta del chat " + this.nombreSala + ", el mejor chat de todos!",
			"Chat " + this.nombreSala + " el unico que no tiene mensajes automáticos!!"
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
		paquete.setMensaje(this.publi[0]);
		this.enviarMensaje(paquete);
		
		// Preparamos un indice para ir pasando los mensajes
		int i= 1; // ya que el cero acabamos de enviarlo
		
		while(this.salirSala == false) {
			try {
				sleep(4000);
				paquete.setMensaje(publi[i]);
				this.enviarMensaje(paquete);
				
				if(i >= this.publi.length -1) i = 0;
				else i++;
				
			} 
			catch (Exception e) {
				System.out.println("Error en el bucle de publi de la sala " + 
						this.nombreSala + ": " + e.getMessage());
				this.salirSala = true;
			}
		}
		System.out.println("Cerrada la sala " + this.nombreSala);
	}

	// Para extraer un paquete con los datos de la sala
	public PaqueteSala getPaqueteSala() {
		PaqueteSala paquete = new PaqueteSala();
		paquete.setGrupo(direccion);
		paquete.setIpRemota(ipEmisor);
		paquete.setNombre(nombreSala);
		paquete.setPuerto(puerto);
		paquete.setTamMaxBuffer(tamMaximoBuffer);
		return paquete;
	}
}
