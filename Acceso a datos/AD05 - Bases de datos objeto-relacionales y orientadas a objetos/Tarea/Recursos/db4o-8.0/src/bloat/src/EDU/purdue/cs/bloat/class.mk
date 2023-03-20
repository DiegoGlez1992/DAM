# All files in the distribution of BLOAT (Bytecode Level Optimization and
# Analysis tool for Java(tm)) are Copyright 1997-2001 by the Purdue
# Research Foundation of Purdue University.  All rights reserved.
# 
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

.SUFFIXES: .java .class

JAVA_HOME = /gcm/where/jdk/1.3/sparc.Solaris
JAVAC = $(JAVA_HOME)/bin/javac
JFLAGS = -g
CLASSPATH = $(JAVA_HOME)/lib/classes.zip

all: class

clean:
	rm -f *.class

class:
	@files=`$(MAKE) -n _class | grep $(JAVAC) | cut -d' ' -f4`; \
	cpath=$(CLASSPATH):`(cd ../../../../..; pwd)`; \
	if [ "x$$files" != "x" ]; then \
	    echo $(JAVAC) $(JFLAGS) -classpath $$cpath $$files; \
	    $(JAVAC) $(JFLAGS) -classpath $$cpath $$files; \
	fi

_class: $(CLASS)

.java.class:
	cpath=$(CLASSPATH):`(cd ../../../../..; pwd)`; \
	$(JAVAC) -classpath $$cpath $<
