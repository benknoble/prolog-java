.POSIX:
SHELL = /bin/sh
.SUFFIXES: .java .class .g4

DOWNLOAD = curl

LIB = lib

ANTLR_VERSION = 4.8-complete
ANTLR = antlr-$(ANTLR_VERSION).jar
ANTLR_URL = https://www.antlr.org/download/$(ANTLR)

BUILD = build

GRAMMAR = prolog.g4
PROLOG_URL = https://raw.githubusercontent.com/antlr/grammars-v4/master/prolog/prolog.g4

all:

$(LIB)/$(ANTLR):
	if ! test -d "$$(dirname '$@')"; then mkdir "$$(dirname '$@')"; fi
	$(DOWNLOAD) $(DOWNLOADFLAGS) '$(ANTLR_URL)' > '$@'

prolog.g4:
	$(DOWNLOAD) $(DOWNLOADFLAGS) '$(PROLOG_URL)' > '$@'

clean:
	-$(RM) -r '$(BUILD)'

distclean: clean
	-git clean -fxd
