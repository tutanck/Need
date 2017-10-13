package com.aj.need.domain.components.ads;

class Ad {

    String search;
    String ownerID;
    String updatedAt;

    public Ad(String search, String ownerID, String updatedAt) {
        this.search = search;
        this.ownerID = ownerID;
        this.updatedAt = updatedAt;
    }


    public String getSearch() {
        return search;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return search+" "+ownerID+" "+updatedAt;
    }
}