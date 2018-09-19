#! /bin/bash

SECOR_INSTALL_DIR="./bin"

if [ -e ${SECOR_INSTALL_DIR} ]; then
    rm -fr ${SECOR_INSTALL_DIR}
fi

# remove data but not the logs
if [ -e "/tmp/data/secor/secor_data/message_logs/partition" ]; then
    rm -fr /tmp/data/secor/secor_data/message_logs/partition
fi
mkdir -p /tmp/data/secor/secor_data/message_logs/partition
mkdir -p /tmp/log/secor/

mkdir ${SECOR_INSTALL_DIR}
tar -zxvf target/secor-0.26-bin.tar.gz -C ${SECOR_INSTALL_DIR}

# remove default config files
rm -f ${SECOR_INSTALL_DIR}/*.properties

tar -zxvf config.tar.gz -C ${SECOR_INSTALL_DIR}
