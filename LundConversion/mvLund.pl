#!/apps/bin/perl -w

use strict;
use warnings;

my $iJob = 0;

my $lundOutput_dir = "/volatile/clas12/mkunkel/EtaPrimeDilepton/LundFiles";

my $nJobs = 50;	# total number of jobs


while($iJob < $nJobs){
 
  my $mv_lund = "mv $lundOutput_dir/Dilepton_FullEvents_".$iJob.".lund $lundOutput_dir/Done";

  system ($mv_lund);
print "$mv_lund \n";
  $iJob++;
  
  
}
