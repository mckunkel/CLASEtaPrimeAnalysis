#!/apps/bin/perl -w


#Script to make swif workflows
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de


use strict;
use warnings;


my @torusValue = ("-0.75", "0.75", "1.0", "-1.0");
my @solenoidValue = ("0.6", "0.8");


for $a (0 .. $#torusValue)
{
  for $b (0 .. $#solenoidValue){
	  my $rm_workflow = "swif cancel etaP_sim_tor".$torusValue[$a]."sol".$solenoidValue[$b]." -delete";
    my $workflow = "swif create etaP_sim_tor".$torusValue[$a]."sol".$solenoidValue[$b];
system($rm_workflow);
    print "$workflow \n";
    system($workflow);
  }
}
