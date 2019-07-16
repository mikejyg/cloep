#!/usr/bin/env bash

# prepare jars
pushd ../
./gradlew build
popd

outFile=argsParserTest.out

if [ -e $outFile ]; then rm $outFile test.log; fi

java -cp ../build/classes/java/test\;../build/libs/cloep.jar mikejyg.cloep.ArgsParserTest | tee $outFile

echo diff with golden...
diff golden/$outFile $outFile
if [ $? != 0 ]; then 
	echo golden log compare failed.
else
	echo golden log compare succeeded.
fi
