#!/usr/bin/perl

my $data = "./data";
my $grammar = "arith.gr";
my $sentences = "arith.sen";


my $cmd = "./run $data/$grammar $data/$sentences recognize";
print $cmd;
system $cmd;

