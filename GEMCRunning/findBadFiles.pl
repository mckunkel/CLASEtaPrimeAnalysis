#!/apps/bin/perl -w

#Script to find files the have lower size than average
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de


use strict;
use warnings;
use POSIX;

my $nJobs = 470;	# total number of jobs
my $submit_dir = "/volatile/clas12/mkunkel/EtaPrimeDilepton";

#my @torusValue = ("-0.75", "0.75", "1.0", "-1.0");
#my @solenoidValue = ("0.6", "0.8");
my @torusValue = ("-1.0");
my @solenoidValue = ("0.8");
my $deletedFile = 0;

#interate through torus
for $a (0 .. $#torusValue)
{
  #interate through solenoid
  for $b (0 .. $#solenoidValue){
    my $iJob = 0;
    my $doneJobs = 0;
    my $totalSize = 0;
    
    my $torusSol_dir = "Torus".$torusValue[$a]."Sol".$solenoidValue[$b];
    my $gemcOutput_dir = "$submit_dir/GEMCFiles/$torusSol_dir";
    #find average file size from all files in $gemcOutput_dir
    while($iJob < $nJobs){
      my $gemc_out = "$gemcOutput_dir/FullDilepton_Tor".$torusValue[$a]."Sol".$solenoidValue[$b]."_".$iJob.".ev";
      if(-e $gemc_out){
        $doneJobs++;
        my $filesize = -s $gemc_out;
        $filesize = ceil($filesize/(1024*1024));
        $totalSize = $totalSize + $filesize;
      }
      $iJob++;
    }#end of while loop
    #now we have the size of all the files, lets find the average
    my $average = $totalSize/$doneJobs;
    print "Average is ", $average,"\n";
    
    #re-interate in while loop for files in $gemcOutput_dir to see if file is less than average, if so, delete it
    $iJob = 0;
    while($iJob < $nJobs){
      my $gemc_out = "$gemcOutput_dir/FullDilepton_Tor".$torusValue[$a]."Sol".$solenoidValue[$b]."_".$iJob.".ev";
      if(-e $gemc_out){
        my $filesize = -s $gemc_out;
        $filesize = ceil($filesize/(1024*1024));
        if($filesize<0.985*$average){
          print "$gemc_out is to be deleted  \n";
          print "$filesize is fileSize and Average is  $average \n";
          my $rmFile = "rm $gemc_out";
          $deletedFile++;
          #system($rmFile);
        }
      }
      $iJob++;
    }#end of while loop
  }#end of solenoid loop
}#end of torus loops
print "number of deleted files is $deletedFile \n";
