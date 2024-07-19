public class Planta extends Celda {
	private int energia;

	public Planta () {
		this.energia = ENERGIA_INICIAL_PLANTA;  // Las plantas empiezan con energía 0.
	}

	public void incrementarEnergia () {
		if(this.energia < ENERGIA_MAXIMA_PLANTA) {
			this.energia++;
		}
	}

	public int getEnergia () {
		return this.energia;
	}

	public boolean estaEnEnergiaMaxima () {
		return this.energia == ENERGIA_MAXIMA_PLANTA;
	}
}