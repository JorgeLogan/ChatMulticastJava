package clases;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import hilos_escucha.HiloEscucha;
import interfaces.InterfazConexion;
import paquetes.PaqueteLogin;
import paquetes.PaqueteRespuesta;
import paquetes.PaqueteSala;
import vistas.VentanaCliente;

public class Cliente extends VentanaCliente implements InterfazConexion<PaqueteLogin, PaqueteRespuesta>{

	/**
	 * Sugerido por el IDE
	 */
	private static final long serialVersionUID = 1L;

	// Atributos
	Socket socket = null;
	ObjectOutputStream objSalida = null;
	ObjectInputStream objEntrada = null;
	
	EscuchaCliente escuchaMulticast = null;
	
	public Cliente() {
		
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
				this.unirseSala(nickEnvio);
				
				this.escuchaMulticast = new EscuchaCliente(respuesta.getPaqueteSala(), this.modeloMensajes);
			}
		}
		else {
			JOptionPane.showMessageDialog(this,"No se pudo conectar al servidor!");
		}		
	}

	public void salir() {
		System.out.println("Saliendo de la ventana cliente");
		this.desconectar();
		//this.dispose();
		
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
		// Enviamos mensaje al servidor
		System.out.println("Estado socket " + this.socket.isConnected());
		PaqueteLogin paqueteDesconexion = new PaqueteLogin(this.txtNick.getText().toString(), true);
		this.enviarMensaje(paqueteDesconexion);
		System.out.println("Enviado mensaje de desconexion");
			
		// Cerramos la escucha si la tenemos
		if(this.escuchaMulticast!= null) {
			this.escuchaMulticast.cerrarHilo();
		}
		// Comprobamos si hemos creado salas
		this.cerrarSalasPropias();
		
		// Cerramos nuestro socket
		/*
		try {
			this.socket.close();
		} catch (Exception e) {
			System.out.println("Error intentando cerrar el socket desde el cliente");
		}
		*/
	}

	
	private void cerrarSalasPropias() {
		System.out.println("SIN IMPLEMENTAR CERRAR SALAS");
	}
	/**
	 * Método de la interfaz de conexion para enviar mensajes.
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

	@Override
	public void crearSala() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unirseSala(String valor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void borrarSala(String valor) {
		// TODO Auto-generated method stub
		
	}

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


	@Override
	public void enviarMensaje(String nickEnvio) {
		System.out.println("notd");
	}
	
	/**
	 * Clase para escuchar los mensajes Multicast
	 * @author Jorge
	 *
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
}
