#!/apps/bin/perl -w


#Script to make directories
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de


use strict;
use warnings;


my @torusValue = ("-0.75", "1.0");
my @solenoidValue = ("0.8");
my @particleValue = ("AntiNeutron","AntiProton","Electron","Gamma","KMinus","KPlus","Neutron","PiMinus","PiPlus","Positron","Proton");

my $submit_dir = "/volatile/clas12/mkunkel/MachineLearningPID";
my $outDir = "$submit_dir/DecodedFiles";


for my $p (0 .. $#particleValue){
  
  for my $a (0 .. $#torusValue){
    for my $b (0 .. $#solenoidValue){
      
      my $torusSol_dir = $particleValue[$p]."/Torus".$torusValue[$a]."Sol".$solenoidValue[$b];
      #my $output_dir = "$outDir/".$particleValue[$p];
      my $output_dir = "$outDir/$torusSol_dir";
      my $mkdir = "mkdir $output_dir";
      print"$mkdir \n";
      system($mkdir);
    }
  }
}