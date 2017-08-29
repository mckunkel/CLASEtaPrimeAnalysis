package domain;

//Example of reading in a hipo-file
//Last date worked on: 08/28/17
//Michael C. Kunkel: m.kunkel@fz-juelich.de
//Daniel Lersch: d.kunkel@fz-juelich.de


import org.jlab.clas.physics.Particle;
import org.jlab.groot.data.H1F;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.HipoDataSource;
import org.jlab.groot.ui.TCanvas;


public class readFile {
  //Stuff for reading in a hipo-file:
  //+++++++++++++++++++++++++++++++++++++++++++++++++++++++	
  private static HipoDataSource[] hipoReader = null;
  private static int[] Nevents = null;
  private static int NinputFiles = 0;
  //+++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	
  //Stuff for looking at different banks:
  //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
  private static DataEvent Event = null;
  
  private static DataBank MCBank = null;
  private static boolean is_MCBank = false;
  private static int NgenParticle = 0;
  
  private static DataBank recParticleBank = null;
  private static boolean is_recParticleBank = false;
  private static int NrecParticle = 0;
  //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
   
  //Stuff for looking at specific reactions:
  //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
  private static int[] particleIDS = null;
  private static Particle[] genParticle = null;
  private static Particle[] recParticle = null;
 // private static double electron_mass = 0.000511;
  //private static double proton_mass = 0.938;
  //+++++++++++++++++++++++++++++++++++++++++++++++++++++++
  
  private static readFile RF = new readFile();
  
  public static void main(String[] args) {
	  
	  
	  //**************************************************
	  //STAFF THAT HAS TO BE SET:
	  RF.setNinputFile(1); //--> Set the number of hipo-files you want to analyse
	  RF.setReaders("/Users/daniellersch/Desktop/CLAS/EtaPrimeDilepton", "fullEtaPrimeDilepton_"); //-->Define the directoy, where the out_bla.hipo files are located
	  //and put in the name of each file e.g. fullEtaPrime_....
	 
	  String[] myReaction = {"e-","p","e-","e+"}; //Define the DECAY-particles here / Beam and Target are automatically set
	  RF.loadReaction(myReaction);
	 //**************************************************
	  
	  
	  
	  
	  //Define some quantities here to look at:
	  //Look at missing mass deduced from beam, target, proton and one electron
	  //either the scattered one, or the one from etaP->e+e-y
	  //Therefore look at two combinations...
	  
	  double missing_mass_rec_comb1 = 0;
	  Particle missing_rec_particle_comb1 = new Particle();
	  double missing_mass_rec_comb2 = 0;
	  Particle missing_rec_particle_comb2 = new Particle();
	  
	  
	  double missing_mass_gen_comb1 = 0;
	  Particle missing_gen_particle_comb1 = new Particle();
	  double missing_mass_gen_comb2 = 0;
	  Particle missing_gen_particle_comb2 = new Particle();
	  
	  //ja... it seems that this is the current way to add particle vectors
	  //but maybe I am stupid and missing something...
	  //Define the missing vector, for two possible electrons...
	  missing_rec_particle_comb1.copy(recParticle[0]);
	  missing_rec_particle_comb1.combine(recParticle[1], +1);
	  missing_rec_particle_comb2.copy(recParticle[0]);
	  missing_rec_particle_comb2.combine(recParticle[1], +1);
	  
	  missing_gen_particle_comb1.copy(genParticle[0]);
	  missing_gen_particle_comb1.combine(genParticle[1], +1);
	  missing_gen_particle_comb2.copy(genParticle[0]);
	  missing_gen_particle_comb2.combine(genParticle[1], +1);
	  
	  H1F mmHist_rec1 = new H1F("mmHist_rec1",100,0,1.0);
	  mmHist_rec1.setTitleX("Missing Mass M_x(pe-)");
	  mmHist_rec1.setTitleY("Entries");
	  mmHist_rec1.setTitle("Reconstructed Particles: Comb1");
	  
	  H1F mmHist_rec2 = new H1F("mmHist_rec2",100,0,1.0);
	  mmHist_rec2.setTitleX("Missing Mass M_x(pe-)");
	  mmHist_rec2.setTitleY("Entries");
	  mmHist_rec2.setTitle("Reconstructed Particles: Comb2");
	  
	  H1F mmHist_gen1 = new H1F("mmHist_gen1",100,0,1.0);
	  mmHist_gen1.setTitleX("Missing Mass M_x(pe-)");
	  mmHist_gen1.setTitleY("Entries");
	  mmHist_gen1.setTitle("Generated Particles: Comb1");
	  
	  H1F mmHist_gen2 = new H1F("mmHist_gen2",100,0,1.0);
	  mmHist_gen2.setTitleX("Missing Mass M_x(pe-)");
	  mmHist_gen2.setTitleY("Entries");
	  mmHist_gen2.setTitle("Generated Particles: Comb2");
	  
	  //Loop over the number of hipo-files
	  for(int iF=0;iF < RF.getNinputFile();iF++) {
	  //=============================================================	 File-Loop 
		  
		  //Loop over all events in each hipo-file:
		  //============================================================= Event-Loop
		  for(int ev=0;ev < Nevents[iF];ev++) {
			  Event = (DataEvent) hipoReader[iF].gotoEvent(ev);
			  RF.setBanks(Event); //Define the banks here...
			 
			  //The event-filter here is based on the current PID-implementation...
			  //We can do better than that...
			  //The filter also sets the particle 4momentum-vectors....
			  //Also you can define whether you are looking at MC or REC-events...
			  if(RF.Filter("REC") == 1) {
			    missing_rec_particle_comb1.combine(recParticle[2], -1);
			    missing_rec_particle_comb1.combine(recParticle[3], -1);
			    missing_mass_rec_comb1 = 0.001*missing_rec_particle_comb1.mass(); //--> I am not sure about this scaling factor
			    //I have the hunch, that either I am doing something wrong, or that components of the 4Vector might need to be scaled because of the unit-issues
			    
			    mmHist_rec1.fill(missing_mass_rec_comb1);
			    
			    missing_rec_particle_comb2.combine(recParticle[4], -1);
			    missing_rec_particle_comb2.combine(recParticle[3], -1);
			    missing_mass_rec_comb2 = 0.001*missing_rec_particle_comb2.mass();
			    
			    mmHist_rec2.fill(missing_mass_rec_comb2);
			  }
			  
			  if(RF.Filter("MC") == 1) {
				    missing_gen_particle_comb1.combine(genParticle[2], -1);
				    missing_gen_particle_comb1.combine(genParticle[3], -1);
				    missing_mass_gen_comb1 = 0.001*missing_gen_particle_comb1.mass();
				    
				    mmHist_gen1.fill(missing_mass_gen_comb1);
				    
				    missing_gen_particle_comb2.combine(genParticle[4], -1);
				    missing_gen_particle_comb2.combine(genParticle[3], -1);
				    missing_mass_gen_comb2 = 0.001*missing_gen_particle_comb2.mass();
				    
				    mmHist_gen2.fill(missing_mass_gen_comb2);
				  }
			  
		  }
		//============================================================= Event-Loop
	  }
	 //============================================================= File-Loop
	  
	  
	  
	 //Plot the results here:
	 TCanvas c = new TCanvas("c",1200,800);
	 c.divide(2, 2);
	 c.cd(0);
	 c.draw(mmHist_rec1);
	 c.cd(1);
	 c.draw(mmHist_gen1);
	 c.cd(2);
	 c.draw(mmHist_rec2);
	 c.cd(3);
	 c.draw(mmHist_gen2);
	 
  }
  
  
  
  
  
