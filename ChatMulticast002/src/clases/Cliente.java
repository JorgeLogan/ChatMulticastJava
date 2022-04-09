package clases;

import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	private Socket socket = null;
	private ObjectOutputStream objSalida = null;
	private ObjectInputStream objEntrada = null;
	private boolean conectado = false; // Para que al salir, si esta conectado, libere su nick del server
	
	// Para la escucha de UDP cree una clase al final de esta, que hereda de HiloEscucha
	private EscuchaCliente escuchaMulticast = null;
	
	// Para el paquete de la sala en la que estamos chateando
	PaqueteSala paqueteSalaActual;
		
	// Y creo un diccionario de las salas disponibles
	//private Map<String, PaqueteSala> salasDisponibles = new HashMap<String, PaqueteSala>();
	private List<PaqueteSala> salasDisponibles = new LinkedList<PaqueteSala>();
	/*****************************************************************************************
	 * Constructor de la clase
	 * 
	 * ***************************************************************************************
	 */
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
			
			// Gestionamos las salas disponibles
			this.gestionSalasDisponibles();
						
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
				
				// Cogemos los paquetes de la sala, e iniciamos escucha
				this.leerSalas(respuesta.getPaquetesSala());
				
				// Configuramos los controles a modo conextado
				this.gestionBotonesConexion();
				
				// Nos unimos a la sala
				this.unirseSala(nickEnvio);
				
				
				
				// Preparamos la escucha
				this.escuchaMulticast = new EscuchaCliente(this.paqueteSalaActual, this.modeloMensajes, this.modeloSalas, this.salasDisponibles);
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
	 * Para el login, cogemos del paquete de respuesta el listado de paquetes de sala
	 * y lo pasamos a nuestro diccionario, y al jlist de salas en formato string
	 * @param lista
	 */
	private void leerSalas(List<PaqueteSala> lista) {
		for(PaqueteSala paquete : lista){
			System.out.println("===> Leo de la lista del servidor la sala " + paquete.toString());
			this.salasDisponibles.add(paquete);
			this.modeloSalas.addElement(paquete.getNombre());
		}
		this.paqueteSalaActual = this.salasDisponibles.get(0);
		System.out.println("Paquete actual del cliente --> " + this.paqueteSalaActual.toString());
	}
	
	/**
	 * Para crear y configurar la gestion de salas
	 */
	private void gestionSalasDisponibles() {
		System.out.println("SIN IMPLEMENTAR");
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
		// empiezo limpiando los areas y el listado de salas
		this.modeloMensajes.clear();
		this.modeloSalas.clear();
		this.salasDisponibles.clear();
		
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
		System.out.println("----------------------------------------------------------------------------------------");
		PaqueteSala paqueteNuevo = new PaqueteSala();
		paqueteNuevo.setCreador(nick);
		
		paqueteNuevo.setNombre(JOptionPane.showInputDialog("Escribe el nombre de la nueva sala"));
		if(paqueteNuevo.getNombre() == null) return;
		
		paqueteNuevo.setGrupo(JOptionPane.showInputDialog("Escribe la direccion tipo D donde estará el grupo"));
		if(paqueteNuevo.getGrupo() == null) return;
		
		paqueteNuevo.setIpRemota(JOptionPane.showInputDialog("Escribe la direccion IP del equipo servidor"));
		if(paqueteNuevo.getIpRemota() == null) return;
		
		try {
			paqueteNuevo.setPuerto(Integer.parseInt(JOptionPane.showInputDialog("Escribe el puerto de escucha")));
			if(paqueteNuevo.getPuerto() == 0) return;
			
			paqueteNuevo.setTamMaxBuffer(Integer.parseInt(JOptionPane.showInputDialog("Escribe el tamaño máximo del paquete")));
			if(paqueteNuevo.getTamMaxBuffer() == 0) return;

			// Si llegamos aqui, todos los valores son "validos" (no hago comprobaciones, se toma todo por válido)
			// lo unico que voy a comprobar es que no exista esa sala en el listado
			if(existeSala(paqueteNuevo.getNombre())) {
				JOptionPane.showMessageDialog(this, "Lo siento, ya existe una sala con ese nombre. Salimos");
			}
			else {
				// Agregamos el paquete a nuestro listado de salas
				this.salasDisponibles.add(paqueteNuevo);
				
				// La pasamos a nuestro listado visual de salas
				this.modeloMensajes.addElement(paqueteNuevo.getNombre());
				
				// Creamos un paqueteChat para enviar a los clientes actuales
				PaqueteChat pc = new PaqueteChat(this.nick, paqueteNuevo);
				this.enviarMensaje(pc);
				System.out.println(" ---------------------> Enviada sala a los clientes");
				
				// Creamos un paqueteLogin para avisar al servidor para los clientes nuevos que llegen despues
				PaqueteLogin pl = new PaqueteLogin(paqueteNuevo);
				this.enviarMensaje(pl);
				System.out.println(" ---------------------> Enviada sala al servidor");
			}
		}
		catch(Exception e) {
			System.out.println("Valores nulos no validos, asi que se sale de la función sin crear sala: " + e.getMessage());
			return;
		}	
	}

	
	public boolean existeSala(String nombreSala) {
		return this.salasDisponibles.contains(nombreSala);
	}
	
	
	
	@Override
	public void unirseSala(String valor) {
		System.out.println("SIN IMPLEMENTAR");		
	}

	@Override
	public void borrarSala(String valor) {
		System.out.println("SIN IMPLEMENTAR");		
	}

	/*************************************************************************************************
	 * Sobreescritura de la funcion de click de desconexion de la interfaz grafica
	 * 
	 * ***********************************************************************************************
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
	/*******************************************************************************************
	 * Metodo para recibir mensajes por TCP en el formato de PaqueteRespuesta
	 * 
	 * *****************************************************************************************
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


	/********************************************************************************************
	 * Funcion para el click del envio de mensajes multicast una vez conectado
	 * 
	 * ******************************************************************************************
	 */
	@Override
	public void enviarMensaje(String cadena) {
		
		// Limpiamos el area de texto de envio de mensajes
		this.txtMensaje.setText("");
		
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
								
			// Enviamos el datagrama con el paquete
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
	 * Funcion para enviar un paqueteChat a los clientes por multicast
	 * @param paquete
	 */
	public void enviarMensaje(PaqueteChat paquete) {
		try {
			// Preparo los elementos de emision del multicast
			InetAddress grupo = InetAddress.getByName(this.paqueteSalaActual.getGrupo());
			MulticastSocket socketMc = new MulticastSocket(this.paqueteSalaActual.getPuerto());
			
			// Uno el socketMc al grupo
			socketMc.joinGroup(grupo);
			
			// Preparo el buffer de bytes
			byte[] buffer = new byte[this.paqueteSalaActual.getTamMaxBuffer()];
			buffer = FuncionesConversion.convertirPaquete(paquete);
			
			// Ya puedo preparar el datagrama
			DatagramPacket datagrama = new DatagramPacket(buffer, buffer.length, 
					grupo, this.paqueteSalaActual.getPuerto());
			socketMc.send(datagrama);
			
			// Y ahora cierro los elementos
			socketMc.leaveGroup(grupo);
			socketMc.close();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/***************************************************************************************************
	 * Funcion para gestionar los botones segun la conexion
	 * *************************************************************************************************
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
		DefaultListModel<String> listadoMensajes;
		DefaultListModel<String> listadoSalas;
		List<PaqueteSala> listadoPaqueteSalas;
		
		public EscuchaCliente(PaqueteSala p, DefaultListModel<String> listadoM, DefaultListModel<String>listadoS, 
				List<PaqueteSala> listaPaquetes) {
			this.paquete = p;
			this.listadoMensajes = listadoM;
			this.listadoSalas = listadoS;
			this.listadoPaqueteSalas = listaPaquetes;
			
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
				String emisor = FuncionesConversion.extraerPaquete(buffer).getNombreUsuario();
				System.out.println("Mi nick: " + nick + " emisor: " + emisor);
				String mensaje = FuncionesConversion.extraerPaquete(buffer).getMensaje();
				
				// Pasamos el mensaje al listado, poniendole cabecera si no la tiene (busqueda de bugs, ejem)
				// Aunque puede ser que el paquete recibido, solo quiera informar de una sala nueva!! En ese caso el formato es distinto
				if(mensaje.contains("dice:") == false) { // Si no tiene el indicador de nick, agrego el nick
					this.listadoMensajes.addElement(FuncionesConversion.cadenaHTML(emisor, mensaje));	
				}else {
					this.listadoMensajes.addElement(mensaje);
					
				}
				
				// Buscamos si en el paquete recibido, tenemos salas nuevas
				// Si no tiene un texto normal, puede ser que sea una sala nueva
				System.out.println("\nBuscamos salas en el paquete recibido --> ");
				PaqueteChat pc = FuncionesConversion.extraerPaquete(buffer);
				if(pc.getNuevaSala()!= null) {
					System.out.println("\nBuscamos salas en el paquete recibido -->" + pc.getNuevaSala().toString());
					
					if(this.listadoSalas.contains(pc.getNuevaSala().getNombre() )== false) {
						this.listadoSalas.addElement(pc.getNuevaSala().getNombre());
						this.listadoPaqueteSalas.add(pc.getNuevaSala());
						System.out.println("--->  --> Sala Nueva añadida: " + pc.getNuevaSala().getNombre());
						JOptionPane.showMessageDialog(null, "Sala Nueva añadida: " + pc.getNuevaSala().getNombre());
					}
					else {
						System.out.println("No agrego la sala porque ya la tengo en el listado");
						for(int i=0; i<this.listadoMensajes.size(); i++) {
							System.out.println("Listado " + i + " ----------------------------------------> " + listadoMensajes.get(i));
						}
					}
				}
				else {
					System.out.println("\nBuscamos salas en el paquete recibido --> NO hay sala en el paquete");
				}
				
				// COmprobamos que no tengamos un listado enorme para no gastar mucha memoria
				if(this.listadoMensajes.size() > 10) this.listadoMensajes.remove(0);
				
				
				System.out.println("Recibo por MultiCast -----> " + emisor + "--> " + mensaje);
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
	
	/****************************************************************************************************
	 * Implementación del metodo de cerrar ventana
	 * 
	 * **************************************************************************************************
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		System.out.println("Cerrando ventana...");
		this.desconectar();
	}
}
