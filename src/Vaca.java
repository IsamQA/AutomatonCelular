public class Vaca extends Celda {
	int energia;
	int edad;

	public Vaca () {
		this.energia = ENERGIA_INICIAL_VACA;
		this.edad = 4;  // Las vacas iniciales son adultas.
	}

	public Vaca (int edad) {
		this.energia = ENERGIA_INICIAL_VACA;
		this.edad = edad;  // Las nuevas vacas empiezan con la edad proporcionada.
	}

	public void decrementarEnergia () {
		this.energia--;
	}

	public boolean estaViva () {
		return this.energia > 0;
	}

	public void incrementarEdad () {
		this.edad++;
	}

	public boolean esAdulta () {
		return this.edad >= 4;
	}

	public void comerPlanta (Planta planta) {
		this.energia += planta.getEnergia();
	}
}
