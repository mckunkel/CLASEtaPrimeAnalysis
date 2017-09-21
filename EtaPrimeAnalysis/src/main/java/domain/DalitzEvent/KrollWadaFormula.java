/*  +__^_________,_________,_____,________^-.-------------------,
 *  | |||||||||   `--------'     |          |                   O
 *  `+-------------USMC----------^----------|___________________|
 *    `\_,---------,---------,--------------'
 *      / X MK X /'|       /'
 *     / X MK X /  `\    /'
 *    / X MK X /`-------'
 *   / X MK X /
 *  / X MK X /
 * (________(                @author m.c.kunkel
 *  `------'
*/
package domain.DalitzEvent;

import org.jlab.groot.data.H1F;
import org.jlab.groot.ui.TCanvas;

public class KrollWadaFormula {

	static double[] par = null;

	public KrollWadaFormula(double[] par) {
		this.par = par;
	}

	public static double Eval_Kroll_wada(double x, double[] par) {
		double structure_constant = 1. / (137.035999074);
		double pi = Math.PI;
		double Melectron = 0.000510999; // Electron Mass
		double M = par[1];
		double fpi = 0.0924; // MeV
		double f8pi = 1.3 * fpi;
		double f0pi = 1.04 * fpi;
		double theta_mix = 10.6 * pi / 180.;

		double Mp_const = structure_constant / (pi * fpi) * (1. / Math.sqrt(3.0))
				* (fpi / f8pi * Math.sin(theta_mix) + 2.0 * Math.sqrt(2.0) * fpi / f0pi * Math.cos(theta_mix));

		double Norm = par[0] / (64. * pi) * Math.pow(M, 3) * Math.pow(Mp_const, 2); // factor
		// 4
		// unknown
		double qsqr = (x * x);
		double f = 0.;

		f = (Norm) * (2.0 * structure_constant / (3. * pi) * (1. / Math.sqrt(qsqr))
				* Math.sqrt(1 - 4. * (Math.pow(Melectron, 2) / qsqr)) * (1. + 2.0 * (Math.pow(Melectron, 2) / qsqr))
				* (Math.pow((1. - qsqr / (Math.pow(M, 2))), 3)));

		return f;
	}

	public static double Pole_FFII(double x, double[] par) {

		double Lambdasqr = par[1];
		double gammasqr = par[2];
		double qsqr = (x * x);
		double f = 0.;
		double Norm = par[0];

		double nom = Lambdasqr * (Lambdasqr + gammasqr);
		double demon = Math.pow((Lambdasqr - qsqr), 2) + Lambdasqr * gammasqr;
		f = nom / demon;
		return f; // Norm *
		// return Norm*abs(f*f);

	}

	public static void main(String[] args) {
		H1F h1 = new H1F("test", 100, 0, 0.957);
		double etaP_eegam_BR = 5.13e-04; // BR from PRELIM CLAS result

		double time = 6.912e06; // this is in seconds 80 days
		// Derek
		double total_etaP_expected = 2.05897e+09;// 5.0e07;
		double total_etaP_Dalitzexpected = total_etaP_expected * etaP_eegam_BR;

		double upperLimit = 11463.907439624105;
		double[] par = { upperLimit, 0.957 };

		double[] pole_par = { 10, 0.59, 0.0144 }; // {A,Lambda,Gamma}
		for (int i = 0; i < h1.getxAxis().getNBins(); i++) {
			double center = h1.getXaxis().getBinCenter(i);
			h1.setBinContent(i, Eval_Kroll_wada(center, par) * Pole_FFII(center, pole_par));
		}

		TCanvas canvas = new TCanvas("aName", 800, 800);
		canvas.draw(h1);

	}

}
