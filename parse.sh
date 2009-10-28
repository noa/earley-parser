./run data/$1.gr data/$2.sen parse > $2.txt; cat $2.txt | data/prettyprint
