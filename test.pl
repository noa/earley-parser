#!/usr/bin/perl

my $grammar = "wallstreet";
my $sentences = "empty";

my $cmd = "";
$cmd .= "time ";
$cmd .= " ./parse.sh $grammar $sentences ";
$cmd .= " > ./output/$grammar-$sentences.out"
print $cmd;
system $cmd;

