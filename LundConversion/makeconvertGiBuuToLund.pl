#!/apps/bin/perl -w


#Script to make directories
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de


use strict;
use warnings;


my $nScripts = 4; #N scripts to produce
my $nJobs = 500;
my $divisor = $nJobs/$nScripts;

my @torusValue = ("-0.75", "0.75", "1.0", "-1.0");
my @solenoidValue = ("0.6", "0.8");

my $jobString = "iJob = 0";
my $njobString = "nJobs = 499";

my $lowerLimit = 0;
my $upperLimit = $divisor;

for (my $i=1; $i<=$nScripts; $i++)
{
  #now lets copy the decoding perl script and set the defaults
  my $perl_out = "convertGiBuuToLund_".$i.".pl";
  
  my $cp_perl = "cp orginal_convertGiBuuToLund.pl $perl_out";
  system($cp_perl);
  
  my $lowerLimitString = "iJob = $lowerLimit";
  my $upperLimitString = "nJobs = $upperLimit";
  
  my $setLowerLimit = "perl -p -i -e 's/$jobString/$lowerLimitString/g' $perl_out";
  my $setUpperLimit = "perl -p -i -e 's/$njobString/$upperLimitString/g' $perl_out";

  system($setLowerLimit);
  system($setUpperLimit);
  
  my $new_dir = "dir_".$i."Convert";
  my $mkdir = "mkdir $new_dir";
  my $mvFile = "mv $perl_out $new_dir";

  
  system($mkdir);
  system($mvFile);


  $lowerLimit = $upperLimit;
  $upperLimit = $upperLimit + $divisor;
}
