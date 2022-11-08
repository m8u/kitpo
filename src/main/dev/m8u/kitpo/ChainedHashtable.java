package src.main.dev.m8u.kitpo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Vector;
import java.util.function.Consumer;


public class ChainedHashtable<K, T> implements Iterable<chain<K, T>> {

    private static final int INITIAL_SIZE = 4;

    private final String keyTypeName;
    private final String valueTypeName;

    private Vector<chain<K, T>> map;

    public ChainedHashtable(String keyTypeName, String valueTypeName) {
        this.keyTypeName = keyTypeName;
        this.valueTypeName = valueTypeName;
        map = new Vector<>();
        map.setSize(INITIAL_SIZE);
        for (int i = 0; i < INITIAL_SIZE; i++) {
            map.set(i, new chain<>());
        }
    }

    public void set(K key, T value) {
        int index = Math.abs(key.hashCode()) % map.size();
        this.map.get(index).set(key, value, this::expand);
    }

    public T get(K key) {
        int index = key.hashCode() % map.size();
        return this.map.get(index).get(key);
    }

    public T remove(K key) {
        int index = key.hashCode() % map.size();
        return this.map.get(index).remove(key);
    }

    void expand() {
        Vector<chain<K, T>> old = this.map;
        this.map = new Vector<>();
        this.map.setSize(old.size() * 2);
        for (int i = 0; i < this.map.size(); i++) {
            map.set(i, new chain<>());
        }
        for (chain<K, T> chain : old) {
            for (chainNode<K, T> entry : chain) {
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
    public Iterator<chain<K, T>> iterator() {
        return new Iterator<>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < map.size();
            }

            @Override
            public chain<K, T> next() {
                return map.get(i++);
            }
        };
    }

    @Override
    public void forEach(Consumer<? super chain<K, T>> action) {
        Iterable.super.forEach(action);
    }

    void saveAsJSON(FileOutputStream fos) throws IOException {
        String json = "{\"keyType\": \"%s\",\"valueType\": \"%s\",\"data\": %s}";
        JSONArray jsonChainNodes = new JSONArray();
        for (chain<K, T> chain : this) {
            for (chainNode<K, T> chainNode : chain) {
                JSONObject jsonChainNode = new JSONObject();
                jsonChainNode.put("key", chainNode.key);
                jsonChainNode.put("value", chainNode.value);
                jsonChainNodes.put(jsonChainNode);
            }
        }
        json = String.format(json, this.keyTypeName, this.valueTypeName, jsonChainNodes);
        fos.write(json.getBytes());
    }

    static ChainedHashtable<Object, Object> loadFromJSON(FileInputStream fis) throws Exception {
        byte[] bytes = fis.readAllBytes();
        String json = new String(bytes, StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(json);

        String keyTypeName = jsonObject.get("keyType").toString();
        String valueTypeName = jsonObject.get("valueType").toString();
        ChainedHashtableStorableBuilder keyBuilder = TypeFactory.getBuilderByName(keyTypeName);
        ChainedHashtableStorableBuilder valueBuilder = TypeFactory.getBuilderByName(valueTypeName);
        ChainedHashtable<Object, Object> hashtable = new ChainedHashtable<>(keyTypeName, valueTypeName);
        JSONArray jsonChainNodes = jsonObject.getJSONArray("data");
        for (Object chainNode : jsonChainNodes) {
            JSONObject jsonChainNode = (JSONObject) chainNode;
            hashtable.set(keyBuilder.parseValue((String) jsonChainNode.get("key")),
                    valueBuilder.parseValue((String) jsonChainNode.get("value")));
        }
        return hashtable;
    }

    public String getKeyTypeName() {
        return this.keyTypeName;
    }

    public String getValueTypeName() {
        return this.valueTypeName;
    }
}

class chain<K, T> implements Iterable<chainNode<K, T>> {

    public static final int CHAIN_MAX_LENGTH = 5;

    chainNode<K, T> head;

    chain() {
    }

    void set(K key, T value, expandable hashmap) {
        if (this.head == null) {
            this.head = new chainNode<>(key, value);
            return;
        }
        if (this.head.key.equals(key)) {
            this.head.value = value;
            return;
        }
        chainNode<K, T> node = this.head;
        int len = 2;
        while (node.next != null) {
            if (node.next.key.equals(key)) {
                node.next.value = value;
                return;
            }
            node = node.next;
            len++;
        }
        node.next = new chainNode<>(key, value);

        if (len > CHAIN_MAX_LENGTH) {
            hashmap.expand();
        }
    }

    T get(K key) {
        chainNode<K, T> node = this.head;
        while (node != null) {
            if (node.key.equals(key)) {
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    T remove(K key) {
        chainNode<K, T> node = this.head, prev = null;
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
        chainNode<K, T> node = this.head;
        while (node != null && node.key != null) {
            str.append("{" + node.key + ":" + node.value + "}");
            node = node.next;
        }
        return str.toString();
    }

    @Override
    public Iterator<chainNode<K, T>> iterator() {
        return new Iterator<>() {
            chainNode<K, T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public chainNode<K, T> next() {
                chainNode<K, T> currentRef = current;
                current = current.next;
                return currentRef;
            }
        };
    }

    @Override
    public void forEach(Consumer<? super chainNode<K, T>> action) {
        Iterable.super.forEach(action);
    }
}

class chainNode<K, T> {
    K key;
    T value;
    chainNode<K, T> next;

    chainNode(K key, T value) {
        this.key = key;
        this.value = value;
    }
}

interface expandable {
    void expand();
}
