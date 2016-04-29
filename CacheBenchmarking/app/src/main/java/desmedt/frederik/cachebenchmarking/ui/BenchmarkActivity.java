package desmedt.frederik.cachebenchmarking.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import desmedt.frederik.cachebenchmarking.BenchmarkRunner;
import desmedt.frederik.cachebenchmarking.R;

public class BenchmarkActivity extends AppCompatActivity {

    private BenchmarkRunner runner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchmark);
    }

    @Override
    protected void onStart() {
        super.onStart();
        runner = new BenchmarkRunner();
        runner.runBenchmarks();
    }
}
