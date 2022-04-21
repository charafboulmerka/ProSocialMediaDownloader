package com.f08.prosaver.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Edge implements Serializable {

    @SerializedName("node")
    private Node node;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
