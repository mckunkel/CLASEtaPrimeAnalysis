#!/apps/bin/perl -w


#Script to make swif workflows
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de


use strict;
use warnings;


my @torusValue = ("-0.75", "1.0");
my @solenoidValue = ("0.8");
my @particleValue = ("AntiNeutron","AntiProton","Electron","Gamma","KMinus","KPlus","Neutron","PiMinus","PiPlus","Positron","Proton");

for my $p (0 .. $#particleValue){
  for $a (0 .. $#torusValue){
    for $b (0 .. $#solenoidValue){
      my $workflow = "machineLearning_sim".$particleValue[$p]."_tor".$torusValue[$a]."sol".$solenoidValue[$b];
      my $runworkflow = "swif run $workflow";
      
      print "$runworkflow \n";
      system($runworkflow);
    }
  }
}
