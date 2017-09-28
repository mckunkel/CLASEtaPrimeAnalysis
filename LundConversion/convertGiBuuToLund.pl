#!/apps/bin/perl -w

use strict;
use warnings;

my $iJob = 0;

my $lundOutput_dir = "/volatile/clas12/mkunkel/EtaPrimeDilepton/LundFiles";
my $gibuuOutput_dir = "/volatile/clas12/mkunkel/EtaPrimeDilepton/GiBUUFiles";




my $nJobs = 10;	# total number of jobs


while($iJob < $nJobs){
  
  
  #check to see in gemc file already exists
  my $gibuu_out = "$gibuuOutput_dir/Dilepton_FullEvents_".$iJob.".dat";
  
  #my $lund_out = "$lundOutput_dir/Dilepton_FullEvents_".$iJob.".lund";
  my $lund_out = "$lundOutput_dir/FullEvents_".$iJob.".lund";

  my $mv_lund = "mv Dilepton_FullEvents_1.lund $lund_out";
  
  if(-e $lund_out){
    print "YO dumbass, are you overwriting an existing file? Tsk Tsk!";
    $iJob++;
    next;
  }

  my $gibuuToLund = "java -jar ~/Jars/UseGiBuuToLund.jar-jar-with-dependencies.jar $gibuu_out Dilepton_FullEvents.lund 250000";

  system ($gibuuToLund);
  system ($mv_lund);
  print "Done with lund file $lund_out \n";
  $iJob++;
  
  
}
