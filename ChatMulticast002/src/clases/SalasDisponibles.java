package clases;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;

import paquetes.PaqueteSala;

public class SalasDisponibles {
	/*
	 * Necesitamos un listado estatico de paquete de salas, para poder tener a todos
	 * los usuarios trabajando con la misma lista
	 */
	public static DefaultListModel<PaqueteSala> salasDisponibles;
	
	SalasDisponibles(){
		if(salasDisponibles == null) salasDisponibles = new DefaultListModel<PaqueteSala>();	
	}
	
	/**
	 * Funcion para agregar una sala.
	 * @param usuario El usuario creador de la sala
	 * @param salaNueva La sala a crear
	 * @return true si la agrega, false si no
	 */
	public boolean agregarPaqueteSala(PaqueteSala paquete) {
		if(salasDisponibles.contains(paquete)) {
			System.out.println("No se puede agregar la sala al listado porque ya existe.");
			return false;
		}else {
			salasDisponibles.addElement(paquete);
			System.out.println(paquete.getCreador() + " ha agregado al listado la sala " + paquete.getNombre()+
					". Salas disponibles:  " + this.salasDisponibles.getSize());
			this.visualizarSalasConsola();
			return true;
		}
	}
	
	/**
	 * Funcion para mostrar por consola las salas disponibles
	 */
	public void visualizarSalasConsola() {
		for(int i=0; i<salasDisponibles.getSize(); i++) {
			System.out.println("Sala num." + i + "--->" + salasDisponibles.get(i).toString());
		}
	}
	
	/**
	 * Funcion para borrar una sala; Solo el creador de la sala puede borrarla
	 * @param usuario El usuario
	 * @param salaBorrar la sala a borrar
	 * @return true si la borra, false si no
	 */
	public boolean borrarPaqueteSala(PaqueteSala paquete) {
		if(salasDisponibles.removeElement(paquete)) {
			System.out.println("Se elimina del listado de salas disponibles, la sala " + paquete.getNombre());
			return true;
		}else {
			System.out.println("Se intentó eliminar la sala " + paquete.getNombre() + ", pero no existe en el listado");
			return false;
		}
	}	
}
