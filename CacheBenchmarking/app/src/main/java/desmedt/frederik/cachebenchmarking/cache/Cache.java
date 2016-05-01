package desmedt.frederik.cachebenchmarking.cache;

/**
 * Interface for every custom cache implementation.
 */
public interface Cache<K extends Comparable<K>, V> {

    V get(K key);

    void put(K key, V value);

    void remove(K key);

    int maxSize();

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
