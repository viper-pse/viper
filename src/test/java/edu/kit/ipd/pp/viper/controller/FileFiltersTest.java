package edu.kit.ipd.pp.viper.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.junit.Test;

public class FileFiltersTest {
    /**
     * Tests the Prolog filter for proper filtering.
     */
    @Test
    public void testPrologFilter() {
        FileFilter filter = FileFilters.PL_FILTER;
        assertTrue(filter.getDescription().equals(LanguageManager.getInstance().getString(LanguageKey.PROLOG_FILES)));

        assertTrue(filter.accept(new File("test.pl")));
        assertTrue(filter.accept(new File("test.PL")));
        assertTrue(filter.accept(new File("test.pL")));

        assertFalse(filter.accept(new File("test")));
        assertFalse(filter.accept(new File("test.svg")));
        assertFalse(filter.accept(new File("test.tikz")));
        assertFalse(filter.accept(new File("test.png")));
        assertFalse(filter.accept(new File("test.txt")));
    }

    /**
     * Tests the TikZ filter for proper filtering.
     */
    @Test
    public void testTikZFilter() {
        FileFilter filter = FileFilters.TIKZ_FILTER;
        assertTrue(filter.getDescription().equals(LanguageManager.getInstance().getString(LanguageKey.TIKZ_FILES)));

        assertTrue(filter.accept(new File("test.tikz")));
        assertTrue(filter.accept(new File("test.TIKZ")));
        assertTrue(filter.accept(new File("test.tiKz")));

        assertFalse(filter.accept(new File("test")));
        assertFalse(filter.accept(new File("test.svg")));
        assertFalse(filter.accept(new File("test.pl")));
        assertFalse(filter.accept(new File("test.png")));
        assertFalse(filter.accept(new File("test.txt")));
    }

    /**
     * Tests the PNG filter for proper filtering.
     */
    @Test
    public void testPNGFilter() {
        FileFilter filter = FileFilters.PNG_FILTER;
        assertTrue(filter.getDescription().equals(LanguageManager.getInstance().getString(LanguageKey.PNG_FILES)));

        assertTrue(filter.accept(new File("test.png")));
        assertTrue(filter.accept(new File("test.PNG")));
        assertTrue(filter.accept(new File("test.pNg")));

        assertFalse(filter.accept(new File("test")));
        assertFalse(filter.accept(new File("test.svg")));
        assertFalse(filter.accept(new File("test.pl")));
        assertFalse(filter.accept(new File("test.tikz")));
    }

    /**
     * Tests the SVG filter for proper filtering.
     */
    @Test
    public void testSVGFilter() {
        FileFilter filter = FileFilters.SVG_FILTER;
        assertTrue(filter.getDescription().equals(LanguageManager.getInstance().getString(LanguageKey.SVG_FILES)));

        assertTrue(filter.accept(new File("test.svg")));
        assertTrue(filter.accept(new File("test.SVG")));
        assertTrue(filter.accept(new File("test.sVg")));

        assertFalse(filter.accept(new File("test")));
        assertFalse(filter.accept(new File("test.png")));
        assertFalse(filter.accept(new File("test.pl")));
        assertFalse(filter.accept(new File("test.tikz")));
    }
}
