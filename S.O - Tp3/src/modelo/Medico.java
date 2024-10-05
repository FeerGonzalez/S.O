package modelo;

public class Medico implements Runnable {
    private final int id;
    private final CentroMedicoVIP centroMedico;

    public Medico(int id, CentroMedicoVIP centroMedico) {
        this.id = id;
        this.centroMedico = centroMedico;
    }

    @Override
    public void run() {
        try {
            centroMedico.atenderPaciente(id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
