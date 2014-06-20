package cl.wafle.mediaplayer.model;

/**
 * Created by ezepeda on 19-06-14.
 */
public class Media {
    private int id;
    private String name;
    private String url;
    private String homepage;
    private String country;
    private String language;
    private int votes;
    private int negativevotes;

    public Media() {}

    public Media(int id, String name, String url, String homepage, String country, String language, int votes, int negativevotes) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.homepage = homepage;
        this.country = country;
        this.language = language;
        this.votes = votes;
        this.negativevotes = negativevotes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getNegativevotes() {
        return negativevotes;
    }

    public void setNegativevotes(int negativevotes) {
        this.negativevotes = negativevotes;
    }
}
