package src.main.dev.m8u.kitpo;

import org.json.JSONArray;
import org.json.JSONObject;
import src.main.dev.m8u.kitpo.builders.MyHashableBuilder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Vector;
import java.util.function.Consumer;


public class ChainedHashtable implements Iterable<ChainedHashtable.Chain> {

    public static final int INITIAL_SIZE = 4;

    private final String keyTypeName;

    private Vector<Chain> map;

    public ChainedHashtable(String keyTypeName) {
        this.keyTypeName = keyTypeName;
        map = new Vector<>();
        map.setSize(INITIAL_SIZE);
        for (int i = 0; i < INITIAL_SIZE; i++) {
            map.set(i, new Chain());
        }
    }

    public boolean set(Object key, Object value) {
        return this.map.get(calcIndex(key)).set(key, value, this::expand);
    }

    public Object get(Object key) {
        return this.map.get(calcIndex(key)).get(key);
    }

    public Object remove(Object key) {
        return this.map.get(calcIndex(key)).remove(key);
    }

    private int calcIndex(Object key) {
        return Math.abs(key.hashCode() % map.size());
    }

    void expand() {
        Vector<Chain> old = this.map;
        this.map = new Vector<>();
        this.map.setSize(old.size() * 2);
        for (int i = 0; i < this.map.size(); i++) {
            map.set(i, new Chain());
        }
        for (Chain chain : old) {
            for (ChainNode entry : chain) {
                this.set(entry.key, entry.value);
            }
        }
    }

    public int getCapacity() {
        return this.map.size();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public Iterator<Chain> iterator() {
        return new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < map.size();
            }

            @Override
            public Chain next() {
                return map.get(i++);
            }
        };
    }

    @Override
    public void forEach(Consumer<? super Chain> action) {
        Iterable.super.forEach(action);
    }

    public void saveAsJSON(FileOutputStream fos) throws IOException {
        String json = "{\"keyType\": \"%s\",\"hashtable\": %s}";
        JSONObject jsonHashtable = new JSONObject();
        jsonHashtable.put("size", this.getCapacity());
        JSONArray jsonHashtableData = new JSONArray();
        for (Chain chain : this) {
            JSONObject jsonChain = new JSONObject();
            JSONArray jsonChainData = new JSONArray();
            int nodeCount = 0;
            for (ChainNode node : chain) {
                JSONObject jsonNode = new JSONObject();
                jsonNode.put("key", node.key.toString());
                jsonNode.put("value", node.value.toString());
                jsonChainData.put(jsonNode);
                nodeCount++;
            }
            jsonChain.put("size", nodeCount);
            jsonChain.put("data", jsonChainData);
            jsonHashtableData.put(jsonChain);
        }
        jsonHashtable.put("data", jsonHashtableData);
        json = String.format(json, this.keyTypeName, jsonHashtable);
        fos.write(json.getBytes());
    }

    public static ChainedHashtable loadFromJSON(FileInputStream fis) throws Exception {
        byte[] bytes = fis.readAllBytes();
        String json = new String(bytes, StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(json);

        String keyTypeName = jsonObject.get("keyType").toString();
        MyHashableBuilder keyBuilder = TypeFactory.getBuilderByName(keyTypeName);
        ChainedHashtable hashtable = new ChainedHashtable(keyTypeName);
        JSONObject jsonHashtable = jsonObject.getJSONObject("hashtable");
        int size = jsonHashtable.getInt("size");
        JSONArray jsonHashtableData = jsonHashtable.getJSONArray("data");
        for (int i = 0; i < size; i++) {
            JSONObject jsonChain = jsonHashtableData.getJSONObject(i);
            int nodesCount = jsonChain.getInt("size");
            JSONArray jsonChainData = jsonChain.getJSONArray("data");
            for (int j = 0; j < nodesCount; j++) {
                JSONObject jsonNode = jsonChainData.getJSONObject(j);
                hashtable.set(keyBuilder.parse((String) jsonNode.get("key")), jsonNode.get("value"));
            }
        }
        return hashtable;
    }

    public String getKeyTypeName() {
        return this.keyTypeName;
    }

    public double getAverageChainLength() {
        double avgChainLength = 0;
        int length;
        for (Chain c : this) {
            length = 0;
            for (ChainNode ignored : c) length++;
            avgChainLength += length;
        }
        avgChainLength /= this.getCapacity();
        return avgChainLength;
    }

    public double getOccupancy() {
        double nonEmptyCount = 0;
        for (Chain c : this)
            for (ChainNode ignored : c) {
                nonEmptyCount++;
                break;
            }
        return nonEmptyCount / this.getCapacity();
    }

    public class Chain implements Iterable<ChainNode> {

        public static final int CHAIN_MAX_LENGTH = 5;

        ChainNode head;

        Chain() {
        }

        boolean set(Object key, Object value, expandable hashmap) {
            if (this.head == null) {
                this.head = new ChainNode(key, value);
                return false;
            }
            if (this.head.key.equals(key)) {
                this.head.value = value;
                return false;
            }
            ChainNode node = this.head;
            int len = 2;
            while (node.next != null) {
                if (node.next.key.equals(key)) {
                    node.next.value = value;
                    return false;
                }
                node = node.next;
                len++;
            }
            node.next = new ChainNode(key, value);

            if (len > CHAIN_MAX_LENGTH) {
                hashmap.expand();
                return true;
            }
            return false;
        }

        Object get(Object key) {
            ChainNode node = this.head;
            while (node != null) {
                if (node.key.equals(key)) {
                    return node.value;
                }
                node = node.next;
            }
            return null;
        }

        Object remove(Object key) {
            ChainNode node = this.head, prev = null;
            while (node != null) {
                if (node.key.equals(key)) {
                    break;
                }
                prev = node;
                node = node.next;
            }
            if (node == null) {
                return null;
            }
            if (prev == null) {
                this.head = this.head.next;
            } else {
                prev.next = node.next;
            }
            return node.value;
        }

        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            ChainNode node = this.head;
            while (node != null && node.key != null) {
                str.append("{" + node.key + ":" + node.value + "}");
                node = node.next;
            }
            return str.toString();
        }

        @Override
        public Iterator<ChainNode> iterator() {
            return new Iterator<>() {
                ChainNode current = head;

                @Override
                public boolean hasNext() {
                    return current != null;
                }

                @Override
                public ChainNode next() {
                    ChainNode currentRef = current;
                    current = current.next;
                    return currentRef;
                }
            };
        }

        @Override
        public void forEach(Consumer<? super ChainNode> action) {
            Iterable.super.forEach(action);
        }
    }

    public class ChainNode {
        Object key;
        Object value;
        ChainNode next;

        ChainNode(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }
    }
}

interface expandable {
    void expand();
}
