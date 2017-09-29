#!/usr/bin/perl -w

use strict;
use warnings;



my @torusValue = ("-0.75", "1.0","-1.0");
#my @torusValue = ("-1.0");
my @solenoidValue = ("0.8");
my @particleValue = ("AntiNeutron","AntiProton","Electron","Gamma","KMinus","KPlus","Neutron","PiMinus","PiPlus","Positron","Proton");

my @particleID = (-2112,-2212,11,22,-321,321,2112,-211,211,-11,2212);
for my $p (0 .. $#particleValue){
  for $a (0 .. $#torusValue){
    for $b (0 .. $#solenoidValue){
      
      my $torusSol_dir = $particleValue[$p]."/Torus".$torusValue[$a]."Sol".$solenoidValue[$b];

      my $fileName = $particleValue[$p]."Torus".$torusValue[$a]."Sol".$solenoidValue[$b];
      
      my $createSVM = "java -jar ~/Jars/RunMLObject.jar-jar-with-dependencies.jar $fileName $particleID[$p] $torusSol_dir/*.hipo";
      
      #print "$torusSol_dir \n";
      # print "$fileName \n";
      print "$createSVM \n";
      
      system($createSVM);

      
      
    }
  }
}
