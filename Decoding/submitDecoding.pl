#!/apps/bin/perl -w


#Script to submit simulations, decoding and reconstruction to the farm
#Last date worked on> 08/24/17
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de

use strict;
use warnings;


my $nJobs = 4;	# total number of jobs
my $NEvents = 10000; #
#workflow settings
my $project = "-project clas12";
my $track = "-track simulation";
my $time = "-time 10min";
my $OS = "-os centos7";
my $shell = "-shell /bin/tcsh";
my $ram = "-ram 6g";
my $disk = "-disk 4g";
my $CPU_count = "-cores 1";

#
#
################################
my $submit_dir = "/volatile/clas12/mkunkel/EtaPrimeDilepton";

my $clara_dir = "/work/clas12/mkunkel/myClara4a.8.1";
my $coatjava_dir = "$clara_dir/plugins/clas12";

my $command_source = "source setCLARA.csh /work/clas12/mkunkel/myClara4a.8.1"; #4a.2.0
my $input_0 = "-input setCLARA.csh /work/clas12/mkunkel/EtaPrimeDilepton/CLASEtaPrimeAnalysis/setCLARA.csh";

my $command_exit = "exit 0";

#torus setting for this study -0.75, 0.75, 1,-1.  Solenoid values for this study 0.8, 0.6
my @torusValue = ("-0.75", "0.75", "1.0", "-1.0");
my @solenoidValue = ("0.6", "0.8");


for $a (0 .. $#torusValue)
{
  for $b (0 .. $#solenoidValue){
    my $iJob = 0;
    
    my $torusSol_dir = "Torus".$torusValue[$a]."Sol".$solenoidValue[$b];
    my $workflow = "-workflow etaP_sim_tor".$torusValue[$a]."sol".$solenoidValue[$b];
    my $gemcInput_dir = "$submit_dir/GEMCFiles/$torusSol_dir";
    my $decoded_dir = "$submit_dir/DecodedFiles/$torusSol_dir";

    
    while($iJob < $nJobs){
      
      #check to see in gemc file already exists
      my $gemc_in = "EtaPrimeDilepton_Tor".$torusValue[$a]."Sol".$solenoidValue[$b]."_".$iJob.".ev";
      my $input_1 = "-input $gemc_in $gemcInput_dir/$gemc_in";

      my $decodedData = "EtaPrimeDilepton_Tor".$torusValue[$a]."Sol".$solenoidValue[$b]."_".$iJob.".hipo";
      
      my $decodedOut = "$decoded_dir/$decodedData";
      if(-e $decodedOut){
        #print "YO dumbass, are you overwriting an existing file? Tsk Tsk! \n";
        $iJob++;
        next;
      }
      
      
      
      my $doDecoding = "$coatjava_dir/bin/evio2hipo -r 11 -t ".$torusValue[$a]." -s ".$solenoidValue[$b]." -o $decodedData $gemc_in";
      my $mv_decoded = "-output $decodedData $decoded_dir/$decodedData";
      
      
      open my $command_file, ">command.dat" or die "cannot open command.dat file:$!";
      print $command_file "$command_source $doDecoding; $command_exit";
      close $command_file;
      
      
      my $sub = "swif add-job $workflow $project $track $time $OS $ram $disk $CPU_count $input_0 $input_1 -script command.dat $mv_decoded";
      system ($sub);
      print "$sub \n\n";
      
      
      $iJob++;
    }#end of job loop
  }#end of solenoid loop
}#end of torus loop
