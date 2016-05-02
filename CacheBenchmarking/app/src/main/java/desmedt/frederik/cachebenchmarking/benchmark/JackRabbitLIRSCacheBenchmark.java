package desmedt.frederik.cachebenchmarking.benchmark;

/**
 * Contains a collection of LIRS cache benchmarks, by JackRabbit, as inner classes.
 */
public class JackRabbitLIRSCacheBenchmark {

//    private static Cache<Integer, Integer> createCache(int maxSize) {
//        return new CacheLIRS<>(maxSize);
//    }
//
//    public static class RandomRead extends CacheBenchmarkConfiguration<Integer, Integer> {
//
//        private final Cache<Integer, Integer> cache;
//        private final RandomGenerator generator;
//
//        public RandomRead(int cacheSize, int lowerBound, int upperBound) {
//            super("LIRSRRead (" + cacheSize + ")", lowerBound, upperBound);
//            cache = createCache(cacheSize);
//
//            generator = new RandomGenerator(lowerBound, upperBound);
//            for (int i = 0; i < cacheSize || i < upperBound; i++) {
//                cache.put(lowerBound + i, generator.next());
//            }
//        }
//
//        @Override
//        protected boolean run(Integer key, Integer value) {
//            return cache.getIfPresent(key) != null;
//        }
//
//        @Override
//        protected Pair<Integer, Integer> generateInput() {
//            return new Pair<>(generator.next(), generator.next());
//        }
//    }
//
//    public static class Insert extends CacheBenchmarkConfiguration<Integer, Integer> {
//
//        private final Random random = new Random();
//        private final Cache<Integer, Integer> cache;
//
//        private int nextKey = 0;
//
//        public Insert(int cacheSize, int lowerBound, int upperBound) {
//            super("LIRSInsert (" + cacheSize + ")", lowerBound, upperBound);
//            cache = createCache(cacheSize);
//        }
//
//        @Override
//        protected void cleanup() {
//            nextKey = getLowerKeyBound() + random.nextInt(getUpperKeyBound());
//            cache.invalidate(nextKey);
//        }
//
//        @Override
//        public boolean run(Integer key, Integer value) {
//            cache.put(key, value);
//            return true;
//        }
//
//        @Override
//        public Pair<Integer, Integer> generateInput() {
//            return new Pair<>(nextKey, random.nextInt());
//        }
//    }
//
//    public static class Delete extends CacheBenchmarkConfiguration<Integer, Integer> {
//
//        private final Cache<Integer, Integer> cache;
//        private final RandomGenerator generator;
//        private int lastKey = 0;
//
//        public Delete(int cacheSize, int lowerBound, int upperBound) {
//            super("LIRSDelete (" + cacheSize + ")", lowerBound, upperBound);
//            cache = createCache(cacheSize);
//
//            generator = new RandomGenerator(lowerBound, upperBound);
//
//            for (int i = 0; i < upperBound - lowerBound; i++) {
//                cache.put(i + lowerBound, generator.next());
//            }
//        }
//
//        @Override
//        protected void prepare() {
//            lastKey = generator.next();
//            cache.put(lastKey, 0);
//        }
//
//        @Override
//        protected boolean run(Integer key, Integer value) {
//            cache.invalidate(key);
//            return true;
//        }
//
//        @Override
//        protected Pair<Integer, Integer> generateInput() {
//            return new Pair<>(lastKey, generator.next());
//        }
//
//        @Override
//        protected void cleanup() {
//            cache.put(lastKey, 0);
//        }
//    }
//
//    public static class Update extends CacheBenchmarkConfiguration<Integer, Integer> {
//
//        private Random random = new Random();
//        private final Cache<Integer, Integer> cache;
//        private final RandomGenerator generator;
//
//        public Update(int cacheSize, int lowerBound, int upperBound) {
//            super("LIRSUpdate (" + cacheSize + ")", lowerBound, upperBound);
//            cache = createCache(cacheSize);
//
//            generator = new RandomGenerator(lowerBound, upperBound);
//        }
//
//        @Override
//        protected boolean run(Integer key, Integer value) {
//            cache.put(key, value);
//            return true;
//        }
//
//        @Override
//        protected Pair<Integer, Integer> generateInput() {
//            return new Pair<>(generator.next(), random.nextInt());
//        }
//    }
}
