#!/apps/bin/perl -w
use strict;
use warnings;



my $file = "EtaPrimeDalitz500Files.lund";
open my $FILE_f, "<", $file or die "Cannot open $file for read :$!";

my $searchStr = "5 1 1";
my $iLine = 0;
my $iFile = 0;
my $nSkimmed = 10000;
open my $get_file, ">EtaPrimeDalitz_$iFile.lund" or die "cannot open EtaPrimeDalitz_$iFile.lund file:$!";

while (<$FILE_f>) {
  chop($_);
  my $file = $_;
  
  if (index($_, $searchStr) != -1) {
    if($iLine ==$nSkimmed){
      close $get_file;

      $iFile++;
      $iLine = 0;
      open $get_file, ">EtaPrimeDalitz_$iFile.lund" or die "cannot open EtaPrimeDalitz_$iFile.lund file:$!";


    }
    $iLine++;

  }
  print $get_file "$file \n";

  
}
my $totalFiles = $iFile +1;
print "$totalFiles lund files produced \n";
close $get_file;
close $FILE_f;
