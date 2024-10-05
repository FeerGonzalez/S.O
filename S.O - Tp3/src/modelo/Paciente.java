package modelo;

import java.util.Random;

public class Paciente implements Runnable {
    private static final Random random = new Random();
    private int id;
    private boolean esVip;
    private CentroMedico centroMedico;

    public Paciente(int id, boolean esVip, CentroMedico centroMedico) {
        this.id = id;
        this.esVip = esVip;
        this.centroMedico = centroMedico;
    }

    @Override
    public void run() {
        try {
            centroMedico.entrarCentroMedico(id, esVip);
            centroMedico.esperarMedico(id, esVip);
            centroMedico.pagarConsulta(id);
            centroMedico.salirCentroMedico(id);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
