#!/apps/bin/perl -w


#Script to make directories
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de


use strict;
use warnings;

my $submit_dir = "/volatile/clas12/mkunkel/EtaPrimeDilepton";
my $outDir = "$submit_dir/ReconstructedFiles";

my @torusValue = ("-0.75", "1.0");
my @solenoidValue = ("0.8");
my @particleValue = ("AntiNeutron","AntiProton","Electron","Gamma","KMinus","KPlus","Neutron","PiMinus","PiPlus","Positron","Proton");
for my $p (0 .. $#particleValue){
  for my $a (0 .. $#torusValue){
    for my $b (0 .. $#solenoidValue){
      
      my $output_dirI = "$outDir/".$particleValue[$p];
      my $mkdirI = "mkdir $output_dirI";
      system($mkdirI);
      
      
      my $torusSol_dir = $particleValue[$p]."/Torus".$torusValue[$a]."Sol".$solenoidValue[$b];
      my $output_dirII = "$outDir/$torusSol_dir";
      my $mkdir = "mkdir $output_dirII";
      print"$mkdirII \n";
      system($mkdirII);
      
    }
  }
}
