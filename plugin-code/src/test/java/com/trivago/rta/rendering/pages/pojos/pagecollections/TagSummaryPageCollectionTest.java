package com.trivago.rta.rendering.pages.pojos.pagecollections;

import com.trivago.rta.constants.Status;
import com.trivago.rta.json.pojo.Element;
import com.trivago.rta.json.pojo.Report;
import com.trivago.rta.json.pojo.Result;
import com.trivago.rta.json.pojo.Step;
import com.trivago.rta.json.pojo.Tag;
import com.trivago.rta.rendering.pages.pojos.ResultCount;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TagSummaryPageCollectionTest {
    private TagSummaryPageCollection tagSummaryPageCollection;

    @Test
    public void getEmptyTagStatsTest() {
        List<Report> reports = new ArrayList<>();
        tagSummaryPageCollection = new TagSummaryPageCollection(reports);
        Map<Tag, ResultCount> tagStats = tagSummaryPageCollection.getTagResultCounts();
        assertThat(tagStats.size(), is(0));
    }

    @Test
    public void getTagStatsTest() {
        tagSummaryPageCollection = new TagSummaryPageCollection(getTestReports());
        Map<Tag, ResultCount> tagStats = tagSummaryPageCollection.getTagResultCounts();
        assertThat(tagStats.size(), is(3));

        Tag tag1 = new Tag();
        tag1.setName("tag1");
        ResultCount tag1Stats = tagStats.get(tag1);
        assertThat(tag1Stats.getTotal(), is(1));
        assertThat(tag1Stats.getPassed(), is(0));
        assertThat(tag1Stats.getFailed(), is(1));
        assertThat(tag1Stats.getSkipped(), is(0));

        Tag tag2 = new Tag();
        tag2.setName("tag2");
        ResultCount tag2Stats = tagStats.get(tag2);
        assertThat(tag2Stats.getTotal(), is(2));
        assertThat(tag2Stats.getPassed(), is(1));
        assertThat(tag2Stats.getFailed(), is(1));
        assertThat(tag2Stats.getSkipped(), is(0));

        Tag tag3 = new Tag();
        tag3.setName("tag3");
        ResultCount tag3Stats = tagStats.get(tag3);
        assertThat(tag3Stats.getTotal(), is(1));
        assertThat(tag3Stats.getPassed(), is(0));
        assertThat(tag3Stats.getFailed(), is(0));
        assertThat(tag3Stats.getSkipped(), is(1));
    }

    @Test
    public void getTagResultsTest(){
        tagSummaryPageCollection = new TagSummaryPageCollection(getTestReports());
        assertThat(tagSummaryPageCollection.getTotalNumberOfTags(), is(3));
        assertThat(tagSummaryPageCollection.getTotalNumberOfFailedTags(), is(2));
        assertThat(tagSummaryPageCollection.getTotalNumberOfPassedTags(), is(1));
        assertThat(tagSummaryPageCollection.getTotalNumberOfSkippedTags(), is(1));
    }

    private List<Report> getTestReports() {
        List<Report> reports = new ArrayList<>();

        Report report = new Report();
        List<Element> elements = new ArrayList<>();

        Element element = new Element();
        List<Tag> tags = new ArrayList<>();
        Tag tag = new Tag();
        tag.setName("tag1");
        tags.add(tag);
        tag = new Tag();
        tag.setName("tag2");
        tags.add(tag);
        element.setTags(tags);
        List<Step> steps = new ArrayList<>();
        Step step = new Step();
        Result result = new Result();
        result.setStatus(Status.FAILED.getStatusString());
        step.setResult(result);
        steps.add(step);
        element.setSteps(steps);
        elements.add(element);
        report.setElements(elements);

        element = new Element();
        tags = new ArrayList<>();
        tag = new Tag();
        tag.setName("tag2");
        tags.add(tag);
        element.setTags(tags);
        steps = new ArrayList<>();
        step = new Step();
        result = new Result();
        result.setStatus(Status.PASSED.getStatusString());
        step.setResult(result);
        steps.add(step);
        element.setSteps(steps);
        elements.add(element);
        report.setElements(elements);

        element = new Element();
        tags = new ArrayList<>();
        tag = new Tag();
        tag.setName("tag3");
        tags.add(tag);
        element.setTags(tags);
        steps = new ArrayList<>();
        step = new Step();
        result = new Result();
        result.setStatus(Status.SKIPPED.getStatusString());
        step.setResult(result);
        steps.add(step);
        element.setSteps(steps);
        elements.add(element);
        report.setElements(elements);

        reports.add(report);
        return reports;
    }
}
