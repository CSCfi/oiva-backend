#!/bin/sh

if [[ $1 == "publish" ]]; then
	read -p "Write YES to confirm new release creation: " confirmation
	if [[ $confirmation == "YES" ]]; then
		CURRENT_BRANCH=`git rev-parse --abbrev-ref HEAD`
		if [[ $CURRENT_BRANCH != "master" ]]; then
			git checkout master
		fi
		if [ -f ./pom.xml.tag ]; then
		    mvn release:clean
		fi
		git merge --no-ff -m "Merge latest development version" develop
		mvn release:prepare -Dusername=git -P dev
	else
		echo "Release preparation cancelled"
	fi
elif [[ $1 == "clean" ]]; then
	mvn release:clean
else
    echo "oiva-backend release [DRY-RUN]"
	CURRENT_BRANCH=`git rev-parse --abbrev-ref HEAD`
	if [[ $CURRENT_BRANCH != "develop" ]]; then
		git checkout develop
	fi
	if [ -f ./pom.xml.tag ]; then
	    mvn release:clean
	fi
	mvn release:prepare -DdryRun=true -Dusername=git -P dev
fi