package clases;

import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import hilos_escucha.HiloEscucha;
import interfaces.InterfazConexion;
import paquetes.PaqueteChat;
import paquetes.PaqueteLogin;
import paquetes.PaqueteRespuesta;
import paquetes.PaqueteSala;
import vistas.VentanaCliente;

public class Cliente extends VentanaCliente implements InterfazConexion<PaqueteLogin, PaqueteRespuesta>, Serializable{
	
	/**
	 * Sugerido por el IDE
	 */
	private static final long serialVersionUID = 1L;

	// Atributos
	private String nick;
	Socket socket = null;
	ObjectOutputStream objSalida = null;
	ObjectInputStream objEntrada = null;
	boolean conectado = false; // Para que al salir, si esta conectado, libere su nick del server
	
	EscuchaCliente escuchaMulticast = null;
	
	PaqueteSala paqueteSalaActual;
	
	// Para las salas que crea el usuario (y eliminarlas cuando cierre)
	List<Sala> listaSalasUsuario = new LinkedList<Sala>(); 
	
	public Cliente() {
		// Ponemos los botones en modo de conexion desconectado
		this.gestionBotonesConexion();
	}
	
	/**
	 * Funcion para el ejecutable
	 * @param args
	 */
	public static void main(String[] args) {
		new Cliente();
	}
	
	/**
	 * Metodos para la interfaz del entorno gráfico
	 */
	
	/**
	 * Metodo para el click del boton del envio de Nick.
	 * Ejecutará una conexión, y si sale bien enviará el paquete al servidor
	 */
	@Override
	public void enviarNick(String nickEnvio) {
		if(this.txtNick.getText().length() == 0) {
			JOptionPane.showMessageDialog(this,"El nick está vacio! Escibe algo!");
			return;
		}
		if(this.conectar()) {
			System.out.println("Cliente conectado OK al servidor! Enviando paquete login...");
			
			// Enviamos el paquete
			this.enviarMensaje(new PaqueteLogin(nickEnvio, false));			
			System.out.println("Paquete login enviado desde el cliente. Esperando respuesta...");
			
			// Esperamos respuesta del servidor
			PaqueteRespuesta respuesta = this.recibirMensajeTCP(socket);
			
			if(respuesta == null ) {
				System.out.println("Respuesta nula. ERROR");
				return;
			}
			
			System.out.println("Hemos recibido respuesta: " + respuesta.toString());
			if(respuesta.isAceptado()) {
				this.nick = this.txtNick.getText().toString().trim();
				this.conectado = true;
				this.gestionBotonesConexion();
				this.unirseSala(nickEnvio);
				
				// Cogemos el paquete de la sala, e iniciamos escucha
				this.paqueteSalaActual = respuesta.getPaqueteSala();
				this.escuchaMulticast = new EscuchaCliente(this.paqueteSalaActual, this.modeloMensajes);
			}else {
				JOptionPane.showMessageDialog(this,respuesta.getMensaje());
			}
		}
		else {
			JOptionPane.showMessageDialog(this,"No se pudo conectar al servidor!");
		}		
	}

	public void salir() {
		System.out.println("Saliendo de la ventana cliente");
		
		this.desconectar();
		this.dispose();
	}

	/**
	 * Enviaremos peticion de conexion al servidor
	 */
	public boolean conectar() {
		boolean resultado = false;
		
		try {
			this.socket = new Socket(HOST, PUERTO_TCP);
			resultado = true;
		} 
		catch (UnknownHostException e) {
			System.out.println("No se pudo conectar el cliente por excepcion de host desconocido: "
					+ e.getMessage());
		} 
		catch (Exception e) {
			System.out.println("No se pudo conectar el cliente: "
					+ e.getMessage());
		}
		
		return resultado;
	}

