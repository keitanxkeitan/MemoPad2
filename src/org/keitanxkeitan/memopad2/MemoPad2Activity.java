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

    // �A�N�e�B�r�e�B�̊J�n���Ƀ{�^����o�^����
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCurrentId = (TextView)findViewById(R.id.view_id);
        mEdit = (EditText)findViewById(R.id.edit_memo);

        // �{�^����o�^
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

    // �A�N�e�B�r�e�B���t�H�A�O���E���h�ɂȂ����^�C�~���O�Ńf�[�^��\������
    @Override
    protected void onResume() {
        super.onResume();

        Cursor c = fetchAllMemo();

        // �x�[�X�N���X�ɃJ�[�\���̃��C�t�T�C�N�����Ǘ�������
        startManagingCursor(c);

        // �f�[�^�x�[�X�̃J�����ƃ��X�g�r���[���֘A�t����
        String[] from = new String[] { BaseColumns._ID, "memo" };
        int[] to = new int[] { R.id._id, R.id.memo_text };
        mAdapter = new SimpleCursorAdapter(
                this, R.layout.memo_row, c, from, to);
        setListAdapter(mAdapter);
    }

    // �A�N�e�B�r�e�B���t�H�A�O���E���h�łȂ��Ȃ�����A�f�[�^�x�[�X���N���[�Y����
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

        // �J�[�\�����ēx���o���āA���X�g�r���[���ĕ`�悷��
        mCurrentId.setText("");
        mEdit.setText("");
        setEnabled(false);

        Cursor c = fetchAllMemo();
        startManagingCursor(c);
        mAdapter.changeCursor(c);
    }

    // ���X�g���^�b�v���ꂽ���̏���
    @Override
    protected void onListItemClick(
            ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // �^�b�v���ꂽ�s��ID�ƃ����̓��e���r���[�ɐݒ�
        LinearLayout ll = (LinearLayout)v;
        TextView t = (TextView)ll.findViewById(R.id.memo_text);
        mEdit.setText(t.getText());
        mCurrentId.setText(Long.toString(id));
        setEnabled(true);
    }

    // �폜�A�ύX�{�^���̏�Ԃ�ύX����
    private void setEnabled(boolean enabled) {
        int buttons[] = {R.id.delete_button,
                R.id.modify_button,
        };
        for (int id : buttons) {
            Button button = (Button)findViewById(id);
            button.setEnabled(enabled);
        }
    }

    // ���ׂĂ̍s�̃J�[�\����ID�̍~���Ŏ��o��
    public Cursor fetchAllMemo() {
        getIntent().setData(
                Uri.parse("content://org.keitanxkeitan.provider.memopad"));
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(getIntent().getData(),
                new String[] {BaseColumns._ID, "memo"},
                null, null, "_id DESC");
        return c;
    }

    // �w�肵��������ǉ�����
    public void addMemo(String memo) {
        ContentValues values = new ContentValues();
        values.put("memo", memo);
        getIntent().setData(
                Uri.parse("content://org.keitanxkeitan.provider.memopad"));
        ContentResolver cr = getContentResolver();
        cr.insert(getIntent().getData(), values);
    }

    // �w�肵��ID�̍s���폜����
    public void deleteMemo(String id) {
        getIntent().setData(
                Uri.parse("content://org.keitanxkeitan.provider.memopad/items/"
                        + id));
        ContentResolver cr = getContentResolver();
        cr.delete(getIntent().getData(), null, null);
    }

    // �w�肵��ID�̍s�̃������X�V����
    public void setMemo(String id, String memo) {
        // �X�V����l��ݒ肷��
        ContentValues values = new ContentValues();
        values.put("memo", memo);

        // �s���X�V����
        getIntent().setData(
                Uri.parse("content://org.keitanxkeitan.provider.memopad/items/"
                        + id));
        ContentResolver cr = getContentResolver();
        cr.update(getIntent().getData(), values, null, null);
    }

}