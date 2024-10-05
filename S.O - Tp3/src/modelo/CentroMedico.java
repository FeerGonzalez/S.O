package modelo;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CentroMedico {
    private static final int MAX_PACIENTES = 28;
    private Semaphore capacidadCentro = new Semaphore(MAX_PACIENTES, true);
    private Semaphore puertaEntrada = new Semaphore(1, true);
    private Semaphore puertaSalida = new Semaphore(1, true);
    private Queue<Integer>[] filasMedicos;
    private Queue<Integer> filaVIP = new LinkedList<>();
    private Queue<Integer> filaCajero = new LinkedList<>();
    private Semaphore[] semaforosMedicos;
    private Semaphore semaforoVIP = new Semaphore(0, true);
    private Semaphore semaforoCajero = new Semaphore(0, true);
    private AtomicInteger vipCounter = new AtomicInteger(0);
    private AtomicBoolean cerrado = new AtomicBoolean(false);
    private AtomicInteger totalPacientes = new AtomicInteger(0);
    private AtomicInteger[] pacientesAtendidosPorMedico;
    private final Object lockCajero = new Object();

    public CentroMedico(int numeroMedicos) {
        filasMedicos = new Queue[numeroMedicos];
        semaforosMedicos = new Semaphore[numeroMedicos];
        pacientesAtendidosPorMedico = new AtomicInteger[numeroMedicos];
        for (int i = 0; i < numeroMedicos; i++) {
            filasMedicos[i] = new LinkedList<>();
            semaforosMedicos[i] = new Semaphore(0, true);
            pacientesAtendidosPorMedico[i] = new AtomicInteger(0);
        }
    }

    public boolean isClosed() {
        return cerrado.get();
    }

    public void cerrarCentroMedico() {
        cerrado.set(true);
    }

    public void entrarCentroMedico(int id, boolean esVip) throws InterruptedException {
        capacidadCentro.acquire();
        puertaEntrada.acquire();
        System.out.println("Paciente " + id + (esVip ? " (VIP)" : "") + " entra al centro");
        puertaEntrada.release();
    }

    public void esperarMedico(int id, boolean esVip) throws InterruptedException {
        if (esVip) {
            synchronized (filaVIP) {
                filaVIP.add(id);
            }
            semaforoVIP.acquire();
        } else {
            int medico = new Random().nextInt(filasMedicos.length);
            synchronized (filasMedicos[medico]) {
                filasMedicos[medico].add(id);
            }
            semaforosMedicos[medico].acquire();
        }
    }

    public void atenderPaciente(int medicoId) throws InterruptedException {
        Integer paciente = null;
        
        if (vipCounter.incrementAndGet() == 3 || filasMedicos[medicoId].isEmpty()) {
            synchronized (filaVIP) {
                paciente = filaVIP.poll();
            }
            if (paciente != null) {
                semaforoVIP.release();
                vipCounter.set(0);
            }
        }
        
        if (paciente == null) {
            synchronized (filasMedicos[medicoId]) {
                paciente = filasMedicos[medicoId].poll();
            }
            if (paciente != null) {
                semaforosMedicos[medicoId].release();
            }
        }
        
        if (paciente != null) {
            pacientesAtendidosPorMedico[medicoId].incrementAndGet();
            totalPacientes.incrementAndGet();
            System.out.println("Médico " + medicoId + " atiende al paciente " + paciente);
        } else {
    
            System.out.println("Médico " + medicoId + " no tiene pacientes, duerme");
            Thread.sleep(500);
        }
    }

    public void pagarConsulta(int id) throws InterruptedException {
        synchronized (lockCajero) {
            filaCajero.add(id);
        }
        semaforoCajero.acquire();
    }

    public void cobrarConsulta(int cajeroId) throws InterruptedException {
        Integer paciente;
        synchronized (lockCajero) {
            paciente = filaCajero.poll();
        }
        if (paciente != null) {
            semaforoCajero.release();
            System.out.println("Cajero " + cajeroId + " cobra al paciente " + paciente);
        }
    }

    public void salirCentroMedico(int id) throws InterruptedException {
        puertaSalida.acquire();
        System.out.println("Paciente " + id + " sale del centro");
        capacidadCentro.release();
        puertaSalida.release();
    }

    public void imprimirEstadisticas() {
        System.out.println("--- Estadísticas ---");
        System.out.println("Total de pacientes atendidos: " + totalPacientes.get());
        for (int i = 0; i < pacientesAtendidosPorMedico.length; i++) {
            System.out.println("Médico " + i + " atendió a " + pacientesAtendidosPorMedico[i].get() + " pacientes.");
        }
    }
}
