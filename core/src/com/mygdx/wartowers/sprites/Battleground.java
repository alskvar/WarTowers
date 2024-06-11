package com.mygdx.wartowers.sprites;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Battleground {
    private final Array<Tower> towers;
    private final Map<Integer, Array<ConnectionGraph>> connections;
    private String backgroundImagePath;
    private int winner;

    public Battleground(String jsonString) {
        this.towers = new Array<>();
        this.connections = new HashMap<>();
        this.winner = -1;
        parseJson(jsonString);
    }

    private void parseJson(String jsonString) {
        Json json = new Json();
        JsonValue root = json.fromJson(null, jsonString);

        JsonValue towersJson = root.get("towers");
        for (JsonValue towerJson : towersJson) {
            int id = towerJson.getInt("id");
            int x = towerJson.getInt("x");
            int y = towerJson.getInt("y");
            int owner = towerJson.getInt("owner");
            int troops = towerJson.getInt("troops");
            int troopsType = towerJson.getInt("troopsType");
            int level = towerJson.getInt("level");
            Tower tower = new Tower(x, y, id, owner, level, troops, troopsType);
            towers.add(tower);
            connections.put(id, new Array<ConnectionGraph>());
        }

        JsonValue connectionsJson = root.get("connections");
        for (JsonValue connectionJson : connectionsJson) {
            int from = connectionJson.getInt("from");
            int to = connectionJson.getInt("to");
            int weight = connectionJson.getInt("weight");
            connections.get(from).add(new ConnectionGraph(to, weight));
            connections.get(to).add(new ConnectionGraph(from, weight));
        }

        this.backgroundImagePath = root.getString("backgroundPath", "backgroundImages/play_bg_tmp.jpg");
    }

    public void updateTowers(float dt){
        for (Tower tower : towers) {
            tower.update(dt);
        }
    }

    public PathResult getShortestPath(int fromTowerId, int toTowerId) {
        if (fromTowerId == toTowerId) {
            return new PathResult(Integer.MAX_VALUE, new Array<ConnectionGraph>());
        }

        int n = towers.size;
        int[] distances = new int[n];
        int[] previous = new int[n];
        boolean[] visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            distances[i] = Integer.MAX_VALUE;
            previous[i] = -1;
        }
        distances[fromTowerId] = 0;

        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(new Node(fromTowerId, 0));

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            int current = node.id;
            if (visited[current]) continue;
            visited[current] = true;

            for (ConnectionGraph conn : connections.get(current)) {
                if (conn.toTowerId != toTowerId &&
                        towers.get(conn.toTowerId).getOwner() != towers.get(fromTowerId).getOwner()){
                    continue;
                }
                int neighbor = conn.toTowerId;
                int newDist = distances[current] + conn.weight;
                if (newDist < distances[neighbor]) {
                    distances[neighbor] = newDist;
                    previous[neighbor] = current;
                    queue.add(new Node(neighbor, newDist));
                }
            }
        }

        if (distances[toTowerId] == Integer.MAX_VALUE) {
            return new PathResult(Integer.MAX_VALUE, new Array<ConnectionGraph>());
        }

        Array<ConnectionGraph> path = new Array<>();
        for (int at = toTowerId; at != -1; at = previous[at]) {
            if (previous[at] != -1) {
                int from = previous[at];
                path.add(new ConnectionGraph(at, distances[at] - distances[from]));
            } else{
                path.add(new ConnectionGraph(at, 0));
            }
        }
        path.reverse();

        return new PathResult(distances[toTowerId], path);
    }


    protected static class ConnectionGraph {
        int toTowerId;
        int weight;

        ConnectionGraph(int toTowerId, int weight) {
            this.toTowerId = toTowerId;
            this.weight = weight;
        }
    }

    private static class Node implements Comparable<Node> {
        int id;
        int distance;
        Node(int id, int distance) {
            this.id = id;
            this.distance = distance;
        }
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    public static class PathResult {
        int distance;
        Array<ConnectionGraph> path;

        PathResult(int distance, Array<ConnectionGraph> path) {
            this.distance = distance;
            this.path = path;
        }
        public int getDistance() {
            return distance;
        }
        public Array<ConnectionGraph> getPath() {
            return path;
        }
    }


    public Array<Tower> getTowers() {
        return towers;
    }

    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }

    public void dispose() {
        // Dispose each tower's resources
        for (Tower tower : towers) {
            tower.dispose();
        }
        // Clear the towers and connections
        towers.clear();
        connections.clear();
    }

    public boolean checkIsGameOver() {
        int ownerPlayer = -1;
        for (Tower tower : towers) {
            if (tower.getOwner() != 0) {
                if(ownerPlayer == -1) {
                    ownerPlayer = tower.getOwner();
                } else if (tower.getOwner() != ownerPlayer) {
                    return false;
                }
            }
        }
        winner = ownerPlayer;
        return true;
    }

    public int getWinner() {
        return winner;
    }
}


