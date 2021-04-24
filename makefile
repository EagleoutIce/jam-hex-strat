VERSION := $(shell cat src/main/resources/version.info)
.PHONY = all build version

all: build version

build:
	mvn clean install

version:
	@echo $(shell echo $$((${VERSION}+1))) > src/main/resources/version.info
	@echo "Bumping from ${VERSION}"
