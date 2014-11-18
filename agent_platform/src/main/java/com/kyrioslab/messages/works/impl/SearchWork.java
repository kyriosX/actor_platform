package com.kyrioslab.messages.works.impl;

import com.kyrioslab.messages.works.Work;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wizzard on 24.09.14.
 */
public class SearchWork implements Work{

    private boolean done = false;
    private List<String> pathList;
    private String query;
    private String fromHost;


    @Override
    public void doWork() {
        Finder f = new Finder();
        f.setQuery(query);
        try {
            Files.walkFileTree(Paths.get(System.getProperty("user.home")), f);
        } catch (IOException e) {
            System.out.println("Unable to get path from: " + System.getenv("SystemRoot"));
        }
        try {
            fromHost = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.out.println("Unable to retrieve hostname");
        }
        pathList = f.getPathList();
        done = true;
    }

    @Override
    public List<String> getResult() {
        return pathList;
    }

    public String getFromHost() {
        return fromHost;
    }

    @Override
    public boolean isDone() {
        return done;
    }

        public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public static class Finder extends SimpleFileVisitor<Path> {

        List<String> pathList = new LinkedList<>();

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        private String query;

        @Override
        public FileVisitResult visitFile(Path file,
                                         BasicFileAttributes attr) {
            if (file.getFileName().toString().contains(query)) {
                pathList.add(file.getFileName().toUri().toString());
                System.out.println("Added: " + file.getFileName().toString());
            }
            return FileVisitResult.CONTINUE;
        }

        // Print each directory visited.
        @Override
        public FileVisitResult postVisitDirectory(Path dir,
                                                  IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file,
                                               IOException exc) {
            return FileVisitResult.CONTINUE;
        }

        public List<String> getPathList() {
            return pathList;
        }

        public void setPathList(List<String> pathList) {
            this.pathList = pathList;
        }
    }

}
