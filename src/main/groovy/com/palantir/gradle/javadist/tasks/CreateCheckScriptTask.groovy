/*
 * Copyright 2016 Palantir Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.palantir.gradle.javadist.tasks

import com.palantir.gradle.javadist.JavaDistributionPlugin
import com.palantir.gradle.javadist.util.EmitFiles
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class CreateCheckScriptTask extends BaseTask {
    CreateCheckScriptTask() {
        group = JavaDistributionPlugin.GROUP_NAME
        description = "Generates healthcheck (service/monitoring/bin/check.sh) script."
    }

    @Input
    public String getServiceName() {
        return distributionExtension().serviceName
    }

    @Input
    public Iterable<String> getCheckArgs() {
        return distributionExtension().checkArgs
    }

    @OutputFile
    public File getOutputFile() {
        return new File("${project.buildDir}/monitoring/check.sh")
    }

    @TaskAction
    void createInitScript() {
        if (!distributionExtension().checkArgs.empty) {
            EmitFiles.replaceVars(
                    JavaDistributionPlugin.class.getResourceAsStream('/check.sh'),
                    getOutputFile().toPath(),
                    ['@serviceName@': getServiceName(),
                     '@checkArgs@': getCheckArgs().iterator().join(' ')])
                    .toFile()
                    .setExecutable(true)
        }
    }
}
