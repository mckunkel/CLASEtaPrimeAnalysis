#!/apps/bin/perl -w


#Script to make directories
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de


use strict;
use warnings;


my @torusValue = ("-0.75", "0.75", "1.0", "-1.0");
my @solenoidValue = ("0.6", "0.8");

my $submit_dir = "/volatile/clas12/mkunkel/EtaPrimeDilepton";
my $type_dir = "DecodedFiles";

for $a (0 .. $#torusValue)
{
  for $b (0 .. $#solenoidValue){
    
    my $torusSol_dir = "Torus".$torusValue[$a]."Sol".$solenoidValue[$b];
    my $fileOutput_dir = "$submit_dir/".$type_dir."/$torusSol_dir";

    my $list_file = "totfile_Torus".$torusValue[$a]."Sol".$solenoidValue[$b].".dat";

    my $mklist = "ls ".$fileOutput_dir."/*.hipo > $list_file";
    
    system($mklist);
  }
}
