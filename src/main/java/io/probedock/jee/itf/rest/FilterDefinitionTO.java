package io.probedock.jee.itf.rest;

/**
 * Filter definition
 *
 * @author Laurent Prevost <laurent.prevost@probedock.io>
 */
public class FilterDefinitionTO {
    private String type;
    private String text;

    public FilterDefinitionTO() {}

    public FilterDefinitionTO(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
