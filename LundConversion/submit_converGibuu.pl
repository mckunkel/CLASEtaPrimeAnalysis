#!/apps/bin/perl -w


#Script to submit simulations, decoding and reconstruction to the farm
#Last date worked on> 08/24/17
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de
#Written by: Daniel R. Lersch / d.lersch@fz-juelich.de


use strict;
use warnings;


my $iJob = 2;
my $nJobs = 4;	# total number of jobs
my $NEvents = 100000; #
#workflow settings
my $workflow = "-workflow gibuutolund";
my $project = "-project clas12";
my $track = "-track test";
my $time = "-time 5min";
my $OS = "-os centos7";
my $shell = "-shell /bin/tcsh";
my $ram = "-ram 6g";
my $disk = "-disk 4g";
my $CPU_count = "-cores 1";

#
#
################################



my $submit_dir = "/volatile/clas12/mkunkel/EtaPrimeDilepton";

my $lundOutput_dir = "$submit_dir/LundFiles";
my $gibuuInput_dir = "$submit_dir/GiBUUFiles";

my $command_source = "source clas12.cshrc";
my $command_exit = "exit 0";


#my $input_1 = "-input setCLARA.csh $submit_dir/clara/setCLARA.csh";
my $input_1 = "-input clas12.cshrc /u/home/mkunkel/CSHRC_FILES/clas12.cshrc";


while($iJob < $nJobs){
  #now lets get the gibuu file as input
  my $gibuuIn = "Dilepton_FullEvents_".$iJob.".dat";
  my $input_2 = "-input $gibuuIn $gibuuInput_dir/$gibuuIn";
  
  
  #check to see in gemc file already exists
  my $lund_out = "$lundOutput_dir/Dilepton_FullEvents_".$iJob.".lund";
  my $mv_lund = "-output Dilepton_FullEvents_1.lund $lund_out";
  
  if(-e $lund_out){
    print "YO dumbass, are you overwriting an existing file? Tsk Tsk!";
    $iJob++;
    next;
  }
  my $conversion_run = "/apps/scicomp/java/jdk1.8/bin/java -Xmx3.5G client.UseGiBuuToLund $gibuuIn Dilepton_FullEvents.lund 100000";
  
  
  open my $command_file, ">command.dat" or die "cannot open command.dat file:$!";
  #print $command_file "$command_source ; $setEvents;  $setTorus; $setSolenoid; $gemc_run; $doDecoding; $command_exit";
  print $command_file "$command_source ; $conversion_run; $command_exit";
  
  close $command_file;
  
  
  my $sub = "swif add-job $workflow $project $track $time $OS $ram $disk $CPU_count $input_1 $input_2 -script command.dat $mv_lund";
  system ($sub);
  print "$sub \n\n";
  
  
  $iJob++;
  
  
}