	public void desconectar() {
		// Enviamos mensaje al servidor si estamos conectados, si no, salimos directamente
		if(this.conectado == false) return;
		
		// Si llegamos aquí, es que estamos conectados, asi que cambiaremos eso...
		// empiezo limpiando los areas
		this.modeloMensajes.clear();
		this.modeloSalas.clear();
		
		System.out.println("Enviando mensaje de desconexion al servidor");
		try {
			this.socket = new Socket(this.HOST, this.PUERTO_TCP);
			
		}catch (Exception e) {
			System.out.println("No se pudo crear una nueva conexion al servidor");
		}

		PaqueteLogin paqueteDesconexion = new PaqueteLogin(this.nick, true);
		this.enviarMensaje(paqueteDesconexion);
		System.out.println("Enviado mensaje de desconexion");
			
		// Cerramos la escucha si la tenemos
		if(this.escuchaMulticast!= null) {
			this.escuchaMulticast.cerrarHilo();
			this.escuchaMulticast = null;
		}
		// Comprobamos si hemos creado salas
		this.cerrarSalasPropias();
		
		// Cerramos socket
		try {
			this.socket.close();
		} catch (Exception e) {
			System.out.println("Error intentando cerrar el socket desde el cliente");
		}
		
		// Por ultimo gestionamos el nick, el booleano de conexión y los componentes
		this.nick = "";
		this.conectado = false;
		this.gestionBotonesConexion();
	}

	
	private void cerrarSalasPropias() {
		System.out.println("SIN IMPLEMENTAR CERRAR SALAS");
	}
	
	/***********************************************************************************************************
	 * 
	 * Implementaciones de la Interfaz de Conexion
	 * 
	 * *********************************************************************************************************
	 */
	
	/**
	 * Método de la interfaz de conexion para enviar mensajes (por TCP).
	 * En éste caso, enviaremos paquetes de tipo PaqueteLogin
	 */
	@Override
	public boolean enviarMensaje(PaqueteLogin paquete) {
		boolean resultado = false;
		try {
			this.objSalida = new ObjectOutputStream(this.socket.getOutputStream());
			this.objSalida.writeObject(paquete);
			System.out.println("objeto enviado por el cliente al servidor");
			resultado = true;
			
		} catch (IOException e) {
			System.out.println("Error del cliente al enviar mensaje con paqueteLogin: " + e.getMessage());
		}
		
		return resultado;
	}

	/**
	 * Metodo para crear una sala
	 */
	@Override
	public void crearSala() {
		System.out.println("SIN IMPLEMENTAR");
	}

	@Override
	public void unirseSala(String valor) {
		System.out.println("SIN IMPLEMENTAR");		
	}

	@Override
	public void borrarSala(String valor) {
		System.out.println("SIN IMPLEMENTAR");		
	}

	/**
	 * Sobreescritura de la funcion de click de desconexion de la interfaz grafica
	 */
	@Override
	public void clickDesconectar() {
		this.desconectar();
	}
	
	/********************************************************************************************************
	 *  
	 *  Implementaciones de la Interfaz de Conexion
	 *  
	 *  *****************************************************************************************************
	 */
	/**
	 * Metodo para recibir mensajes por TCP en el formato de PaqueteRespuesta
	 */
	@Override
	public PaqueteRespuesta recibirMensajeTCP(Socket socket) {
		PaqueteRespuesta respuesta = null;
		
		try {
			this.objEntrada = new ObjectInputStream(this.socket.getInputStream());
			respuesta = (PaqueteRespuesta) this.objEntrada.readObject();
		} catch (Exception e) {
			System.out.println("Error recibiendo paquete respuesta: " + e.getMessage());
		}
		return respuesta;
	}


