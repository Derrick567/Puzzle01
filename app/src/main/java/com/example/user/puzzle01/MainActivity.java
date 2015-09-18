package com.example.user.puzzle01;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.user.puzzle01.com.example.user.view.GamePuzzleLayout;

public class MainActivity extends AppCompatActivity {


    private GamePuzzleLayout mGamePuzzleLayout;
    private TextView tvLevel;
    private TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGamePuzzleLayout = (GamePuzzleLayout) findViewById(R.id.game_puzzle_layout);

        tvLevel = (TextView) findViewById(R.id.tvLevel);
        tvTime = (TextView) findViewById(R.id.tvTime);

        mGamePuzzleLayout.setTimeEnable(true);

        mGamePuzzleLayout.setOnGamePuzzleListener(new GamePuzzleLayout.GamePuzzleListener() {
            @Override
            public void nextLevel(final int nextLevel) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Game info ")
                        .setMessage("Level up")
                        .setPositiveButton("NEXT LEVEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mGamePuzzleLayout.nextLevel();
                                tvLevel.setText("" + nextLevel);
                            }
                        }).show();
            }

            @Override
            public void timeChanged(int currentTime) {
                tvTime.setText("" + currentTime);
            }

            @Override
            public void gameover() {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Game info ")
                        .setMessage("Game Over!!!")
                        .setPositiveButton("RESTART", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mGamePuzzleLayout.restart();
                            }
                        }).setNegativeButton("QUIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGamePuzzleLayout.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGamePuzzleLayout.resume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
