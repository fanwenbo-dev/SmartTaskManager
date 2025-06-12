package com.sp.smarttaskmanagerv2;

public class Note {
    private long id;
    private String title;
    private String description;
    private String category;
    private String additionalNote;

    public Note(long id, String title, String description, String category, String additionalNote) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.additionalNote = additionalNote;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }
}