package com.github.fearhardcore.rtorrent;

import java.util.ArrayList;
import java.util.List;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RtorrentActivity extends Activity {
	/** Called when the activity is first created. */

	List<Torrent> tz = new ArrayList<Torrent>();
	TorrentAdapter ad;
	ListView lv;

	XMLRPCClient client;

	int itemLongSelected = -1;
	
	List<Integer> itemSelected;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		client = new XMLRPCClient("http://192.168.1.48/RPC2");

		ad = new TorrentAdapter(this, R.layout.torrent_item, tz);
		lv = ((ListView) findViewById(R.id.listView1));
		lv.setAdapter(ad);
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				itemLongSelected = (int) id;
				ad.notifyDataSetChanged();
				return true;
			}
		});
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				itemSelected.add((int) id);
				ad.notifyDataSetChanged();
			}
		});

		TorrentGetter tgetter = new TorrentGetter();

		tgetter.execute((Void[]) null);

	}

	public class TorrentGetter extends AsyncTask<Void, Void, List<Torrent>> {

		@Override
		protected List<Torrent> doInBackground(Void... params) {
			try {
				Object[] result = (Object[]) client.call("download_list");
				for (Object o : result) {
					Torrent t = new Torrent((String) o, (String) client.call(
							"d.get_name", (String) o));
					t.setStatus(((Long) client.call("d.state", (String) o))
							.intValue());
					tz.add(t);
				}
				for (Torrent t : tz)
					Log.v("xmlresult", t.toString());
			} catch (XMLRPCException e) {
				e.printStackTrace();
			}
			return tz;
		}

		@Override
		protected void onPostExecute(List<Torrent> result) {
			tz = result;
			((BaseAdapter) ad).notifyDataSetChanged();
		}
	}

	private class TorrentAdapter extends ArrayAdapter<Torrent> implements
			Adapter {

		public TorrentAdapter(Context context, int textViewResourceId,
				List<Torrent> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public int getCount() {
			return tz.size();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			View v = convertView;

			LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (position == itemLongSelected) {
				v = vi.inflate(R.layout.torrent_selected, null);
				v.findViewById(R.id.button1).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								String hash = TorrentAdapter.this.getItem(
										position).getHash();
								try {
									client.call("d.start", hash);
								} catch (XMLRPCException e) {
									e.printStackTrace();
								}
							}
						});

				v.findViewById(R.id.button2).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								String hash = TorrentAdapter.this.getItem(
										position).getHash();
								try {
									client.call("d.pause", hash);
								} catch (XMLRPCException e) {
									e.printStackTrace();
								}
							}
						});
				v.findViewById(R.id.button3).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								String hash = TorrentAdapter.this.getItem(
										position).getHash();
								try {
									client.call("d.stop", hash);
								} catch (XMLRPCException e) {
									e.printStackTrace();
								}
							}
						});
				return v;
			} else {
				v = vi.inflate(R.layout.torrent_item, null);
			}

			Torrent t = tz.get(position);
			if (t != null) {
				((TextView) v.findViewById(R.id.torrentName)).setText(t
						.getName());
				if (t.getStatus() == Torrent.STATUS_STARTED)
					((ImageView) v.findViewById(R.id.torrentStatus))
							.setImageResource(android.R.drawable.ic_media_play);
				else if (t.getStatus() == Torrent.STATUS_STOPPED)
					((ImageView) v.findViewById(R.id.torrentStatus))
							.setImageResource(android.R.drawable.ic_media_pause);

			}
			return v;
		}
	}

	@Override
	public void onBackPressed() {
		if (itemLongSelected != -1) {
			itemLongSelected = -1;
			ad.notifyDataSetChanged();
		}
	}
}