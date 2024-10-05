package modelo;

public class Cajero implements Runnable {
    private final int id;
    private final CentroMedicoVIP centroMedico;

    public Cajero(int id, CentroMedicoVIP centroMedico) {
        this.id = id;
        this.centroMedico = centroMedico;
    }

    @Override
    public void run() {
        try {
            centroMedico.atenderCaja(id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
