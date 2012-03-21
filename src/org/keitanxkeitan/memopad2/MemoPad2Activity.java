package org.keitanxkeitan.memopad2;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MemoPad2Activity extends ListActivity implements OnClickListener {
    
    private TextView mCurrentId;
    private EditText mEdit;
    private SimpleCursorAdapter mAdapter;

    // アクティビティの開始時にボタンを登録する
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCurrentId = (TextView)findViewById(R.id.view_id);
        mEdit = (EditText)findViewById(R.id.edit_memo);

        // ボタンを登録
        int buttons[] = {R.id.delete_button,
                R.id.modify_button,
                R.id.add_button
        };
        for (int id : buttons) {
            Button button = (Button)findViewById(id);
            button.setOnClickListener(this);
        }
        setEnabled(false);
    }

    // アクティビティがフォアグラウンドになったタイミングでデータを表示する
    @Override
    protected void onResume() {
        super.onResume();

        Cursor c = fetchAllMemo();

        // ベースクラスにカーソルのライフサイクルを管理させる
        startManagingCursor(c);

        // データベースのカラムとリストビューを関連付ける
        String[] from = new String[] { BaseColumns._ID, "memo" };
        int[] to = new int[] { R.id._id, R.id.memo_text };
        mAdapter = new SimpleCursorAdapter(
                this, R.layout.memo_row, c, from, to);
        setListAdapter(mAdapter);
    }

    // アクティビティがフォアグラウンドでなくなったら、データベースをクローズする
    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onClick(View view) {
        String id = mCurrentId.getText().toString();
        String str = mEdit.getText().toString();
        if (view.getId() == R.id.delete_button) {
            deleteMemo(id);
        } else if (view.getId() == R.id.modify_button) {
            if (str.length() != 0) {
                setMemo(id, str);
            } else {
                deleteMemo(id);
            }
        } else if (view.getId() == R.id.add_button) {
            if (str.length() != 0) {
                addMemo(str);
            }
        } else {
            Log.e("MemoPadActivity", "Invalid button id");
        }

        // カーソルを再度取り出して、リストビューを再描画する
        mCurrentId.setText("");
        mEdit.setText("");
        setEnabled(false);

        Cursor c = fetchAllMemo();
        startManagingCursor(c);
        mAdapter.changeCursor(c);
    }

    // リストがタップされた時の処理
    @Override
    protected void onListItemClick(
            ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // タップされた行のIDとメモの内容をビューに設定
        LinearLayout ll = (LinearLayout)v;
        TextView t = (TextView)ll.findViewById(R.id.memo_text);
        mEdit.setText(t.getText());
        mCurrentId.setText(Long.toString(id));
        setEnabled(true);
    }

    // 削除、変更ボタンの状態を変更する
    private void setEnabled(boolean enabled) {
        int buttons[] = {R.id.delete_button,
                R.id.modify_button,
        };
        for (int id : buttons) {
            Button button = (Button)findViewById(id);
            button.setEnabled(enabled);
        }
    }

    // すべての行のカーソルをIDの降順で取り出す
    public Cursor fetchAllMemo() {
        getIntent().setData(
                Uri.parse("content://org.keitanxkeitan.provider.memopad"));
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(getIntent().getData(),
                new String[] {BaseColumns._ID, "memo"},
                null, null, "_id DESC");
        return c;
    }

    // 指定したメモを追加する
    public void addMemo(String memo) {
        ContentValues values = new ContentValues();
        values.put("memo", memo);
        getIntent().setData(
                Uri.parse("content://org.keitanxkeitan.provider.memopad"));
        ContentResolver cr = getContentResolver();
        cr.insert(getIntent().getData(), values);
    }

    // 指定したIDの行を削除する
    public void deleteMemo(String id) {
        getIntent().setData(
                Uri.parse("content://org.keitanxkeitan.provider.memopad/items/"
                        + id));
        ContentResolver cr = getContentResolver();
        cr.delete(getIntent().getData(), null, null);
    }

    // 指定したIDの行のメモを更新する
    public void setMemo(String id, String memo) {
        // 更新する値を設定する
        ContentValues values = new ContentValues();
        values.put("memo", memo);

        // 行を更新する
        getIntent().setData(
                Uri.parse("content://org.keitanxkeitan.provider.memopad/items/"
                        + id));
        ContentResolver cr = getContentResolver();
        cr.update(getIntent().getData(), values, null, null);
    }

}