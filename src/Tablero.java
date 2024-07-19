import java.util.*;

public class Tablero extends Configuracion {
	private static final Random random = new Random();

	private Celda[][] tablero;
	private final int numeroDeVacas;
	private final int numeroDePlantas;
	private int turno;
	private int nacimientosVacas;
	private int muertesVacas;
	private List<String> eventos;
	private final RegistroCSV registroCSV;
	private int generacion = 1;

	public Tablero (int numeroDeVacas, int numeroDePlantas, String nombreArchivoCSV) {
		this.numeroDeVacas = numeroDeVacas;
		this.numeroDePlantas = numeroDePlantas;
		this.registroCSV = new RegistroCSV(nombreArchivoCSV);
		tablero = new Celda[NUMERO_DE_FILAS][NUMERO_DE_COLUMNAS];
		inicializar();
	}

	public void inicializar () {
		int vacasRestantes = numeroDeVacas;
		int plantasRestantes = numeroDePlantas;

		for (int fila = 0; fila < NUMERO_DE_FILAS; fila++) {
			for (int columna = 0; columna < NUMERO_DE_COLUMNAS; columna++) {
				tablero[fila][columna] = new Celda();
			}
		}

		while (vacasRestantes > 0 || plantasRestantes > 0) {
			int fila = random.nextInt(NUMERO_DE_FILAS);
			int columna = random.nextInt(NUMERO_DE_COLUMNAS);

			if(tablero[fila][columna] instanceof Celda) {
				if(vacasRestantes > 0) {
					tablero[fila][columna] = new Vaca();
					vacasRestantes--;
				} else if(plantasRestantes > 0) {
					tablero[fila][columna] = new Planta();
					plantasRestantes--;
				}
			}
		}
	}

	public void ejecutarTurno () {
		turno++;
		nacimientosVacas = 0;
		muertesVacas = 0;
		eventos = new ArrayList<>();

		Celda[][] nuevasCeldas = new Celda[NUMERO_DE_FILAS][NUMERO_DE_COLUMNAS];

		for (int fila = 0; fila < NUMERO_DE_FILAS; fila++) {
			System.arraycopy(tablero[fila], 0, nuevasCeldas[fila], 0, NUMERO_DE_COLUMNAS);
		}

		for (int fila = 0; fila < NUMERO_DE_FILAS; fila++) {
			for (int columna = 0; columna < NUMERO_DE_COLUMNAS; columna++) {
				if(tablero[fila][columna] instanceof Vaca vaca) {
					vaca.decrementarEnergia();
					vaca.incrementarEdad();

					if(!vaca.estaViva()) {
						nuevasCeldas[fila][columna] = new Celda();
						muertesVacas++;
						eventos.add("Muere vaca en [" + fila + "," + columna + "]");
					} else {
						moverVaca(fila, columna, nuevasCeldas);
					}
				} else if(tablero[fila][columna] instanceof Planta planta) {
					planta.incrementarEnergia();
					nuevasCeldas[fila][columna] = planta;
				}
			}
		}

		tablero = nuevasCeldas;
		int numeroDeVacas = contarVacas();
		int numeroDePlantas = contarPlantas();
		registroCSV.registrarTurno(turno, numeroDeVacas, numeroDePlantas, nacimientosVacas, muertesVacas, eventos);
	}

