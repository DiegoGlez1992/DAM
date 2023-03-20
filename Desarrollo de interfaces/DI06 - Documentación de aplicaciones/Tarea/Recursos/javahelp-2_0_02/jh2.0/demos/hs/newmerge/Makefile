# Makefile for use with Microsoft's nmake; it assumes your Java bin directory
# is in your path, and that a CLASSPATH environment variable is defined.
#
# @(#)Makefile 1.2 07/06/98

# newmerge.jar is the JAR that contains all the help information
HELP_JAR=..\..\hsjar\newmerge.jar

# Should be conditional...
JHHOME=..\..\..\javahelp

INDEXER=$(JHHOME)\bin\jhindexer
JAR=jar

HELP_SOURCE_FILES = \
	MergeHelp.hs \
	$(HELP_META_FILES) \
	$(HELP_CONTENT_FILES) \
	$(HELP_OTHER_FILES)

HELP_META_FILES = \
	Map.jhm \
	MergeTOC.xml \
	MergeIndex.xml

HELP_CONTENT_FILES = \
	Pages\Introduction.html \
        Pages\add.html \
        Pages\main.html \
        Pages\view.html

HELP_OTHER_FILES = \
        Pages\add.jpg \
        Pages\view.jpg \
        Pages\hg_proc.gif

HELP_SEARCH_FILES = \
	JavaHelpSearch\DOCS \
	JavaHelpSearch\DOCS.TAB \
	JavaHelpSearch\OFFSETS \
	JavaHelpSearch\POSITIONS \
	JavaHelpSearch\SCHEMA \
	JavaHelpSearch\TMAP

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
	@mkdir JavaHelpSearch
	$(INDEXER) -db JavaHelpSearch -verbose $(HELP_CONTENT_FILES)

..\..\hsjar:
	mkdir ..\..\hsjar
