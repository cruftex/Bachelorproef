package desmedt.frederik.cachebenchmarking.cache;

/**
 * Interface for every custom cache implementation.
 */
public interface Cache<K extends Comparable<K>, V> {

    /**
     * Get the value of the entry associated with the key or null if there is no entry in the cache
     * linked to the key.
     *
     * @param key The key associated with the entry
     * @return The value if the key exists in the cache, false otherwise
     */
    V get(K key);

    /**
     * Put a new entry in the cache with a key and a value.
     *
     * @param key   The key of the entry
     * @param value The value of the entry
     */
    void put(K key, V value);

    /**
     * Removes a single entry from the cache, or does nothing if the key is not present.
     *
     * @param key The key of the entry that should be removed
     */
    void remove(K key);

    /**
     * Removes all elements from the cache.
     */
    void removeAll();

    /**
     * @return The maximum amount of entries present in the cache at any given time
     */
    int maxSize();

    /**
     * @return The current amount of entries present in the cache
     */
    int size();

    class Element<K extends Comparable<K>, V> implements Comparable<K> {

        private K key;
        private V value;

        public Element(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Element) {
                return key.equals(((Element) o).getKey()) && value.equals(((Element) o).getValue());
            } else {
                return key.equals(o);
            }
        }

        @Override
        public int compareTo(K key) {
            return getKey().compareTo(key);
        }
    }
}
