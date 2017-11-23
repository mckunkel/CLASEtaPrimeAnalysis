#!/apps/bin/perl -w


#Script to make directories
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de


use strict;
use warnings;


#my @torusValue = ("-0.75", "0.75", "1.0", "-1.0");
#my @solenoidValue = ("0.6", "0.8");
my @torusValue = ("-1.0");
my @solenoidValue = ("0.8");
my $gemcDataDir = "/volatile/clas12/mkunkel/EtaPrimeDilepton/GEMCFiles";


for $a (0 .. $#torusValue)
{
  for $b (0 .. $#solenoidValue){

    my $torusSol_dir = "$gemcDataDir/Torus".$torusValue[$a]."Sol".$solenoidValue[$b];
    
    my $mkdir = "mkdir $torusSol_dir";
    
    system($mkdir);
  }
}
