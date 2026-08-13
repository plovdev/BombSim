package com.plovdev.bombsim.controls;

@FunctionalInterface
public interface TextCorrector {
    String correct(String input);
}