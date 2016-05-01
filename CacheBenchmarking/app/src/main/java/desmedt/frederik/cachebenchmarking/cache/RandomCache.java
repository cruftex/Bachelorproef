package desmedt.frederik.cachebenchmarking.cache;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import desmedt.frederik.cachebenchmarking.cache.Cache;

/**
 * A simple random cache replacement policy implementation.
 */
public class RandomCache<K extends Comparable<K>, V> implements Cache<K, V> {

    private int maxSize;
    private LinkedList<Element<K, V>> heap = new LinkedList<>();
    private Random random = new Random();

    public RandomCache(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public V get(K key) {
        final int index = Collections.binarySearch(heap, key);
        return index < 0 ? null : heap.get(index).getValue();
    }

    @Override
    public void put(K key, V value) {
        int index = Collections.binarySearch(heap, key);
        if (index < 0) {
            if (maxSize < heap.size() + 1) {
                removeElement();
            }

            int insertIndex = -(index + 1);
            if (insertIndex == heap.size() + 1) {
                heap.add(new Element<K, V>(key, value));
            } else {
                heap.add(insertIndex, new Element<>(key, value));
            }
        } else {
            heap.get(index).setValue(value);
        }
    }

    @Override
    public void remove(K key) {
        int index = Collections.binarySearch(heap, key);
        if (index > 0) {
            heap.remove(index);
        }
    }

    private void removeElement() {
        final int index = random.nextInt(maxSize);
        heap.remove(index);
    }

    @Override
    public int maxSize() {
        return maxSize;
    }
}
