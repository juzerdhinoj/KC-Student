package in.net.kccollege.student.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import in.net.kccollege.student.R;
import in.net.kccollege.student.model.RssEntry;

/**
 * Created by Sahil on 14-07-2017.
 */

public class NoticesAdapter extends RecyclerView.Adapter<NoticesAdapter.ViewHolder> {

	ArrayList<RssEntry> dataset;
	OnItemClickListener listener;

	public NoticesAdapter(ArrayList<RssEntry> items) {
		this.dataset = items;
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		View itemview = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.notices_row, parent, false);
		return new ViewHolder(itemview);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		RssEntry entry = dataset.get(position);
		holder.title.setText(Html.fromHtml(entry.getTitle()));
		holder.desc.setText(Html.fromHtml(entry.getDesc()));
		holder.date.setText(entry.getDate());
	}

	public void swapDataset(ArrayList<RssEntry> items) {
		dataset.clear();
		dataset.addAll(items);
		notifyDataSetChanged();

	}

	@Override
	public int getItemCount() {
		return dataset.size();
	}

	public void setOnClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	public interface OnItemClickListener {
		public void onClick(RssEntry entry);
	}

	public class ViewHolder extends RecyclerView.ViewHolder {

		private TextView title, date, desc;

		public ViewHolder(View itemView) {
			super(itemView);
			this.title = (TextView) itemView.findViewById(R.id.lblTitle);
			this.desc = (TextView) itemView.findViewById(R.id.lblDesc);
			this.date = (TextView) itemView.findViewById(R.id.lblDate);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onClick(dataset.get(getAdapterPosition()));
				}
			});
		}
	}

}
