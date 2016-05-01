package desmedt.frederik.cachebenchmarking.ui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import desmedt.frederik.cachebenchmarking.BenchmarkRunner;
import desmedt.frederik.cachebenchmarking.R;

public class BenchmarkActivity extends AppCompatActivity {

    private BenchmarkRunner runner;

    private ProgressBar benchmarkProgressBar;
    private TextView textCompleted;
    private ImageView checkCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchmark);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onStart() {
        super.onStart();

        benchmarkProgressBar = (ProgressBar) findViewById(R.id.benchmarkProgressBar);
        textCompleted = (TextView) findViewById(R.id.textCompleted);
        checkCompleted = (ImageView) findViewById(R.id.checkCompleted);

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                runner = new BenchmarkRunner();
                runner.runBenchmarks();

                try {
                    return runner.getBenchmarkRunnerService().awaitTermination(1, TimeUnit.DAYS);
                } catch (InterruptedException iex) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                benchmarkProgressBar.setVisibility(View.INVISIBLE);
                checkCompleted.setVisibility(View.VISIBLE);
                textCompleted.setVisibility(View.VISIBLE);
            }
        }.execute();
    }
}
