package com.xebialabs.patchformatter;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.FileSystems.getDefault;
import static org.eclipse.jgit.util.FileUtils.RECURSIVE;
import static org.eclipse.jgit.util.FileUtils.delete;

/**
 * Created by sameer on 19/2/16.
 */
public class GitHubUtils {

    public static final String REPO_HOME = "/home/sameer/Refactoring";

    private static final String REMOTE_URL = "https://github.com/sameer11sep/Refactoring";

    Git git;

    public GitHubUtils(String branchName) {
        try {
            this.git = Git.cloneRepository()
                    .setURI(REMOTE_URL)
                    .setDirectory(new File(REPO_HOME))
                    .setBranch(branchName)
                    .call();
        } catch (GitAPIException e) {
            throw new IllegalStateException("Cannot initialize Git repo");
        }
    }

    public String readFile(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(REPO_HOME + "/" + path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    public void replaceFileContents(String fileName, String s) throws IOException {
        Path path = getDefault().getPath(REPO_HOME + "/" + fileName + ".tmp");
        Files.write(path, s.getBytes());
        Files.delete(FileSystems.getDefault().getPath(REPO_HOME + "/" + fileName));
        Files.move(path, getDefault().getPath(REPO_HOME + "/" + fileName));
    }

    public void commitFile(String fileName) throws GitAPIException {
        git.add().addFilepattern(fileName).call();
        git.commit().setMessage("Updated file formatting").call();
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider("sameer11sep", System.getenv("GIT_PASSWORD"))).call();
    }

    public void removeRepo() {
        try {
            delete(new File(REPO_HOME), RECURSIVE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
