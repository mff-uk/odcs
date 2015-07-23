#*******************************************************************************
# This file is part of UnifiedViews.
#
# UnifiedViews is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# UnifiedViews is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
#*******************************************************************************
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
