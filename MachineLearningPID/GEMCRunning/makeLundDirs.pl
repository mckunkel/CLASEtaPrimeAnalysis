#!/apps/bin/perl -w


#Script to make directories
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de


use strict;
use warnings;

my $submit_dir = "/volatile/clas12/mkunkel/MachineLearningPID";
my $lundInput_dir = "$submit_dir/LundFiles";
my @particleValue = ("AntiNeutron","AntiProton","Electron","Gamma","KMinus","KPlus","Neutron","PiMinus","PiPlus","Positron","Proton");
for my $p (0 .. $#particleValue)
{
  my $my_dir = "$lundInput_dir/".$particleValue[$p];
  my $mkdir = "mkdir $my_dir";
  print "$mkdir \n";
  system($mkdir);
}
