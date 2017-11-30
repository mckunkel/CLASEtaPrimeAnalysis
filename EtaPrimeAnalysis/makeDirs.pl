#!/apps/bin/perl -w


#Script to make directories
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de


use strict;
use warnings;


my @torusValue = ("-0.75", "0.75", "1.0", "-1.0");
my @solenoidValue = ("0.6", "0.8");

my $torusString = "\"-0.75\", \"0.75\", \"1.0\", \"-1.0\"";
my $solenoidString = "\"0.6\", \"0.8\"";

my $decodedDataDir = "/volatile/clas12/mkunkel/EtaPrimeDilepton/DecodedFiles";


for $a (0 .. $#torusValue)
{
  for $b (0 .. $#solenoidValue){
    
    my $torusSol_dir = "$decodedDataDir/Torus".$torusValue[$a]."Sol".$solenoidValue[$b];
    
    my $mkdir = "mkdir $torusSol_dir";
    
    system($mkdir);
    
    #now lets copy the decoding perl script and set the defaults
    my $perl_out = "doDecoding_Tor".$torusValue[$a]."Sol".$solenoidValue[$b].".pl";

    my $cp_perl = "cp doDecoding.pl $perl_out";
    system($cp_perl);
    
    my $setTorusString = "$torusValue[$a]";
    my $setTorus = "perl -p -i -e 's/$torusString/$setTorusString/g' $perl_out";
    
    
    my $setSolenoidtString = "$solenoidValue[$b]";
    my $setSolenoid = "perl -p -i -e 's/$solenoidString/$setSolenoidtString/g' $perl_out";
    
    system($setTorus);
    system($setSolenoid);

    #make executables
    system("chmod +x $perl_out");
    #move the new script to the new directory
    my $mv_perl = "mv $perl_out $torusSol_dir";
    system($mv_perl);

    
    
  }
}
