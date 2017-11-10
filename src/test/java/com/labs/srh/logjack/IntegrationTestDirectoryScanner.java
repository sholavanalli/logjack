package com.labs.srh.logjack;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = App.class)
public class IntegrationTestDirectoryScanner {

    @Autowired
    private DirectoryScanner directoryScanner;

    @Before
    public void setup() throws IOException {
    }

    @After
    public void cleanup() throws IOException {
    }

    @Test
    public void testRun() throws Exception {
//        directoryScanner.run();
    }
}
