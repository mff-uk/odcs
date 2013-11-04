#!/bin/bash

script=`readlink -f $0`
basedir=`dirname $script`
dpusrcdir="${basedir}/../../DPUs"
dputargetdir="${basedir}/../target/dpu"

# override DPU source path if it was specified in argument
if [ -n "$1" ]; then
	dpusrcdir="$1"
fi

find "${basedir}/../../DPUs/" -iname "*.jar" | \
	grep /target/ | while read f; do
		dir=`echo "$f" | grep -o "[^/]*$" | grep -o "^[_a-zA-Z0-9]*"`
		mkdir -p "${dputargetdir}/${dir}"
		cp "$f" "${dputargetdir}/${dir}"
	done
