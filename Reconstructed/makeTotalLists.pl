#!/apps/bin/perl -w


#Script to make directories
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de


use strict;
use warnings;


my @torusValue = ("-0.75", "0.75", "1.0", "-1.0");
my @solenoidValue = ("0.6", "0.8");


for $a (0 .. $#torusValue)
{
  for $b (0 .. $#solenoidValue){
    
    my $torusSol_dir = "Torus".$torusValue[$a]."Sol".$solenoidValue[$b];
    my $list_file = "totfile_Torus".$torusValue[$a]."Sol".$solenoidValue[$b].".dat";

    my $mklist = "ls ".$torusSol_dir."/*.hipo > $list_file";
    
    system($mklist);
  }
}
