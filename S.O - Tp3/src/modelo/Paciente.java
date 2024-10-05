package modelo;

public class Paciente implements Runnable {
    private final int id;
    private final boolean esVIP;
    private final CentroMedicoVIP centroMedico;
    private final int medicoAsignado;

    public Paciente(int id, boolean esVIP, CentroMedicoVIP centroMedico, int medicoAsignado) {
        this.id = id;
        this.esVIP = esVIP;
        this.centroMedico = centroMedico;
        this.medicoAsignado = medicoAsignado;
    }

    public boolean esVIP() {
        return esVIP;
    }

    public int getMedicoAsignado() {
        return medicoAsignado;
    }

    @Override
    public void run() {
        try {
            centroMedico.ingresarCentro(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return esVIP ? "Paciente VIP " + id : "Paciente " + id + " asignado al médico " + medicoAsignado;
    }
}