  //Everything for reading in N hipo-files:
  //*******************************************************
  public void setNinputFile(int Nin) {
	  NinputFiles = Nin;
  }
  
  public int getNinputFile() {
	  return NinputFiles;
  }
  
  public void setReaders(String DATADIR, String FileName) {
	  hipoReader = new HipoDataSource[NinputFiles];
	  Nevents = new int[NinputFiles];
	  
	  String hipoFileName;
	  int fileIndex = 0; //In case your index starts at 1 or whatever...
	  for(int i=0;i<NinputFiles;i++) {
		 fileIndex = i;
		 hipoFileName = DATADIR + "/out_" + FileName + fileIndex + ".hipo"; 
		 hipoReader[i] = new HipoDataSource();
		 hipoReader[i].open(hipoFileName);
		 Nevents[i] = hipoReader[i].getSize();
	  }
	  
  }
  //*******************************************************	
  
  //Look at various banks:
  //*******************************************************
  public void setBanks(DataEvent event) {
	  String MCName = "MC::Particle";
	  MCBank = null;
	  is_MCBank = false;
	  NgenParticle = 0;
	  
	  String recParticleName = "REC::Particle";
	  recParticleBank = null;
	  is_recParticleBank = false;
	  NrecParticle = 0;
	  
	  if(event.hasBank(MCName)) {
		  is_MCBank = true;
		  MCBank = event.getBank(MCName);
		  NgenParticle = MCBank.rows();
	  }
	  
	  if(event.hasBank(recParticleName)) {
		  is_recParticleBank = true;
		  recParticleBank = event.getBank(recParticleName);
		  NrecParticle = recParticleBank.rows();
	  }
	  
  }
  //*******************************************************
  
  
  
 
  //Stuff for looking at specific particle types within a given reaction:
  //****************************************************************************
  
