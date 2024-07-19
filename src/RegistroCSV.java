import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RegistroCSV {
	private FileWriter fileWriter;

	public RegistroCSV (String nombreArchivo) {
		try {
			fileWriter = new FileWriter(nombreArchivo);
			fileWriter.append("Turno; Numero de Vacas; Numero de Plantas; Nacimientos de Vacas; Muertes de Vacas; Eventos\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void registrarTurno (int turno, int numeroDeVacas, int numeroDePlantas, int nacimientosVacas, int muertesVacas, List<String> eventos) {
		try {
			fileWriter.append(turno + "; "
					+ numeroDeVacas + "; "
					+ numeroDePlantas + "; "
					+ nacimientosVacas + "; "
					+ muertesVacas + "; "
					+ String.join(" / ", eventos) + "\n");
			fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void cerrarArchivo () {
		try {
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
