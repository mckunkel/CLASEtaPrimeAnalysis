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
package domain;

import org.jlab.clas.physics.Particle;
import org.jlab.groot.data.H1F;
import org.jlab.groot.graphics.EmbeddedCanvas;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;

public class Analysis {

	// Stuff for reading in a hipo-file:
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private HipoDataSource[] hipoReader = null;
	private int[] Nevents = null;
	private int NinputFiles = 0;
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++

	// Stuff for looking at different banks:
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private DataEvent Event = null;

	private DataBank MCBank = null;
	private boolean is_MCBank = false;
	private int NgenParticle = 0;

	private DataBank recParticleBank = null;
	private boolean is_recParticleBank = false;
	private int NrecParticle = 0;
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++

	// Stuff for looking at specific reactions:
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private int[] particleIDS = null;
	private Particle[] genParticle = null;
	private Particle[] recParticle = null;

	// Canvas for plots
	private EmbeddedCanvas can = null;
	private int updateTime = 2000;

	// Histograms for Canvas
	private H1F mmHist_rec1 = null;
	private H1F mmHist_rec2 = null;
	private H1F mmHist_gen1 = null;
	private H1F mmHist_gen2 = null;

	// Some particles
	Particle missing_rec_particle_comb1 = null;
	Particle missing_rec_particle_comb2 = null;

	Particle missing_gen_particle_comb1 = null;
	Particle missing_gen_particle_comb2 = null;

	public Analysis() {

		init();

	}

	private void init() {
		createCanvas();
		createHistograms();
		createParticles();
	}

	private void createCanvas() {
		this.can = new EmbeddedCanvas();
		this.can.initTimer(updateTime);
		this.can.divide(2, 2);

	}

	private void createHistograms() {
		this.mmHist_rec1 = new H1F("mmHist_rec1", 100, 0, 1.0);
		this.mmHist_rec1.setTitleX("Missing Mass M_x(pe-)");
		this.mmHist_rec1.setTitleY("Entries");
		this.mmHist_rec1.setTitle("Reconstructed Particles: Comb1");

		this.mmHist_rec2 = new H1F("mmHist_rec2", 100, 0, 1.0);
		this.mmHist_rec2.setTitleX("Missing Mass M_x(pe-)");
		this.mmHist_rec2.setTitleY("Entries");
		this.mmHist_rec2.setTitle("Reconstructed Particles: Comb2");

		this.mmHist_gen1 = new H1F("mmHist_gen1", 100, 0, 1.0);
		this.mmHist_gen1.setTitleX("Missing Mass M_x(pe-)");
		this.mmHist_gen1.setTitleY("Entries");
		this.mmHist_gen1.setTitle("Generated Particles: Comb1");

		this.mmHist_gen2 = new H1F("mmHist_gen2", 100, 0, 1.0);
		this.mmHist_gen2.setTitleX("Missing Mass M_x(pe-)");
		this.mmHist_gen2.setTitleY("Entries");
		this.mmHist_gen2.setTitle("Generated Particles: Comb2");

	}

	private void createParticles() {
		this.missing_rec_particle_comb1 = new Particle();
		this.missing_rec_particle_comb2 = new Particle();

		this.missing_gen_particle_comb1 = new Particle();
		this.missing_gen_particle_comb2 = new Particle();
	}

}
