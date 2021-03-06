/*
 * Copyright 2018 trivago N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trivago.rta.json.postprocessors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.trivago.rta.json.pojo.Element;
import com.trivago.rta.json.pojo.Report;
import com.trivago.rta.logging.CluecumberLogger;
import io.gsonfire.PostProcessor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ReportPostProcessor implements PostProcessor<Report> {

    private final CluecumberLogger logger;
    private List<String> featureUris;

    @Inject
    public ReportPostProcessor(final CluecumberLogger logger) {
        this.logger = logger;
        featureUris = new ArrayList<>();
    }

    @Override
    public void postDeserialize(final Report report, final JsonElement jsonElement, final Gson gson) {
        mergeBackgroundScenarios(report);
        addFeatureIndex(report);
    }

    private void addFeatureIndex(final Report report) {
        if (report == null) return;

        String featureName = report.getName();
        if (!featureUris.contains(featureName)){
            featureUris.add(featureName);
        }
        report.setFeatureIndex(featureUris.indexOf(featureName));
    }

    private void mergeBackgroundScenarios(final Report report) {
        List<Element> cleanedUpElements = new ArrayList<>();
        Element currentBackgroundElement = null;

        for (Element element : report.getElements()) {
            if (element.getType().equalsIgnoreCase("background")) {
                currentBackgroundElement = element;
            } else {
                if (currentBackgroundElement != null) {
                    element.getSteps().addAll(0, currentBackgroundElement.getSteps());
                }
                cleanedUpElements.add(element);
            }
        }
        report.setElements(cleanedUpElements);
    }

    @Override
    public void postSerialize(final JsonElement jsonElement, final Report report, final Gson gson) {
        // not used
    }
}