  //Get pid for each particle from the reaction you specified:
  //############################################################
  public void loadReaction(String[] Reaction) {
	  
	  int NdecayParticles = Reaction.length;
	  particleIDS = new int[NdecayParticles];
	 
	  String[] names = {"p","e-","e+","pi+","pi-","g"};
	  int[] numbers = {2212, 11, -11, 211, -211, 22};
	  
	  recParticle = new Particle[NdecayParticles + 2]; // Because we have beam and target --> 2 extra particles
	  genParticle = new Particle[NdecayParticles + 2]; // Because we have beam and target --> 2 extra particles
	 
	  recParticle[0] = new Particle();
	  recParticle[1] = new Particle();
	  
	  genParticle[0] = new Particle();
	  genParticle[1] = new Particle();
	  
	//Set beam-particle and target:
	  //1.) Beam:
	  recParticle[0].setVector(11, 0.0, 0.0, 11.0, 0.0, 0.0, 0.0);
	  genParticle[0].setVector(11, 0.0, 0.0, 11.0, 0.0, 0.0, 0.0);
	  //2.) Target:
	  recParticle[1].setVector(2212, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	  genParticle[1].setVector(2212, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	  
	  for(int r=0;r<NdecayParticles;r++) {
		  recParticle[r+2] = new Particle();
		  genParticle[r+2] = new Particle();
		  for(int h=0;h<names.length;h++) {
			  if(Reaction[r].equals(names[h])) {
				  particleIDS[r] = numbers[h];
			  }
		  }
	  }
  }
  //############################################################
  
  //Reaction filter: is one if all particles that have been specified
  //in the reaction string are found in the rec::particle-bank:
  //############################################################
  public int Filter(String Option) {
	  int out = 0;
	  
	  int NdecayParticles = particleIDS.length;
	  DataBank currentBank = null;
	  boolean is_currentBank = false;
	  int Nparticles = 0;
	  
	  if(Option.equals("REC")) {
		  currentBank = recParticleBank;
		  is_currentBank = is_recParticleBank;
		  Nparticles = NrecParticle;
	  }else if(Option.equals("MC")) {
		  currentBank = MCBank;
		  is_currentBank = is_MCBank;
		  Nparticles = NgenParticle;
	  }
	  
	  
	  if(Nparticles >= NdecayParticles && NdecayParticles >= 2) {
	  
	    int currentPid = 0;
	    int current_particle = 0;
	    int found_reaction_particle = 0;
	    int matchedID = 0;
	    int[] usedEntry = new int[Nparticles];
	    
	    for(int entr=0;entr < Nparticles;entr++) {
		    usedEntry[entr] = 0;
	    }
	  
	    int[] requestedID = particleIDS;
	    double px,py,pz,vx,vy,vz;
	  
	    if(is_currentBank) {
		  //------------------------------------------------------------------------------
		  while(current_particle < NdecayParticles) {
			  matchedID = 0;
			  px = py = pz = 0.0;
			  vx = vy = vz = 0.0;
			 
			  for(int t=0;t<Nparticles;t++) {
				  currentPid = currentBank.getInt("pid", t);
				  
				  if(currentPid == requestedID[current_particle] && matchedID == 0 && usedEntry[t] == 0) {
					  matchedID = 1;
					  usedEntry[t] = 1;
					  found_reaction_particle++;
					  
					  px = currentBank.getFloat("px", t); //I know having a double as a float... but the setVector-methoid requires doubles...
					  py = currentBank.getFloat("py", t);
					  pz = currentBank.getFloat("pz", t);
					  
					  vx = currentBank.getFloat("vx", t); //I know having a double as a float... but the setVector-methoid requires doubles...
					  vy = currentBank.getFloat("vy", t);
					  vz = currentBank.getFloat("vz", t);
					  
					  
					  if(Option.equals("REC")) {
						  recParticle[current_particle+2].setVector(currentPid, px, py, pz, vx, vy, vz);
					  }else if(Option.equals("MC")) {
						  genParticle[current_particle+2].setVector(currentPid, px, py, pz, vx, vy, vz);
					  }
					  
					  
				  }
				  
			  }
			
			  current_particle++;
		  }
		 //------------------------------------------------------------------------------
	    }
	  
	    if(found_reaction_particle == NdecayParticles) {
	     	  out = 1;
	     	  
	   }else out = 0;
	  }
	  return out;
  }
  //############################################################
  //****************************************************************************
  
 
  
}
