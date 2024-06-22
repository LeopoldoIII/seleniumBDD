package com.bdd.hooks;


import io.cucumber.java.After;
import io.cucumber.java.Before;
import com.bdd.core.DriverManager;

public class Hooks {

    @Before
    public void setUp() {
        DriverManager.getDriver();
    }

    @After
    public void tearDown() {
        DriverManager.closeDriver();
    }
}
