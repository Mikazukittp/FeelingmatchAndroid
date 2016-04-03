package mikazuki.android.app.feelingmatch.view.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import mikazuki.android.app.feelingmatch.R;
import mikazuki.android.app.feelingmatch.model.Match;
import mikazuki.android.app.feelingmatch.model.User;

/**
 * @author haijimakazuki
 */
public class LogListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater = null;
    private List<Match> logList;

    public LogListAdapter(Context context, List<Match> logList) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.logList = logList;
    }

    @Override
    public int getCount() {
        return logList.size();
    }

    @Override
    public Match getItem(int position) {
        return logList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = layoutInflater.inflate(R.layout.list_log, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        final Match rowData = logList.get(position);
        holder.name.setText(rowData.getName());
        final String date = DateFormat.format("MM/dd hh:mm", rowData.getTime()).toString();
        holder.date.setText(date);

        Map<Long, User> boys = Maps.newHashMap();
        Map<Long, User> girls = Maps.newHashMap();
        Stream.of(rowData.getMembers()).forEach(u -> (u.isBoy() ? boys : girls).put(u.getId(), u));
        long match = Stream.of(boys).filter(b -> girls.get(b.getValue().getFavoriteUserId()).getFavoriteUserId() == b.getKey()).count();
        holder.boy.setText("男子" + boys.size() + "名");
        holder.girl.setText("女子" + girls.size() + "名");
        holder.match.setText(match + "組マッチ");
        return convertView;
    }

    public static class ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.date)
        TextView date;
        @Bind(R.id.boy)
        TextView boy;
        @Bind(R.id.girl)
        TextView girl;
        @Bind(R.id.match)
        TextView match;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
