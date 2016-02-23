package com.xebialabs.patchformatter;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import com.xebialabs.patchformatter.model.Patch;
import com.xebialabs.patchformatter.model.Range;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sameer on 19/2/16.
 */
public class PatchRetriever {

    Formatter formatter = new Formatter();


    public void formatPatch(int prId) throws IOException {
        List<Patch> patches = retrievePatch(prId);
        patches.stream().forEach(p -> {
            GitHubUtils gitHubUtils = new GitHubUtils(p.getBranch());
            try {
                String inputSource = gitHubUtils.readFile(p.getFileName());
                List<com.google.common.collect.Range<Integer>> ranges = p.getLineRanges().stream().map(r -> com.google.common.collect.Range.closed(r.getStart(), r.getEnd())).collect(Collectors.toList());
                System.out.println(ranges);
                String formattedSource = formatter.formatSource(inputSource);
                gitHubUtils.replaceFileContents(p.getFileName(), formattedSource);
                gitHubUtils.commitFile(p.getFileName());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FormatterException e) {
                e.printStackTrace();
            } catch (GitAPIException e) {
                e.printStackTrace();
            } finally {
                gitHubUtils.removeRepo();
            }
        });
    }

    private List<Patch> retrievePatch(int prId) throws IOException {
        RepositoryId repositoryId = new RepositoryId("sameer11sep", "Refactoring");
        PullRequestService service = new PullRequestService();
        final List<PullRequest> pullRequests = service.getPullRequests(repositoryId, "open");

        for (PullRequest pullRequest : pullRequests) {
            if (pullRequest.getNumber() == prId) {
                return service.getFiles(repositoryId, prId).stream().
                        map(p -> new Patch(p.getFilename(), pickLineNumbers(p.getPatch()), pullRequest.getHead().getRef()))
                        .collect(Collectors.toList());
            }
        }
        throw new IllegalArgumentException("Invalid pull request Id or Pull request has been closed already");
    }

    private List<Range> pickLineNumbers(String patch) {
        BufferedReader rd = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(patch.getBytes())));
        List<Range> lineNumbers = new ArrayList<>();
        String line = "";
        int counter = 1;
        boolean buildingRange = false;
        int start = 0;
        try {
            while ((line = rd.readLine()) != null) {
                if (line.startsWith("+")) {
                    if (buildingRange) {
                        counter++;
                        continue;
                    } else {
                        buildingRange = true;
                        start = counter + 1;
                    }
                } else {
                    if (buildingRange) {
                        lineNumbers.add(new Range(start, counter));
                        buildingRange = false;
                    }
                }
                if (!line.startsWith("-") && (!line.startsWith("@@") && !line.endsWith("@@")))
                    counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineNumbers;
    }
}
