package hilos_escucha;

import java.io.IOException;
import java.io.ObjectInputStream;

public abstract class HiloEscucha extends Thread{
	
	// Atributos
	protected boolean salirHilo = false;
	private String usuario; // No es necesario, pero no esta de más
	
	// Constructor vacio
	public HiloEscucha() {}

	// Constructor con nombre de usuario
	public HiloEscucha(String usuario) {
		this.usuario = usuario;
	}
	
	// Metodo abstracto para implementar en las clases finales
	public abstract void escucha();
	
	// Metodo para cerrar el hilo (cuando le llegue al while)
	public void cerrarHilo() {
		this.salirHilo = true;
	}
	
	// Metodo de ejecución del hilo
	@Override
	public void run() {
		while(this.salirHilo == false) {
			// Por si acaso, me protego en un try/catch
			try {
				this.escucha();	
			}
			catch(Exception e) {
				// Salimos del hilo de escucha
				//this.cerrarHilo();
				System.out.println("-------> Error en el hilo de escucha Multicast--> " + e.getMessage());
			}
		}
		System.out.println("Cerrado hilo de escucha del usuario: " + this.usuario);
	}
}