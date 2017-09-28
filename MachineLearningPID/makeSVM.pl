#!/usr/bin/perl -w

use strict;
use warnings;



my @torusValue = ("-0.75", "1.0");
#my @torusValue = ("-1.0");
my @solenoidValue = ("0.8");
my @particleValue = ("AntiNeutron","AntiProton","Electron","Gamma","KMinus","KPlus","Neutron","PiMinus","PiPlus","Positron","Proton");

for my $p (0 .. $#particleValue){
  for $a (0 .. $#torusValue){
    for $b (0 .. $#solenoidValue){
      
      my $torusSol_dir = $particleValue[$p]."/Torus".$torusValue[$a]."Sol".$solenoidValue[$b];

      my $fileName = $particleValue[$p]."Torus".$torusValue[$a]."Sol".$solenoidValue[$b].".txt";
      
      my $createSVM = "java -jar RunMLObject.jar-jar-with-dependencies.jar $fileName $torusSol_dir/*.hipo";
      
      print "$torusSol_dir \n";
      print "$fileName \n";
      print "$createSVM \n";
      
      system($createSVM);

      
      
    }
  }
}
