#!/apps/bin/perl -w


#Script to submit simulations to the farm
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de


use strict;
use warnings;


my $nJobs = 400;	# total number of jobs
my $NEvents = 10000; #
#workflow settings
my $project = "-project clas12";
my $track = "-track simulation";
my $time = "-time 90min";
my $OS = "-os centos7";
my $shell = "-shell /bin/tcsh";
my $ram = "-ram 6g";
my $disk = "-disk 4g";
my $CPU_count = "-cores 1";

#
#
################################



my $submit_dir = "/volatile/clas12/mkunkel/MachineLearningPID";
my $lundInput_dir = "$submit_dir/LundFiles";
my $gemc_outDir = "$submit_dir/GEMCFiles";

my $command_source = "source /group/clas12/gemc/environment.csh 4a.2.1"; #4a.2.0
my $command_exit = "exit 0";

#Options to input into Gemc
my $eventString = "name=\"N\"              value=\"350\"";
my $setEventString = "name=\"N\"              value=\"$NEvents\"";
my $setEvents = "perl -p -i -e 's/$eventString/$setEventString/g' clas12.gcard";

#These will be set inside the torus and solenoid loop
my $torusString = "value=\"clas12-torus-big, -1\"";
my $solenoidString = "value=\"clas12-solenoid, 1\"";

#my $input_1 = "-input setCLARA.csh $submit_dir/clara/setCLARA.csh";
my $input_1 = "-input clas12.gcard $submit_dir/clas12_4a.2.1.gcard";


#torus setting for this study -0.75, 0.75, 1,-1.  Solenoid values for this study 0.8, 0.6
my @torusValue = ("-0.75", "1.0");
my @solenoidValue = ("0.8");
my @particleValue = ("AntiNeutron","AntiProton","Electron","Gamma","KMinus","KPlus","Neutron","PiMinus","PiPlus","Positron","Proton");
#my @particleValue = ("Positron","Proton","Electron","Gamma");


for my $p (0 .. $#particleValue){
  for my $a (0 .. $#torusValue){
    for my $b (0 .. $#solenoidValue){
      
      my $iJob = 1;
      
      my $torusSol_dir = $particleValue[$p]."/Torus".$torusValue[$a]."Sol".$solenoidValue[$b];
      my $workflow = "-workflow machineLearning_sim".$particleValue[$p]."_tor".$torusValue[$a]."sol".$solenoidValue[$b];
      my $gemcOutput_dir = "$gemc_outDir/$torusSol_dir";
      
      #Options to input into Gemc
      my $setTorusString = "value=\"clas12-torus-big, ".$torusValue[$a]."\"";
      my $setTorus = "perl -p -i -e 's/$torusString/$setTorusString/g' clas12.gcard";
      
      my $setSolenoidtString = "value=\"clas12-solenoid, ".$solenoidValue[$b]."\"";
      my $setSolenoid = "perl -p -i -e 's/$solenoidString/$setSolenoidtString/g' clas12.gcard";
      
      print "$torusSol_dir and $workflow \n";
      
      
      
      while($iJob <= $nJobs){
        #now lets get the lund file as input
        my $input_2 = "-input fullEtaPrimeDilepton.lund $lundInput_dir/$particleValue[$p]/".$particleValue[$p]."_$iJob.lund";
        
        
        #check to see in gemc file already exists
        my $gemc_out = "$gemcOutput_dir/".$particleValue[$p]."_Tor".$torusValue[$a]."Sol".$solenoidValue[$b]."_".$iJob.".ev";
        my $mv_gemc = "-output out.ev $gemc_out";
        
        if(-e $gemc_out){
          #print "YO dumbass, are you overwriting an existing file? Tsk Tsk! \n";
          $iJob++;
          next;
        }
        my $gemc_run = "gemc clas12.gcard";
        
        
        #GEMC is done, now lets set up Decoding
        
        open my $command_file, ">command.dat" or die "cannot open command.dat file:$!";
        print $command_file "$command_source ; $setEvents;  $setTorus; $setSolenoid; $gemc_run; $command_exit";
        
        close $command_file;
        
        
        my $sub = "swif add-job $workflow $project $track $time $OS $ram $disk $CPU_count $input_1 $input_2 -script command.dat $mv_gemc";
        system ($sub);
        print "$sub \n\n";
        
        
        $iJob++;
      }#end of job loop
    }#end of torus loop
  }#end of solenoid loop
}#end of particle loop
