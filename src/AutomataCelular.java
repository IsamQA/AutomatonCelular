public class AutomataCelular extends Configuracion {
	private final Tablero tablero;

	public AutomataCelular (int numeroVacas, int numeroPlantas, String nombreCSV) {
		tablero = new Tablero(numeroVacas, numeroPlantas, nombreCSV);
	}

	public static void main (String[] args) {
		AutomataCelular automata = new AutomataCelular(NUMERO_DE_VACAS, NUMERO_DE_PLANTAS, "C:\\Users\\HP\\IdeaProjects\\AutomatonCelular\\Automaton.csv");
		automata.ejecutar();
	}

	public void ejecutar () {
		for (int iteracion = 0; iteracion < NUMERO_DE_TURNOS; iteracion++) {
			tablero.imprimirCuadrilla();
			tablero.ejecutarTurno();

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}