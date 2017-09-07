#!/apps/bin/perl -w
### setCLARA.csh /work/clas12/mkunkel/myClara
#Script to run clara with decoded files
#Written by: Michael C. Kunkel / m.kunkel@fz-juelich.de

use strict;
use warnings;

####Some environment book keeping
print "CLARA_HOME is set to be $ENV{'CLARA_HOME'}\n";
print "COATJAVA is set to be $ENV{'COATJAVA'}\n";
print "CLAS12DIR is set to be $ENV{'CLAS12DIR'}\n";
print "\n";
############
my $submit_dir = "/volatile/clas12/mkunkel/EtaPrimeDilepton";
my $userName = "mkunkel";

my $claraSERVICE = "$ENV{'COATJAVA'}/config/services.yaml"; #Location of the service files i.e. the jars

print "$claraSERVICE\n";

my $nJobs = 469;	# total number of jobs 469
my @torusValue = ("-0.75", "0.75", "1.0", "-1.0");
my @solenoidValue = ("0.6", "0.8");
my $session = 0;
#interate through torus
for $a (0 .. $#torusValue)
{
  #interate through solenoid
  for $b (0 .. $#solenoidValue){
    my $torusSol_dir = "Torus".$torusValue[$a]."Sol".$solenoidValue[$b];
    my $decoded_dir = "$submit_dir/DecodedFiles/$torusSol_dir"; #Location of your decoded hipo-files
    my $recon_dir = "$submit_dir/ReconstructedFiles/$torusSol_dir";#Location where the reconstructed hipo-file should be written
    
    #lets get the clara list
    my $claraList = "fileList".$torusValue[$a]."Sol".$solenoidValue[$b];
    
    open my $clara_file, ">$claraList.list" or die "cannot open $claraList.list file:$!";
    
    #lets make a fileList with values not already reconstructed
    my $iJob = 0;
    while($iJob < $nJobs){
      my $decodedData = "EtaPrimeDilepton_Tor".$torusValue[$a]."Sol".$solenoidValue[$b]."_".$iJob.".hipo";
      my $decoded_out = "$decoded_dir/$decodedData";
      my $reconData = "out_EtaPrimeDilepton_Tor".$torusValue[$a]."Sol".$solenoidValue[$b]."_".$iJob.".hipo";
      my $recon_out = "$recon_dir/$reconData";
      
      if(-e $decoded_out){
        
        if(-e $recon_out){
          #print "YO dumbass, are you overwriting an existing file? Tsk Tsk!";
          $iJob++;
          next;
        }
        print $clara_file "$decodedData\n";
      }
      $iJob++;
    }#end of while loop
    close $clara_file;
    #system("ls $decoded_dir/* > $claraList.list");
    
    #Done making list
    
    my $claraCOOK = $claraList."_cook.clara";
    open my $get_file, ">$claraCOOK" or die "cannot open $claraCOOK file:$!";
    
    print $get_file "set inputDir $decoded_dir\n";
    print $get_file "set outputDir $recon_dir\n";
    print $get_file "\n";
    
    print $get_file "set farm.disk 5\n";
    print $get_file "set farm.time 1440\n";
    print $get_file "set farm.os centos7\n";
    print $get_file "set farm.track analysis\n";
    print $get_file "set farm.memory 30\n";
    print $get_file "set farm.cpu 32\n";
    print $get_file "set farm.scaling 4\n";
    print $get_file "set threads 32\n";
    print $get_file "set javaMemory 2\n";
    
    print $get_file "\n";
    
    print $get_file "set session s_".$userName."_etaprime_".$session."_rec\n";
    print $get_file "set description d_".$userName."_etaprime_".$session."_rec\n";
    print $get_file "set fileList $submit_dir/ReconstructedFiles/$claraList.list\n";
    print $get_file "set servicesFile $claraSERVICE\n";
    
    print $get_file "run farm\n";
    
    close $get_file;
    
    my $runClara = "$ENV{'CLARA_HOME'}/bin/clara-shell $claraCOOK";
    #system("$runClara");
    print "$runClara\n";
    $session++;
    #sleep(60);
  }#end of solenoid loop
}#end of torus loops
