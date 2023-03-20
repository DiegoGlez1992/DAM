# Makefile for use with Microsoft's nmake; it assumes your Java bin directory
# is in your path, and that a CLASSPATH environment variable is defined.
#
# @(#)Makefile 1.2 07/06/98

# help.jar is the JAR that contains all the help information
HELP_JAR=..\..\hsjar\master.jar

# Should be conditional...
JHHOME=..\..\..\javahelp

INDEXER=$(JHHOME)\bin\jhindexer
JAR=jar

HELP_SOURCE_FILES = \
	Master.hs \
	$(HELP_META_FILES) \
	$(HELP_CONTENT_FILES) \
	$(HELP_OTHER_FILES)

HELP_META_FILES = \
	Master.jhm \
	MasterTOC.xml \
	MasterIndex.xml

HELP_CONTENT_FILES = \
	master\bean.html \
	master\builder.html \
	master\intro.html \
	master\modes.html \
	master\server.html \
	master\suite.html

HELP_OTHER_FILES = \


HELP_SEARCH_FILES = \
	MasterSearchIndex\DOCS \
	MasterSearchIndex\DOCS.TAB \
	MasterSearchIndex\OFFSETS \
	MasterSearchIndex\POSITIONS \
	MasterSearchIndex\SCHEMA \
	MasterSearchIndex\TMAP

all: $(HELP_JAR)

searchfiles: $(HELP_SEARCH_FILES)

clean:
	del $(HELP_JAR)
	del $(HELP_SEARCH_FILES)

$(HELP_JAR): $(HELP_SOURCE_FILES) $(HELP_SEARCH_FILES)
	@del /f/q $(HELP_JAR)
	$(JAR) -cvf $@ $(HELP_SOURCE_FILES) $(HELP_SEARCH_FILES)

$(HELP_SEARCH_FILES): $(HELP_CONTENT_FILES)
	@del /f/q $(HELP_SEARCH_FILES)
	@mkdir MasterSearchIndex
	$(INDEXER) -db MasterSearchIndex -verbose $(HELP_CONTENT_FILES)

..\..\hsjar:
	mkdir ..\..\hsjar
