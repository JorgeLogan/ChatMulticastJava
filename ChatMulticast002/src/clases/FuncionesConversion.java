package clases;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import paquetes.PaqueteChat;

public class FuncionesConversion {

	public static byte[] convertirPaquete(PaqueteChat paquete) {
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
			System.out.println("No se pudo convertir el paquete a bytes[]: " + e.getMessage());
		}

		// devolvemos el buffer leido o null si no pudo
		return buffer;
	}
	
	// Para convertir los bytes leidos en el paquete
	public static PaqueteChat extraerPaquete(byte[] arrayBytes) {
		
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
			System.out.println("Error al intentar extraer el paquete de datos: " + e.getMessage());
		}
		
		// Grabamos el paquete o null si fallo algo		
		return paquete;
	}

	// Para transformar un código plano en u código formateado tipo HTML con negrita, etc...
	public static String cadenaHTML(String nombre, String mensaje) {
		return "<html><body> <font color='red'>  <b>" + nombre + " dice:</b> </font> <br> &nbsp &nbsp &nbsp &nbsp <i> " + mensaje + "</i></body></html>";
	}
}
