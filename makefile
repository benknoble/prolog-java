.POSIX:
SHELL = /bin/sh
.SUFFIXES: .java .class .g4

DOWNLOAD = curl

LIB = lib
BUILD = build
BIN = bin

JAVA = java
JAVAC = javac
JAR = jar

ANTLR_VERSION = 4.8-complete
ANTLR = antlr-$(ANTLR_VERSION).jar
ANTLR_URL = https://www.antlr.org/download/$(ANTLR)

GRAMMAR = prolog.g4
PROLOG_URL = https://raw.githubusercontent.com/antlr/grammars-v4/master/prolog/prolog.g4
PARSER_JAVA = prologParser.java
PARSER_CLASS = prologParser.class

JAR_FILE = prolog-java.jar

all: compile
compile: $(BIN)
jar: $(JAR_FILE)

$(LIB)/$(ANTLR):
	if ! test -d "$$(dirname '$@')"; then mkdir "$$(dirname '$@')"; fi
	$(DOWNLOAD) $(DOWNLOADFLAGS) '$(ANTLR_URL)' > '$@'

$(BUILD): $(GRAMMAR) $(LIB)/$(ANTLR)
	$(JAVA) $(JAVAFLAGS) -classpath "lib/*:$(CLASSPATH)" \
		org.antlr.v4.Tool $(ANTLRFLAGS) \
		-o '$(BUILD)' '$(GRAMMAR)'

$(BIN): $(BUILD) $(LIB)/$(ANTLR)
	$(JAVAC) $(JAVACFLAGS) \
		-classpath "lib/*:$(CLASSPATH)" \
		-sourcepath '$(BUILD)' \
		-d '$(BIN)' \
		'$(BUILD)'/*.java

$(JAR_FILE): $(BIN)
	$(JAR) $(JARFLAGS) cf '$@' -C '$(BIN)' .

$(GRAMMAR):
	$(DOWNLOAD) $(DOWNLOADFLAGS) '$(PROLOG_URL)' > '$@'

clean:
	-$(RM) -r '$(BUILD)' '$(BIN)' '$(JAR_FILE)'

distclean: clean
	-git clean -fxd