	private void moverVaca (int fila, int columna, Celda[][] nuevoTablero) {
		List<int[]> direcciones = Arrays.asList(
				new int[]{-1, -1}, new int[]{-1, 0}, new int[]{-1, 1},
				new int[]{0, -1}, new int[]{0, 1},
				new int[]{1, -1}, new int[]{1, 0}, new int[]{1, 1}
		);
		Collections.shuffle(direcciones);
		boolean seMovio = false;
		Vaca vaca = (Vaca) tablero[fila][columna];

		for (int[] direccion : direcciones) {
			int desplazamientoFila = direccion[0];
			int desplazamientoColumna = direccion[1];

			int nuevaFila = fila + desplazamientoFila;
			int nuevaColumna = columna + desplazamientoColumna;

			if(nuevaFila >= 0 && nuevaFila < NUMERO_DE_FILAS && nuevaColumna >= 0 && nuevaColumna < NUMERO_DE_COLUMNAS) {
				if(tablero[nuevaFila][nuevaColumna] instanceof Planta) {
					vaca.comerPlanta((Planta) tablero[nuevaFila][nuevaColumna]);
					nuevoTablero[nuevaFila][nuevaColumna] = vaca;
					nuevoTablero[fila][columna] = new Celda();
					eventos.add("Muere planta en [" + nuevaFila + "," + nuevaColumna + "]");
					seMovio = true;
					break;
				} else if(tablero[nuevaFila][nuevaColumna] instanceof Vaca otraVaca) {
					if(vaca.esAdulta() && otraVaca.esAdulta()) {
						reproducirVacas(fila, columna, nuevoTablero);
					}
				} else if(tablero[nuevaFila][nuevaColumna] instanceof Celda) {
					nuevoTablero[nuevaFila][nuevaColumna] = vaca;
					nuevoTablero[fila][columna] = new Celda();
					seMovio = true;
					break;
				}
			}
		}

		if(!seMovio) {
			nuevoTablero[fila][columna] = vaca;
		}
	}

	private void reproducirVacas (int fila, int columna, Celda[][] nuevoTablero) {
		List<int[]> direcciones = Arrays.asList(
				new int[]{-1, -1}, new int[]{-1, 0}, new int[]{-1, 1},
				new int[]{0, -1}, new int[]{0, 1},
				new int[]{1, -1}, new int[]{1, 0}, new int[]{1, 1}
		);
		Collections.shuffle(direcciones);

		for (int[] direccion : direcciones) {
			int desplazamientoFila = direccion[0];
			int desplazamientoColumna = direccion[1];

			int nuevaFila = fila + desplazamientoFila;
			int nuevaColumna = columna + desplazamientoColumna;

			if(nuevaFila >= 0 && nuevaFila < NUMERO_DE_FILAS && nuevaColumna >= 0 && nuevaColumna < NUMERO_DE_COLUMNAS) {
				if(nuevoTablero[nuevaFila][nuevaColumna] instanceof Celda) {
					nuevoTablero[nuevaFila][nuevaColumna] = new Vaca(0);
					nacimientosVacas++;
					eventos.add("Nace vaca en [" + nuevaFila + "," + nuevaColumna + "]");
					return;
				}
			}
		}
	}

	private int contarVacas () {
		int numeroDeVacas = 0;
		for (int fila = 0; fila < NUMERO_DE_FILAS; fila++) {
			for (int columna = 0; columna < NUMERO_DE_COLUMNAS; columna++) {
				if(tablero[fila][columna] instanceof Vaca) {
					numeroDeVacas++;
				}
			}
		}
		return numeroDeVacas;
	}

	private int contarPlantas () {
		int numeroDePlantas = 0;
		for (int fila = 0; fila < NUMERO_DE_FILAS; fila++) {
			for (int columna = 0; columna < NUMERO_DE_COLUMNAS; columna++) {
				if(tablero[fila][columna] instanceof Planta) {
					numeroDePlantas++;
				}
			}
		}
		return numeroDePlantas;
	}

	public void imprimirCuadrilla () {

		System.out.println("-------------------------------------------\n" +
				"Generacion Numero: " + generacion + "\n-------------------------------------------");
		for (int fila = 0; fila < NUMERO_DE_FILAS; fila++) {
			for (int columna = 0; columna < NUMERO_DE_COLUMNAS; columna++) {
				if(tablero[fila][columna] instanceof Vaca) {
					System.out.print("\uD83D\uDC04 ");
				} else if(tablero[fila][columna] instanceof Planta) {
					System.out.print("\uD83C\uDF3E ");
				} else {
					System.out.print(". ");
				}
			}
			System.out.println();
		}
		System.out.println();
		generacion++;
	}

	public void cerrarRegistroCSV () {
		registroCSV.cerrarArchivo();
	}
}
