package main;

import modelo.Cajero;
import modelo.CentroMedicoVIP;
import modelo.Medico;
import modelo.Paciente;

public class Main {

	public static void main(String[] args) {
		try {
			CentroMedicoVIP centroMedico = new CentroMedicoVIP();

	        // Crear hilos de médicos
	        Thread[] medicos = new Thread[4];
	        for (int i = 0; i < 4; i++) {
	            medicos[i] = new Thread(new Medico(i, centroMedico));
	            medicos[i].start();
	        }

	        // Crear hilos de cajeros
	        Thread[] cajeros = new Thread[2];
	        for (int i = 0; i < 2; i++) {
	            cajeros[i] = new Thread(new Cajero(i, centroMedico));
	            cajeros[i].start();
	        }

	        // Crear pacientes (ejemplo con 10 pacientes)
	        for (int i = 0; i < 10; i++) {
	            boolean esVIP = (i % 4 == 0); // 1 de cada 4 es VIP
	            int medicoAsignado = esVIP ? -1 : i % 4;
	            new Thread(new Paciente(i, esVIP, centroMedico, medicoAsignado)).start();
	           
				Thread.sleep(500);
				 // Simular tiempo entre la llegada de pacientes
	        }

	        // Esperar a que todos los pacientes sean atendidos (simulación)
	        Thread.sleep(20000);

	        // Imprimir estadísticas
	        centroMedico.imprimirEstadisticas();
		}catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
