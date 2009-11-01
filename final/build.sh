#!/usr/local/bin/bash 
#
# Authors: Matt Gormley and Nicholas Andrews
# 

mkdir ./earleyParser/bin/
javac -d ./earleyParser/bin/ -sourcepath ./earleyParser/src/ ./earleyParser/src/cs465/ParserMain.java
