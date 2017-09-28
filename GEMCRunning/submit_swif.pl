#!/apps/bin/perl -w


#Script to submit simulations, decoding and reconstruction to the farm
#Last date worked on> 08/24/17
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de
#Written by: Daniel R. Lersch / d.lersch@fz-juelich.de


use strict;
use warnings;


my $nJobs = 449;	# total number of jobs
my $NEvents = 10000; #
#workflow settings
my $project = "-project clas12";
my $track = "-track simulation";
my $time = "-time 400min";
my $OS = "-os centos7";
my $shell = "-shell /bin/tcsh";
my $ram = "-ram 6g";
my $disk = "-disk 4g";
my $CPU_count = "-cores 1";

#
#
################################



my $submit_dir = "/volatile/clas12/mkunkel/EtaPrimeDilepton";
my $lundInput_dir = "$submit_dir/LundFiles";

my $reconOutput_dir = "$submit_dir/ReconstructedFiles";


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
#my @torusValue = ("-0.75", "0.75", "1.0", "-1.0");
#my @solenoidValue = ("0.6", "0.8");
my @torusValue = ("-1.0");
my @solenoidValue = ("0.8");

for $a (0 .. $#torusValue)
{
  for $b (0 .. $#solenoidValue){
    my $iJob = 0;

    my $torusSol_dir = "Torus".$torusValue[$a]."Sol".$solenoidValue[$b];
    my $workflow = "-workflow full_sim_tor".$torusValue[$a]."sol".$solenoidValue[$b];
    my $gemcOutput_dir = "$submit_dir/GEMCFiles/$torusSol_dir";
    
    #Options to input into Gemc
    my $setTorusString = "value=\"clas12-torus-big, ".$torusValue[$a]."\"";
    my $setTorus = "perl -p -i -e 's/$torusString/$setTorusString/g' clas12.gcard";
    
    my $setSolenoidtString = "value=\"clas12-solenoid, ".$solenoidValue[$b]."\"";
    my $setSolenoid = "perl -p -i -e 's/$solenoidString/$setSolenoidtString/g' clas12.gcard";
    
    print "$torusSol_dir and $workflow \n";
    
    
    
    while($iJob < $nJobs){
      #now lets get the lund file as input
      my $input_2 = "-input fullEtaPrimeDilepton.lund $lundInput_dir/AllDalitz_$iJob.lund";
      
      
      #check to see in gemc file already exists
      my $gemc_out = "$gemcOutput_dir/FullDilepton_Tor".$torusValue[$a]."Sol".$solenoidValue[$b]."_".$iJob.".ev";
      my $mv_gemc = "-output out.ev $gemc_out";
      
      if(-e $gemc_out){
        #print "YO dumbass, are you overwriting an existing file? Tsk Tsk! \n";
        $iJob++;
        next;
      }
      my $gemc_run = "gemc clas12.gcard";
      
      
      open my $command_file, ">command.dat" or die "cannot open command.dat file:$!";
      print $command_file "$command_source ; $setEvents;  $setTorus; $setSolenoid; $gemc_run; $command_exit";
      
      close $command_file;
      
      
      my $sub = "swif add-job $workflow $project $track $time $OS $ram $disk $CPU_count $input_1 $input_2 -script command.dat $mv_gemc";
      system ($sub);
      print "$sub \n\n";
      
      
      $iJob++;
    }#end of job loop
  }#end of solenoid loop
}#end of torus loop
