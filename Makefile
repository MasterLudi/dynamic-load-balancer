# Program packages and files
PACKAGES = gui host job runner controller

# Java compiler
JAVAC = javac
JVM = 1.8

# .jar package
JAR_PKG = dlb.jar

# entry point
ENTRY = runner/ServerRunner.java

# Directory for compiled binaries (ex) ./bin/
BIN = bin/

# Directory of source files (ex) ./src/
SRC = src/

# Java compiler flags
JAVAFLAGS = -g -d $(BIN) -cp $(SRC) -target $(JVM)

# Creating a .class file
COMPILE = $(JAVAC) $(JAVAFLAGS)

EMPTY =

JAVA_FILES = $(subst $(SRC), $(EMPTY), $(wildcard $(SRC)*.java))

ifdef PACKAGES
PACKAGEDIRS = $(addprefix $(SRC), $(PACKAGES))
PACKAGEFILES = $(subst $(SRC), $(EMPTY), $(foreach DIR, $(PACKAGEDIRS), $(wildcard $(DIR)/*.java)))
ALL_FILES = $(PACKAGEFILES) $(JAVA_FILES)
else
#ALL_FILES = $(wildcard $(SRC).java)
ALL_FILES = $(JAVA_FILES)
endif

# One of these should be the "main" class listed in Runfile
# CLASS_FILES = $(subst $(SRC), $(BIN), $(ALL_FILES:.java=.class))
CLASS_FILES = $(ALL_FILES:.java=.class)

all : $(addprefix $(BIN), $(CLASS_FILES))

# The line describing the action starts with <TAB>
$(BIN)%.class : $(SRC)%.java
	$(COMPILE) $<

clean :
	rm -rf $(BIN)*

jar :
	jar cvfe $(JAR_PKG) $(ENTRY) -C bin .