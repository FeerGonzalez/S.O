package modelo;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class CentroMedicoVIP {
	private static final int MAX_PACIENTES = 28;
    private static final int MEDICOS = 4;
    private static final int CAJEROS = 2;

    // Semáforos para controlar la entrada y salida de pacientes
    private final Semaphore capacidadCentro = new Semaphore(MAX_PACIENTES, true);
    private final Semaphore puertaEntrada = new Semaphore(1, true);
    private final Semaphore puertaSalida = new Semaphore(1, true);

    // Fila de espera para pacientes VIP
    private final Queue<Paciente> filaVIP = new LinkedList<>();
    private final Semaphore semVIP = new Semaphore(0, true); // Controla pacientes en fila VIP

    // Fila de espera para pacientes en caja
    private final Queue<Paciente> filaCaja = new LinkedList<>();
    private final Semaphore semCaja = new Semaphore(0, true); // Controla pacientes en fila caja

    // Fila de espera por médico
    private final Queue<Paciente>[] filasMedicos = new LinkedList[MEDICOS];
    private final Semaphore[] semMedicos = new Semaphore[MEDICOS]; // Controla pacientes por médico
    private final int[] atencionesPorMedico = new int[MEDICOS]; // Contador de pacientes por médico

    // Semáforo para controlar el acceso a la fila de cajeros
    private final Semaphore accesoFilaCaja = new Semaphore(1, true);
    
    private int pacientesAtendidosTotal = 0;
    private final Semaphore accesoContador = new Semaphore(1, true);

    public CentroMedicoVIP() {
        // Inicializar filas de espera y semáforos
        for (int i = 0; i < MEDICOS; i++) {
            filasMedicos[i] = new LinkedList<>();
            semMedicos[i] = new Semaphore(0, true);
        }
    }

    public void ingresarCentro(Paciente paciente) throws InterruptedException {
        // Controla la capacidad del centro
        capacidadCentro.acquire();
        puertaEntrada.acquire();
        System.out.println(paciente + " ha ingresado al centro.");
        puertaEntrada.release();

        if (paciente.esVIP()) {
            synchronized (filaVIP) {
                filaVIP.add(paciente);
                semVIP.release(); // Notificar a médicos que hay paciente VIP esperando
            }
        } else {
            int medicoAsignado = paciente.getMedicoAsignado();
            synchronized (filasMedicos[medicoAsignado]) {
                filasMedicos[medicoAsignado].add(paciente);
                semMedicos[medicoAsignado].release(); // Notificar al médico que tiene paciente
            }
        }
    }

    public void atenderPaciente(int medicoId) throws InterruptedException {
        int vipCounter = 0;

        while (true) {
            Paciente paciente = null;

            if (!filasMedicos[medicoId].isEmpty()) {
                // Atender paciente de la fila normal del médico
                synchronized (filasMedicos[medicoId]) {
                    paciente = filasMedicos[medicoId].poll();
                }
                vipCounter++;
            }

            if (vipCounter == 3 || (paciente == null && !filaVIP.isEmpty())) {
                // Atender paciente VIP
                synchronized (filaVIP) {
                    if (!filaVIP.isEmpty()) {
                        paciente = filaVIP.poll();
                        vipCounter = 0;
                    }
                }
            }

            if (paciente == null) {
                // Si no hay pacientes, dormir al médico
                semMedicos[medicoId].acquire();
                continue;
            }

            System.out.println("Médico " + medicoId + " atiende a " + paciente);
            Thread.sleep(1000); // Simula la atención médica

            // Contar atención por médico
            accesoContador.acquire();
            atencionesPorMedico[medicoId]++;
            pacientesAtendidosTotal++;
            accesoContador.release();

            // Paciente va a la fila de caja
            accederFilaCaja(paciente);
        }
    }

    public void atenderCaja(int cajeroId) throws InterruptedException {
        while (true) {
            Paciente paciente;
            synchronized (filaCaja) {
                if (filaCaja.isEmpty()) {
                    semCaja.acquire(); // Esperar si no hay pacientes en la fila
                    continue;
                }
                paciente = filaCaja.poll();
            }

            System.out.println("Cajero " + cajeroId + " cobra a " + paciente);
            Thread.sleep(1000); // Simula el cobro

            // Paciente sale del centro
            puertaSalida.acquire();
            System.out.println(paciente + " ha salido del centro.");
            puertaSalida.release();
            capacidadCentro.release(); // Liberar un espacio en el centro
        }
    }

    private void accederFilaCaja(Paciente paciente) throws InterruptedException {
        accesoFilaCaja.acquire();
        synchronized (filaCaja) {
            filaCaja.add(paciente);
            semCaja.release();
        }
        accesoFilaCaja.release();
    }

    public void imprimirEstadisticas() {
        System.out.println("Total de pacientes atendidos: " + pacientesAtendidosTotal);
        for (int i = 0; i < MEDICOS; i++) {
            System.out.println("Médico " + i + " atendió a " + atencionesPorMedico[i] + " pacientes.");
        }
    }
}
