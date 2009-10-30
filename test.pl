#!/usr/bin/perl

my $data = "./data";
my $grammar = "wallstreet";
my $sentences = "two-wallstreet";

my $cmd = "";
$cmd .= "time ";
$cmd .= " ./run $data/$grammar.gr $data/$sentences.sen ";
#$cmd .= " parse -debug ";
$cmd .= " 1> ./output/$grammar-$sentences.out  2>&1";
print $cmd;
system $cmd;


