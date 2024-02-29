package com.example.komplex;

public class Node {
    private String name;
    private float x;
    private float y;
    private float distanceTravelled;
    private float heuristicDistance;

    public Node(String name, float x, float y, float distanceTravelled, float heuristicDistance) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.distanceTravelled = distanceTravelled;
        this.heuristicDistance = heuristicDistance;
    }



    @Override
    public String toString() {
        return "Node [name=" + name + ", x=" + x + ", y=" + y + ", distanceTravelled=" + distanceTravelled
                + ", heuristicDistance=" + heuristicDistance + "]";
    }

    public void copyNode(Node origin){
        this.name = origin.getName();
        this.x = origin.getX();
        this.y = origin.getY();
        this.distanceTravelled = origin.getDistanceTravelled();
        this.heuristicDistance = origin.getHeuristicDistance();
    }


    public String getName() {
        return name;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDistanceTravelled() {
        return distanceTravelled;
    }

    public float getHeuristicDistance() {
        return heuristicDistance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setDistanceTravelled(float distance) {
        this.distanceTravelled = distance;
    }

    public void setHeuristicDistance(float heuristicDistance) {
        this.heuristicDistance = heuristicDistance;
    }
}
