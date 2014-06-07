package com.xshare.xshareclient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class FileSelectActivity extends ListActivity {
	private List<String> items = null;
	private List<String> paths = null;
	private String rootPath = "/";
	private String currentRootString=".";
	private String curPath = "/";
	private TextView mPath;

	private final static String tagString = "FileSelectActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_file_select);
		mPath = (TextView) findViewById(R.id.mPath);

		rootPath = getSDPath();
		getFileDir(rootPath);
	}

	private String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			return sdDir.toString();
		}
		return "/";
	}

	// List files and subdirs in filePath
	private void getFileDir(String filePath) {

		mPath.setText(filePath);
		currentRootString = filePath;
		items = new ArrayList<String>();
		paths = new ArrayList<String>();

		File f = new File(filePath);
		Log.d(tagString, "filepath:" + f.getAbsolutePath());
		File[] files = f.listFiles();

		if (!filePath.equals(rootPath)) {
			items.add("b1");
			paths.add(rootPath);
			items.add("b2");
			paths.add(f.getParent());
		}
		if (files != null) {
			for (File file : files) {
				items.add(file.getName());
				paths.add(file.getPath());
			}
		}
		setListAdapter(new MyAdapter(this, items, paths));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		curPath = paths.get(position);
		File file = new File(curPath);
		if (file.isDirectory()) {
			getFileDir(paths.get(position));
		} else {
			Intent data = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("file", paths.get(position));
			data.putExtras(bundle);
			setResult(RESULT_OK, data);
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		Log.d("PPPPP", "Press");
		Log.d("aaaa",rootPath+"  "+currentRootString);
		File f = new File(currentRootString);
		if(currentRootString.equals(rootPath)){
			this.finish();
		}else{
			getFileDir(f.getParent());
		}
		
		
	}
}