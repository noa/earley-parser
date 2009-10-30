#!/usr/bin/perl

my $testDir = "ep-tmp-test";
#$testDir .= "-" . time();

print "$testDir\n";
chdir "..";
system "rm -rf $testDir";
system "cp -r earley-parser $testDir";
chdir "$testDir";


my $data = "./data";
my $grammar = "wallstreet";
my $sentences = "wallstreet";

my $cmd = "";
$cmd .= "time ";
$cmd .= " ./run $data/$grammar.gr $data/$sentences.sen ";
#$cmd .= " parse -debug ";
$cmd .= " 1> ../earley-parser/output/$grammar-$sentences.out  2>&1";
print $cmd;
system $cmd;


