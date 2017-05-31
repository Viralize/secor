/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pinterest.secor.uploader;

import com.pinterest.secor.common.*;
import com.pinterest.secor.util.FileUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Manages uploads to an external storage using a subprocess.
 *
 * @author Maurizio Sambati (maurizio@viralize.com)
 */
public class SubprocessUploadManager extends UploadManager {
    private static final Logger LOG = LoggerFactory.getLogger(SubprocessUploadManager.class);

    protected static final ExecutorService executor = Executors.newFixedThreadPool(256);

    public SubprocessUploadManager(SecorConfig config) {
        super(config);
    }

    public Handle<?> upload(LogFilePath localPath) throws Exception {
        String prefix = FileUtil.getPrefix(localPath.getTopic(), mConfig);
        LogFilePath path = localPath.withPrefix(prefix);
        final String localLogFilename = localPath.getLogFilePath();
        final String logFileName;

        if (FileUtil.s3PathPrefixIsAltered(path.getLogFilePath(), mConfig)) {
           logFileName = localPath.withPrefix(FileUtil.getS3AlternativePrefix(mConfig)).getLogFilePath();
           LOG.info("Will upload file to alternative s3 prefix path {}", logFileName);
        }
        else {
            logFileName = path.getLogFilePath();
        }

        LOG.info("uploading file {} to {}", localLogFilename, logFileName);

        final Future<?> f = executor.submit(new Runnable() {
            @Override
            public void run() {
                String extraArgs[] = new String[] {localLogFilename, logFileName};
                ArrayList<String> commandLine = new ArrayList<String>(Arrays.asList(mConfig.getSubprocessCommand()));
                commandLine.addAll(Arrays.asList(extraArgs));
                int exitValue;

                try {
                    Process exec = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
                    exec.waitFor();
                    exitValue = exec.exitValue();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (exitValue != 0) {
                    throw new RuntimeException("Subprocess exit code was " + exitValue + "!");
                }
            }
        });

        return new FutureHandle(f);
    }
}
