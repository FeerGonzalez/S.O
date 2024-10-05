package modelo;

public class Medico implements Runnable {
    private int id;
    private CentroMedico centroMedico;

    public Medico(int id, CentroMedico centroMedico) {
        this.id = id;
        this.centroMedico = centroMedico;
    }

    @Override
    public void run() {
        try {
            while (!centroMedico.isClosed()) {
                centroMedico.atenderPaciente(id);
                Thread.sleep(1000); // Simulación de tiempo de consulta
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
