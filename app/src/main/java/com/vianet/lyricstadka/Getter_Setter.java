package com.vianet.lyricstadka;

/**
 * Created by editing2 on 22-Nov-17.
 */

public class Getter_Setter {
    private String id;
    private String text;
    private String head,description;

    public String getSubid() {
        return subid;
    }

    public void setSubid(String subid) {
        this.subid = subid;
    }

    private String subid;

    public Getter_Setter(){

    }
/*    public Getter_Setter(int id,String name){
        this.id=id;
        this.name=name;
    }*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String title) {
        this.head = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
