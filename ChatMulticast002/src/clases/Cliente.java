package clases;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import interfaces.InterfazConexion;
import paquetes.PaqueteLogin;
import paquetes.PaqueteRespuesta;
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
			
			System.out.println("Hemos recibido respuesta: " + respuesta.toString());
			// Comprobamos si estamos o no aceptados
			if(respuesta.isAceptado()) {
				this.unirseSala(nickEnvio);
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
		// Enviamos mensaje al servidor
		try {
			this.objSalida.writeObject(new PaqueteLogin(this.txtNick.getText().toString(), true));
		} catch (IOException e1) {
			System.out.println("Error en el cliente al intentar enviar un objeto de desconexion al server:"
					+ e1.getMessage());
		}
		
		// Comprobamos si hemos creado salas
		
		// Cerramos nuestro socket
		try {
			this.socket.close();
		} catch (Exception e) {
			System.out.println("Error intentando cerrar el socket desde el cliente");
		}
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
			//this.objSalida.close();
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
}
