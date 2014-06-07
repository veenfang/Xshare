#!/bin/bash
NO_ARG=-1  
if [ $# -lt 1 ]  
then  
        echo "Give me a arg."  
        exit $NO_ARG  
fi  
for name in $(ldd ${1} | awk -F' ' '{print $3}')  
do  
	if [ -f "$name" ]; then  
		cp $name sos # some operation
	fi 
          
done
