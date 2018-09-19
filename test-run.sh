#! /bin/bash

trap cleanup 1 2 3 6

cleanup() {
    cd ..
}

cd bin

HERE=`pwd`

java -ea \
     -Dsecor_group=secor_partition \
     -Dlog4j.configuration=file://${HERE}/config/log4j.prod.properties \
     -Dconfig=file://${HERE}/config/secor.prod.partition.properties \
     -cp secor-0.26.jar:lib/* \
     com.pinterest.secor.main.ConsumerMain