	/**
	 * Funcion para el click del envio de mensajes multicast una vez conectado
	 */
	@Override
	public void enviarMensaje(String cadena) {

		try {
			// Preparamos el multicast
			int puerto = this.paqueteSalaActual.getPuerto();
			MulticastSocket socket = new MulticastSocket(puerto);
			InetAddress grupo = InetAddress.getByName(this.paqueteSalaActual.getGrupo());
			
			// Nos unimos al grupo
			socket.joinGroup(grupo);
						
			// Preparamos el flujo de bytes para el envio
			PaqueteChat paquete = new PaqueteChat();
			paquete.setNombreUsuario(this.nick);
			String cadenaFormateada = FuncionesConversion.cadenaHTML(this.nick, cadena);
			System.out.println("Envio al server: " + cadenaFormateada);
			paquete.setMensaje(cadenaFormateada);
			byte[] paqueteBytes = FuncionesConversion.convertirPaquete(paquete);
								
			// Enviamos el datagrama con el paqute
			DatagramPacket datagrama = new DatagramPacket(paqueteBytes, paqueteBytes.length ,grupo, puerto);
			socket.send(datagrama);
						
			// Abandonamos el grupo y cerramos socket
			socket.leaveGroup(grupo);
			socket.close();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * Funcion para gestionar los botones segun la conexion
	 */
	public void gestionBotonesConexion() {
		this.btnEnviarNick.setEnabled(!conectado);
		this.txtNick.setEnabled(!conectado);
		this.btnEnviarMensaje.setEnabled(conectado);
		this.txtMensaje.setEnabled(conectado);
		this.btnCrearSala.setEnabled(conectado);
		this.btnUnirseSala.setEnabled(conectado);
		this.btnBorrarSala.setEnabled(conectado);
	}
	

	
	/****************************************************************************************************
	 * Clase para escuchar los mensajes Multicast
	 * @author Jorge
	 ****************************************************************************************************
	 */
	public class EscuchaCliente extends HiloEscucha {

		PaqueteSala paquete;
		InetAddress grupoBase;
		MulticastSocket socketMC;
		InetSocketAddress grupoReceptor;
		NetworkInterface netInterface;
		DefaultListModel<String> listado;
		
		public EscuchaCliente(PaqueteSala p, DefaultListModel<String> listado) {
			this.paquete = p;
			this.listado = listado;
			
			try {
				grupoBase = InetAddress.getByName(p.getGrupo());
				socketMC = new MulticastSocket(p.getPuerto());
				grupoReceptor = new InetSocketAddress(this.grupoBase, p.getPuerto());
				netInterface = NetworkInterface.getByName(p.getIpRemota());
				
				// Ahora ya podemos unirnos al grupo
				socketMC.joinGroup(grupoReceptor, netInterface);
				
				this.start();
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void escucha() {
			System.out.println("Hilo cliente a la escucha multicast...");
			// Preparamos el tamaño del flujo de bytes
			byte[] buffer = new byte[this.paquete.getTamMaxBuffer()];
			
			// Preparamos el datagrama
			DatagramPacket datagrama = new DatagramPacket(buffer, buffer.length);
			
			// Esperamos por paquetes en el socket multicast
			try {
				socketMC.receive(datagrama);
				String mensaje = FuncionesConversion.extraerPaquete(buffer).getMensaje();
				this.listado.addElement(mensaje);
				System.out.println("--> " + mensaje);	
			} 
			catch (IOException e) {
				System.out.println("Error o cierre en la escucha del cliente: " + e.getMessage());
			}			
		}
		
		/**
		 * Funcion sobreescrita del hilo de escucha para cerrar el hilo mejor 
		 */
		@Override
		public void cerrarHilo() {
			this.salirHilo = true;
			
			// Dejamos el grupo
			try {
				socketMC.leaveGroup(grupoReceptor, netInterface);
					
				// Salimos del multicast
				socketMC.close();
			} 
			catch (IOException e) {
				System.out.println("Error al cerrar el hilo de escucha multicast: " + e.getMessage());
			}
			
			System.out.println("Saliendo del hilo de escucha del cliente");
		}
	}
	
	/**
	 * Implementación del metodo de cerrar ventana
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		System.out.println("Cerrando ventana...");
		this.desconectar();
	}
}
