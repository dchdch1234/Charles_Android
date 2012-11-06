package com.jerry.lily;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jerry.utils.DatabaseDealer;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AlphabetIndexer;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class AllBoard extends ListActivity{
	private List<String> allBoardList;
	private Button back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.allboard);
		initComponents();
	}
	@SuppressWarnings("unchecked")
	private void initComponents() {
		back = (Button)findViewById(R.id.ab_back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		allBoardList = DatabaseDealer.getAllBoardList(this);
		Collections.sort(allBoardList,new MyComparator());
		setListAdapter(new IndexAdapter<String>(this,R.layout.list_board_name,allBoardList));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String fullName = allBoardList.get(position);
		String boardName = fullName.substring(0, fullName.indexOf("("));
		Intent intent = new Intent(this, BoardList.class);
		intent.putExtra("boardName", boardName);
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}

	@SuppressWarnings("hiding")
	class IndexAdapter<String> extends ArrayAdapter<String> implements SectionIndexer{

		private AlphabetIndexer alphabetIndexer;

		public IndexAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId,objects);
			alphabetIndexer = new AlphabetIndexer(DatabaseDealer.getAllBoardCursor(AllBoard.this), 0, "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		}

		@Override
		public Object[] getSections() {
			return alphabetIndexer.getSections();
		}

		@Override
		public int getPositionForSection(int section) {
			return alphabetIndexer.getPositionForSection(section);
		}

		@Override
		public int getSectionForPosition(int position) {
			return alphabetIndexer.getSectionForPosition(position);
		}
	}

	@SuppressWarnings("rawtypes")
	class MyComparator implements Comparator{ 
		public MyComparator(){ 
			super(); 
		} 

		public int compare(Object o1, Object o2)   { 
			String stringA = (String)o1; 
			String stringB = (String)o2; 
			return (stringA.toUpperCase()).compareTo(stringB.toUpperCase()); 
		} 

	}

}
