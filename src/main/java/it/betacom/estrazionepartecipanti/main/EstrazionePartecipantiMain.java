package it.betacom.estrazionepartecipanti.main;

import it.betacom.estrazionepartecipanti.model.EstrazionePartecipantiModello;

public class EstrazionePartecipantiMain {

	public static void main(String[] args) {
		
		EstrazionePartecipantiModello.inizializzare();
		EstrazionePartecipantiModello.estrazione(6);
		EstrazionePartecipantiModello.stampaPDFConEstrazioni();
		EstrazionePartecipantiModello.riinizializza();
		
	}

}
