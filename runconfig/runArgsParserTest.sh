#!/usr/bin/env bash

# prepare jars
pushd ../
./gradlew build
popd

outFile=argsParserTest.out

java -ea -cp ../build/classes/java/test:../build/resources/test:../build/libs/cloep.jar mikejyg.cloep.ArgsParserTest | tee $outFile

echo comparing with golden...
diff golden/$outFile $outFile
