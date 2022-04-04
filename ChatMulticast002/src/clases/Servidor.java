package clases;

import hilos_escucha.EscuchaSalaModeler;
import interfaces.InterfazConexion;
import paquetes.PaqueteSala;
import vistas.VentanaServidor;

public class Servidor extends VentanaServidor {
	/**
	 * Solicitado por el IDE
	 */
	private static final long serialVersionUID = 1L;
	
	// Atributos
	private Sala agora = null;
	private EscuchaSalaModeler escuchaAgora = null;	// Para la escucha de mensajes de la sala Agora
	private Login login= null;	// Para el control del login de los clientes
	private final String direccionGrupo = "225.2.3.4";
	
	// Constructor de la clase
	public Servidor() {
		this.gestionControles(false);
	}
	
	// para la funcion ejecutable de la clase
	public static void main(String[] args) {
		new Servidor();
	}

	@Override
	public void clickConectar() {
		/* Conectar lleva varios pasos:
			Crear una sala Multicast llamada Agora
			Ponerse a escucha de clientes por TCP --> Hilo 1
			Si éstos son aceptados, pasarles un paquete con la información para la sala
			Ademas, necesitamos estar a la escucha de nuestra propia sala --> Hilo 2
		*/
		System.out.println("Pulsado conectar");
		// Creamos la sala
		this.agora = new Sala(InterfazConexion.HOST, this.direccionGrupo, "Sala Agora", 5678, 1000);
	
		// Escuchamos la sala
		this.escuchaAgora = new EscuchaSalaModeler(this.agora, this.modeloMensajes);
		this.escuchaAgora.start();
		
		// Ahora nos preparamos para la escucha de clientes
		// Preparamos un paquete para la sala de Agora
		PaqueteSala pSala = this.agora.getPaqueteSala();
		System.out.println("Paquete de sala agora: " + pSala.toString());
		this.login = new Login(pSala, InterfazConexion.PUERTO_TCP, this.modeloUsuarios);

		// Ponemos los controles en modo conectado
		this.gestionControles(true);
	}

	@Override
	public void clickDesconectar() {
		System.out.println("Pulsado desconectar");
		if(agora!= null) {
			System.out.println("Cerrando sala Agora...");
			agora.cerrarSala();
		}
		
		if(escuchaAgora!= null) {
			System.out.println("Cerrando hilo de escucha de la sala Agora en el Servidor...");
			escuchaAgora.cerrarHilo();
		}
		
		if(login!= null) {
			System.out.println("Cerrando hilo login....");
			login.cerrarHilo();
		}
		
		// Ponemos los controles en modo desconectaco
		this.gestionControles(false);
	}

	/**
	 * Funcion para el click del boton de Salir
	 */
	@Override
	public void clickSalir() {
		System.out.println("Pulsado salir... desconectando...");
		this.clickDesconectar();
		this.dispose();
	}

	private void gestionControles(boolean conectado) {
		this.btnConectar.setEnabled(!conectado);
		this.btnDesconectar.setEnabled(conectado);
	}
	
}
