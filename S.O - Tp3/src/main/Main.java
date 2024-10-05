package main;

import modelo.Cajero;
import modelo.CentroMedico;
import modelo.Medico;
import modelo.Paciente;

public class Main {

	public static void main(String[] args) {
		try {
			CentroMedico centroMedico = new CentroMedico(4);
	        Thread[] medicos = new Thread[4];
	        for (int i = 0; i < 4; i++) {
	            medicos[i] = new Thread(new Medico(i, centroMedico));
	            medicos[i].start();
	        }
	        
	        Thread[] cajeros = new Thread[2];
	        for (int i = 0; i < 2; i++) {
	            cajeros[i] = new Thread(new Cajero(i, centroMedico));
	            cajeros[i].start();
	        }

	        for (int i = 0; i < 50; i++) {
	            boolean esVip = i % 4 == 0;
	            Thread paciente = new Thread(new Paciente(i, esVip, centroMedico));
	            paciente.start();
	            Thread.sleep(200); 
	        }

	        // Simulacion de la señal TERM
	        Thread.sleep(20000);
	        centroMedico.cerrarCentroMedico();

	        for (Thread medico : medicos) {
	            medico.join();
	        }
	        for (Thread cajero : cajeros) {
	            cajero.join();
	        }

	        centroMedico.imprimirEstadisticas();
		}catch (Exception e) {
			
		}
			 	
		   
	}
}
