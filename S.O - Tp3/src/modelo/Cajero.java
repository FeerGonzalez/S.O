package modelo;

public class Cajero implements Runnable {
    private int id;
    private CentroMedico centroMedico;

    public Cajero(int id, CentroMedico centroMedico) {
        this.id = id;
        this.centroMedico = centroMedico;
    }

    @Override
    public void run() {
        try {
            while (!centroMedico.isClosed()) {
                centroMedico.cobrarConsulta(id);
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
