#!/usr/bin/perl

my $data = "./data";
my $grammar = "arith.gr";
my $sentences = "arith.sen";


my $cmd = "java cs465.ParserMain $data/$grammar $data/$sentences parse";
#print $cmd;
system $cmd;

