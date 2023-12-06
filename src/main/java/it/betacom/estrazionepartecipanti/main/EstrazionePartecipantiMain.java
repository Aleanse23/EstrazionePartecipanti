package it.betacom.estrazionepartecipanti.main;

import java.util.Scanner;

import it.betacom.estrazionepartecipanti.model.EstrazionePartecipantiModello;

public class EstrazionePartecipantiMain {

	public static void main(String[] args) {

		// main senza scanner
//		EstrazionePartecipantiModello.inizializzare();
//		EstrazionePartecipantiModello.estrazione(5);
//		EstrazionePartecipantiModello.stampaPDFConEstrazioni();
//		EstrazionePartecipantiModello.riinizializza();

		// main con scanner
		Scanner input = new Scanner(System.in);
		int numEstrazioni = 0;
		EstrazionePartecipantiModello.inizializzare();

		int secondaScelta = 0;
		System.out.println("Quante estrazioni vuoi fare?");
		numEstrazioni = input.nextInt();
		EstrazionePartecipantiModello.estrazione(numEstrazioni);
		input.nextLine();
		do {

			System.out.println("Vuoi stampare le estrazioni? rispondi Y o N");
			String primaScelta = input.nextLine();
			switch (primaScelta) {

			case "Y":
				EstrazionePartecipantiModello.stampaPDFConEstrazioni();
				break;

			case "N":
				System.out.println(
						"Vuoi fare altre estrazioni da aggiungere o stampare in PDF e riinizializzare? rispondi 1 o 2");
				secondaScelta = input.nextInt();

				switch (secondaScelta) {

				case 1:
					System.out.println("Quante estrazioni vuoi fare?");
					numEstrazioni = input.nextInt();
					EstrazionePartecipantiModello.estrazione(numEstrazioni);
					input.nextLine();
					continue;

				case 2:
					EstrazionePartecipantiModello.stampaPDFConEstrazioni();
					EstrazionePartecipantiModello.riinizializza();
					break;
				}
			}
			break;
		} while (secondaScelta == 1);

		input.close();

	}

}
