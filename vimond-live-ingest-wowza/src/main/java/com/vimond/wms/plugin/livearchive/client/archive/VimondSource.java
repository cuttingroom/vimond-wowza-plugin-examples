package com.vimond.wms.plugin.livearchive.client.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * .
 * @author Vimond Media Solution AS
 * @since 2018-03-13
 */
public class VimondSource {
// ---------------------------------------------------------------
    // STATE
    // ---------------------------------------------------------------

    private final String name;
    private final VimondArchiveInput input;
    private final VimondArchive archive;

    // ---------------------------------------------------------------
    // CONSTRUCTOR AND FACTORY METHODS
    // ---------------------------------------------------------------

    @JsonCreator
    public VimondSource(
            @JsonProperty("name") String name,
            @JsonProperty("input") VimondArchiveInput input,
            @JsonProperty("archive") VimondArchive archive
    ) {
        this.name = name;
        this.input = input;
        this.archive = archive;
    }

    // ---------------------------------------------------------------
    // CORE METHODS
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "Source{" +
                "name='" + name + '\'' +
                ", input=" + input +
                ", archive=" + archive +
                '}';
    }


    // ---------------------------------------------------------------
    // CORE METHODS
    // ---------------------------------------------------------------

    // ---------------------------------------------------------------
    // GETTERS AND SETTERS
    // ---------------------------------------------------------------

    public String getName() {
        return name;
    }

    public VimondArchiveInput getInput() {
        return input;
    }

    public VimondArchive getArchive() {
        return archive;
    }

}
