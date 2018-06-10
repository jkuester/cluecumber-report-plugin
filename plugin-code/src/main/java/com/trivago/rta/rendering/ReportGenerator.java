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

package com.trivago.rta.rendering;

import com.trivago.rta.constants.PluginSettings;
import com.trivago.rta.exceptions.CluecumberPluginException;
import com.trivago.rta.filesystem.FileIO;
import com.trivago.rta.filesystem.FileSystemManager;
import com.trivago.rta.json.pojo.Element;
import com.trivago.rta.json.pojo.Report;
import com.trivago.rta.logging.CluecumberLogger;
import com.trivago.rta.properties.PropertyManager;
import com.trivago.rta.rendering.pages.pojos.DetailPageCollection;
import com.trivago.rta.rendering.pages.pojos.StartPageCollection;
import com.trivago.rta.rendering.pages.pojos.TagSummaryPageCollection;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class ReportGenerator {

    private TemplateEngine templateEngine;
    private FileIO fileIO;
    private PropertyManager propertyManager;
    private FileSystemManager fileSystemManager;
    private CluecumberLogger logger;

    @Inject
    public ReportGenerator(
            final TemplateEngine templateEngine,
            final FileIO fileIO,
            final PropertyManager propertyManager,
            final FileSystemManager fileSystemManager,
            final CluecumberLogger logger
    ) {
        this.templateEngine = templateEngine;
        this.fileIO = fileIO;
        this.propertyManager = propertyManager;
        this.fileSystemManager = fileSystemManager;
        this.logger = logger;
    }

    public void generateReport(final StartPageCollection startPageCollection) throws CluecumberPluginException {
        copyReportAssets();
        fileSystemManager.createDirectory(
                propertyManager.getGeneratedHtmlReportDirectory() + "/" + PluginSettings.PAGES_DIRECTORY);

        generateScenarioDetailPages(startPageCollection.getReports());
        generateStartPage(startPageCollection);
        generateTagSummaryPage(startPageCollection.getReports());
    }

    private void generateTagSummaryPage(final List<Report> reports) throws CluecumberPluginException {
        TagSummaryPageCollection tagSummaryPageCollection = new TagSummaryPageCollection(reports);
        fileIO.writeContentToFile(
                templateEngine.getRenderedTagSummaryPageContent(tagSummaryPageCollection),
                propertyManager.getGeneratedHtmlReportDirectory() + "/" + PluginSettings.PAGES_DIRECTORY + "/" +
                        PluginSettings.TAG_SUMMARY_PAGE_PATH + PluginSettings.HTML_FILE_EXTENSION);
    }

    private void generateStartPage(final StartPageCollection startPageCollection) throws CluecumberPluginException {
        fileIO.writeContentToFile(
                templateEngine.getRenderedStartPageContent(startPageCollection),
                propertyManager.getGeneratedHtmlReportDirectory() + "/" +
                        PluginSettings.SUITE_OVERVIEW_PAGE_PATH + PluginSettings.HTML_FILE_EXTENSION);
    }

    private void generateScenarioDetailPages(final List<Report> reports) throws CluecumberPluginException {
        DetailPageCollection detailPageCollection;
        for (Report report : reports) {
            for (Element element : report.getElements()) {
                detailPageCollection = new DetailPageCollection(element);
                fileIO.writeContentToFile(
                        templateEngine.getRenderedDetailPageContent(detailPageCollection),
                        propertyManager.getGeneratedHtmlReportDirectory() + "/" +
                                PluginSettings.PAGES_DIRECTORY + PluginSettings.SCENARIO_DETAIL_PAGE_FRAGMENT +
                                element.getScenarioIndex() + PluginSettings.HTML_FILE_EXTENSION);
            }
        }
    }

    private void copyReportAssets() throws CluecumberPluginException {
        String reportDirectory = propertyManager.getGeneratedHtmlReportDirectory();
        fileSystemManager.createDirectory(reportDirectory);
        fileSystemManager.createDirectory(reportDirectory + "/js");
        fileSystemManager.createDirectory(reportDirectory + "/css");

        // Copy CSS resources
        copyFileFromJarToFilesystem("/css/bootstrap.min.css");
        copyFileFromJarToFilesystem("/css/cluecumber.css");
        copyFileFromJarToFilesystem("/css/datatables.min.css");
        copyFileFromJarToFilesystem("/css/jquery.fancybox.min.css");
        copyFileFromJarToFilesystem("/css/dataTables.bootstrap4.min.css");

        // Copy Javascript resources
        copyFileFromJarToFilesystem("/js/jquery.min.js");
        copyFileFromJarToFilesystem("/js/bootstrap.min.js");
        copyFileFromJarToFilesystem("/js/popper.min.js");
        copyFileFromJarToFilesystem("/js/Chart.bundle.min.js");
        copyFileFromJarToFilesystem("/js/datatables.min.js");
        copyFileFromJarToFilesystem("/js/jquery.fancybox.min.js");
    }

    private void copyFileFromJarToFilesystem(final String fileName) throws CluecumberPluginException {
        fileSystemManager.exportResource(getClass(),
                PluginSettings.BASE_TEMPLATE_PATH + fileName,
                propertyManager.getGeneratedHtmlReportDirectory() + fileName);
    }
}