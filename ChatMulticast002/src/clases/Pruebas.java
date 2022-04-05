package clases;
import paquetes.PaqueteChat;

public class Pruebas extends Thread{
	
	Sala sala = null;
	
	public Pruebas(){
		
		try {
			sala = new Sala("Pruebas","localhost","225.13.14.15", "Sala de pruebas", 6671, 1000);
			this.start();
		} catch (Exception e) {
			System.out.println("No se pudo crear la sala");
		}
	}
	public static void main(String[] args) {
		new Pruebas();
	}
	
	@Override
	public void run() {
		for(int i=0; i<10; i++) {
			PaqueteChat paquete = sala.escucharMensaje();
			if(paquete!= null)
				System.out.println("Mensaje de: " + paquete.getNombreUsuario() + "\n\t" + paquete.getMensaje());
			else{
				System.out.println("Paquete no valido");
			}
		}
		if(sala != null) sala.cerrarSala();
		System.out.println("Saliendo de la sala");		
	}
}
